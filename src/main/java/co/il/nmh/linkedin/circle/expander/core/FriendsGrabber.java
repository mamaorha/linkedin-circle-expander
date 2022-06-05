package co.il.nmh.linkedin.circle.expander.core;

import co.il.nmh.easy.selenium.EasySeleniumBrowser;
import co.il.nmh.easy.selenium.enums.BrowserType;
import co.il.nmh.easy.selenium.enums.MouseButton;
import co.il.nmh.easy.selenium.enums.SearchBy;
import co.il.nmh.easy.selenium.enums.WaitCondition;
import co.il.nmh.easy.selenium.exceptions.SeleniumActionTimeout;
import co.il.nmh.easy.utils.EasyThread;
import co.il.nmh.linkedin.circle.expander.core.listeners.FriendsGrabberListener;
import co.il.nmh.linkedin.circle.expander.core.steps.GetFriendDetailsStep;
import co.il.nmh.linkedin.circle.expander.core.steps.LoginStep;
import co.il.nmh.linkedin.circle.expander.data.Filter;
import co.il.nmh.linkedin.circle.expander.data.FriendDetails;
import co.il.nmh.linkedin.circle.expander.data.enums.LogTypeEnum;
import co.il.nmh.linkedin.circle.expander.data.enums.LoginStatusEnum;
import co.il.nmh.linkedin.circle.expander.properties.FriendProperties;
import co.il.nmh.linkedin.circle.expander.utils.SharedResources;
import org.openqa.selenium.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Maor Hamami
 */

public class FriendsGrabber extends EasyThread {
    private final String username;
    private final String password;
    private final Set<String> includeFilters;
    private final Set<String> excludeFilters;

    private final Set<FriendsGrabberListener> listeners;

    private EasySeleniumBrowser easySeleniumBrowser;
    private int tries;
    private boolean stoppedManually;
    private FriendDetails lastFriendDetails;
    private FriendProperties friendProperties;
    private int index;

    public FriendsGrabber(String username, String password, Set<Filter> filters) {
        super("FriendsGrabber");

        this.username = username;
        this.password = password;

        if (filters == null) filters = new HashSet<>();
        includeFilters = filters.stream().filter(Filter::isInclude).map(Filter::getValue).map(String::toLowerCase).collect(Collectors.toSet());
        excludeFilters = filters.stream().filter(f -> !f.isInclude()).map(Filter::getValue).map(String::toLowerCase).collect(Collectors.toSet());

        listeners = new HashSet<>();
    }

    public void addListener(FriendsGrabberListener listener) {
        listeners.add(listener);
    }

    @Override
    protected void init() {
        log("linkedin friends grabber initiating", LogTypeEnum.INFO);

        easySeleniumBrowser = new EasySeleniumBrowser(BrowserType.CHROME);

        tries = 0;
        lastFriendDetails = null;
        friendProperties = SharedResources.INSTANCE.getLinkedinCircleExpanderProperties().getFriend();
    }

    @Override
    public boolean loopRun() {
        try {
            LoginStatusEnum login = LoginStep.login(easySeleniumBrowser, username, password);

            switch (login) {
                case ALREADY_LOGGED_IN:
                case SUCCESS:
                    break;
                case FAILED:
                    log("login failed", LogTypeEnum.ERROR);
                    return false;
                case LOGIC_FAILURE:
                    log("login failed - logic failure", LogTypeEnum.ERROR);
                    return false;
            }

            if (isInterrupted()) {
                return false;
            }

            FriendDetails friendDetails = GetFriendDetailsStep.get(easySeleniumBrowser, lastFriendDetails, index);

            if (isInterrupted()) {
                return false;
            }

            if (null == friendDetails) {
                easySeleniumBrowser.action().sendKey(Keys.PAGE_DOWN);
                Thread.sleep(2000);
                tries++;

                if (tries == 2) {
                    refresh(easySeleniumBrowser);
                    tries = 0;
                }
            } else if (friendDetails.equals(lastFriendDetails)) {
                index++;
            } else {
                lastFriendDetails = friendDetails;

                if (shouldAddFriend(friendDetails)) {
                    if (!addFriend(easySeleniumBrowser, friendProperties, friendDetails)) {
                        tries++;

                        if (tries == 2) {
                            refresh(easySeleniumBrowser);
                            tries = 0;
                        }
                    } else {
                        tries = 0;
                    }
                }
            }

            return true;
        } catch (WebDriverException e) {
            if (e.getCause() instanceof InterruptedException) {
                log("stopped manually", LogTypeEnum.INFO);
                stoppedManually = true;
            } else if (e instanceof NoSuchWindowException || e instanceof SessionNotCreatedException) {
                log("browser window was closed manually", LogTypeEnum.ERROR);
            } else {
                log("error occurred - " + e.getMessage(), LogTypeEnum.ERROR);
            }
        } catch (Exception e) {
            log("error occurred - " + e.getMessage(), LogTypeEnum.ERROR);
        }

        return false;
    }

    private boolean shouldAddFriend(FriendDetails friendDetails) {
        if("unknown".equals(friendDetails.getName())) return false;

        if (!includeFilters.isEmpty() && includeFilters.stream().noneMatch(value -> isFilterMatch(friendDetails, value))) {
            log("ignoring suggestion '" + friendDetails.getName() + "' as it's not part of the include filters", LogTypeEnum.INFO);
            index++;

            return false;
        }

        return excludeFilters.stream().filter(value -> isFilterMatch(friendDetails, value)).findAny().map(value -> {
            log("ignoring suggestion '" + friendDetails.getName() + "' as it contains the exclude filter '" + value + "'", LogTypeEnum.INFO);
            index++;

            return false;
        }).orElse(true);
    }

    @Override
    protected void runEnded() {
        if (isInterrupted() && !stoppedManually) {
            log("stopped manually", LogTypeEnum.INFO);
        }

        if (null != easySeleniumBrowser) {
            easySeleniumBrowser.close();
        }

        for (FriendsGrabberListener friendsGrabberListener : listeners) {
            friendsGrabberListener.stopped();
        }
    }

    private void refresh(EasySeleniumBrowser easySeleniumBrowser) {
        log("refreshing page", LogTypeEnum.INFO);
        easySeleniumBrowser.navigator().refresh();
        index = 0;
    }

    private boolean isFilterMatch(FriendDetails friendDetails, String value) {
        return friendDetails.getName().toLowerCase().contains(value) ||
                friendDetails.getDescription().toLowerCase().contains(value) ||
                friendDetails.getInsight().toLowerCase().contains(value);
    }

    private boolean addFriend(EasySeleniumBrowser easySeleniumBrowser, FriendProperties friendProperties, FriendDetails friendDetails) {
        try {
            WebElement AddElement = easySeleniumBrowser.document().getElement(friendDetails.getWebElement(), SearchBy.CLASS_NAME, friendProperties.getAddClass(), 0, WaitCondition.CLICKABILITY_OF_ELEMENT, 5);
            easySeleniumBrowser.action().click(AddElement, MouseButton.LEFT);

            log("connecting suggestion '" + friendDetails + "'", LogTypeEnum.INFO);
            return true;
        } catch (SeleniumActionTimeout e) {
            return false;
        }
    }

    private void log(String message, LogTypeEnum logType) {
        for (FriendsGrabberListener friendsGrabberListener : listeners) {
            friendsGrabberListener.log(message, logType);
        }
    }
}

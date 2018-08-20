package co.il.nmh.linkedin.circle.expander.core;

import java.util.HashSet;
import java.util.Set;

import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

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
import co.il.nmh.linkedin.circle.expander.data.FriendDetials;
import co.il.nmh.linkedin.circle.expander.data.enums.LogTypeEnum;
import co.il.nmh.linkedin.circle.expander.data.enums.LoginStatusEnum;
import co.il.nmh.linkedin.circle.expander.properties.FriendProperties;
import co.il.nmh.linkedin.circle.expander.utils.SharedResources;

/**
 * @author Maor Hamami
 */

public class FriendsGrabber extends EasyThread
{
	private String username;
	private String password;
	private Set<String> filter;

	private Set<FriendsGrabberListener> listeners;

	private EasySeleniumBrowser easySeleniumBrowser;
	private int tries;
	private int sameElement;
	private boolean stoppedManually;
	private FriendDetials lastFriendDetials;
	private FriendProperties friendProperties;

	public FriendsGrabber(String username, String password, Set<String> filter)
	{
		super("FriendsGrabber");

		this.username = username;
		this.password = password;
		this.filter = filter;

		listeners = new HashSet<>();
	}

	public void addListener(FriendsGrabberListener listener)
	{
		listeners.add(listener);
	}

	@Override
	protected void init()
	{
		log("linkedin friends grabber initiating", LogTypeEnum.INFO);

		easySeleniumBrowser = new EasySeleniumBrowser(BrowserType.CHROME);

		tries = 0;
		sameElement = 0;
		lastFriendDetials = null;
		friendProperties = SharedResources.INSTANCE.getLinkedingCircleExpanderProperties().getFriend();
	}

	@Override
	public boolean loopRun() throws InterruptedException
	{
		try
		{
			LoginStatusEnum login = LoginStep.login(easySeleniumBrowser, username, password);

			switch (login)
			{
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

			if (isInterrupted())
			{
				return false;
			}

			FriendDetials friendDetials = GetFriendDetailsStep.get(easySeleniumBrowser, lastFriendDetials);

			if (isInterrupted())
			{
				return false;
			}

			if (null == friendDetials)
			{
				tries++;

				if (tries == 2)
				{
					refresh(easySeleniumBrowser);
					tries = 0;
				}
			}

			else if (friendDetials == lastFriendDetials)
			{
				sameElement++;

				if (sameElement == 10)
				{
					log("Sleeping for 10 min, probably too many requests", LogTypeEnum.INFO);
					Thread.sleep(1000 * 60 * 10);
					refresh(easySeleniumBrowser);

					sameElement = 8;
				}

				else
				{
					Thread.sleep(1000);
				}
			}

			else
			{
				lastFriendDetials = friendDetials;

				if (!handleFriend(easySeleniumBrowser, friendProperties, friendDetials))
				{
					tries++;

					if (tries == 2)
					{
						refresh(easySeleniumBrowser);
						tries = 0;
					}
				}
			}

			return true;
		}
		catch (WebDriverException e)
		{
			if (e.getCause() instanceof InterruptedException)
			{
				log("stopped manually", LogTypeEnum.INFO);
				stoppedManually = true;
			}

			else if (e instanceof NoSuchWindowException || e instanceof SessionNotCreatedException)
			{
				log("browser window was closed manually", LogTypeEnum.ERROR);
			}

			else
			{
				log("error occured - " + e.getMessage(), LogTypeEnum.ERROR);
			}
		}
		catch (Exception e)
		{
			log("error occured - " + e.getMessage(), LogTypeEnum.ERROR);
		}

		return false;
	}

	@Override
	protected void runEnded()
	{
		if (isInterrupted() && !stoppedManually)
		{
			log("stopped manually", LogTypeEnum.INFO);
		}

		if (null != easySeleniumBrowser)
		{
			easySeleniumBrowser.close();
		}

		for (FriendsGrabberListener friendsGrabberListener : listeners)
		{
			friendsGrabberListener.stopped();
		}
	}

	private void refresh(EasySeleniumBrowser easySeleniumBrowser)
	{
		log("refreshing page", LogTypeEnum.INFO);
		easySeleniumBrowser.navigator().refresh();
	}

	private boolean handleFriend(EasySeleniumBrowser easySeleniumBrowser, FriendProperties friendProperties, FriendDetials friendDetials)
	{
		String filterValidation = findFilter(friendDetials);

		if (null != filterValidation)
		{
			removeFriend(easySeleniumBrowser, friendProperties, friendDetials);
			return true;
		}

		else
		{
			return addFriend(easySeleniumBrowser, friendProperties, friendDetials);
		}
	}

	private String findFilter(FriendDetials friendDetials)
	{
		if (null != filter)
		{
			String lowerName = friendDetials.getName().toLowerCase();

			for (String currFilter : filter)
			{
				if (lowerName.contains(currFilter) || friendDetials.getDescription().contains(currFilter) || friendDetials.getInsight().contains(currFilter))
				{
					return currFilter;
				}
			}
		}

		return null;
	}

	private void removeFriend(EasySeleniumBrowser easySeleniumBrowser, FriendProperties friendProperties, FriendDetials friendDetials)
	{
		log("removing suggestion '" + friendDetials.getName() + "' as it contains the exclude filter '" + filter + "'", LogTypeEnum.INFO);
		easySeleniumBrowser.action().execJs(friendProperties.getCloseScript());
	}

	private boolean addFriend(EasySeleniumBrowser easySeleniumBrowser, FriendProperties friendProperties, FriendDetials friendDetials)
	{
		try
		{
			WebElement AddElement = easySeleniumBrowser.document().getElement(SearchBy.CLASS_NAME, friendProperties.getAddClass(), 0, WaitCondition.CLICKABILITY_OF_ELEMENT, 5);
			easySeleniumBrowser.action().click(AddElement, MouseButton.LEFT);

			log("connecting suggestion '" + friendDetials.getName() + "'", LogTypeEnum.INFO);
			return true;
		}
		catch (SeleniumActionTimeout e)
		{
			return false;
		}
	}

	private void log(String message, LogTypeEnum logType)
	{
		for (FriendsGrabberListener friendsGrabberListener : listeners)
		{
			friendsGrabberListener.log(message, logType);
		}
	}
}

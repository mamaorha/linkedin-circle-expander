package co.il.nmh.linkedin.circle.expander.core.steps;

import co.il.nmh.easy.selenium.EasySeleniumBrowser;
import co.il.nmh.easy.selenium.enums.SearchBy;
import co.il.nmh.easy.selenium.enums.WaitCondition;
import co.il.nmh.easy.selenium.exceptions.SeleniumActionTimeout;
import co.il.nmh.linkedin.circle.expander.data.FriendDetails;
import co.il.nmh.linkedin.circle.expander.properties.FriendProperties;
import co.il.nmh.linkedin.circle.expander.utils.SharedResources;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

/**
 * @author Maor Hamami
 */

public class GetFriendDetailsStep
{
	public static FriendDetails get(EasySeleniumBrowser easySeleniumBrowser, FriendDetails lastFriendDetails, int index)
	{
		FriendProperties friend = SharedResources.INSTANCE.getLinkedinCircleExpanderProperties().getFriend();

		while (true)
		{
			try
			{
				WebElement parentElement = easySeleniumBrowser.document().getElement(SearchBy.XPATH, friend.getParentXpath(), index, WaitCondition.ELEMENT_CREATION, 3);

				if (null != lastFriendDetails && lastFriendDetails.getWebElement() == parentElement)
				{
					return lastFriendDetails;
				}

				String name = getText(easySeleniumBrowser, parentElement, friend.getNameClass(), 2);
				String description = getText(easySeleniumBrowser, parentElement, friend.getDescriptionClass(), 0).toLowerCase();
				String insight = getText(easySeleniumBrowser, parentElement, friend.getInsightClass(), 0).toLowerCase();

				return new FriendDetails(parentElement, name, description, insight);
			}
			catch (StaleElementReferenceException e)
			{
			}
			catch (SeleniumActionTimeout e)
			{
				return null;
			}
		}
	}

	private static String getText(EasySeleniumBrowser easySeleniumBrowser, WebElement webElement, String className, int timeout)
	{
		try
		{
			WebElement nameElement = easySeleniumBrowser.document().getElement(webElement, SearchBy.CLASS_NAME, className, 0, WaitCondition.ELEMENT_CREATION, timeout);
			return nameElement.getText();
		}
		catch (SeleniumActionTimeout e)
		{
			return "unknown";
		}
	}
}

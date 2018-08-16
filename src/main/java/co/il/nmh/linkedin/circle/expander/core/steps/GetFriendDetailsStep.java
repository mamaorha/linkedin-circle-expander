package co.il.nmh.linkedin.circle.expander.core.steps;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import co.il.nmh.easy.selenium.EasySeleniumBrowser;
import co.il.nmh.easy.selenium.enums.SearchBy;
import co.il.nmh.easy.selenium.enums.WaitCondition;
import co.il.nmh.easy.selenium.exceptions.SeleniumActionTimeout;
import co.il.nmh.linkedin.circle.expander.data.FriendDetials;
import co.il.nmh.linkedin.circle.expander.properties.FriendProperties;
import co.il.nmh.linkedin.circle.expander.utils.SharedResources;

/**
 * @author Maor Hamami
 */

public class GetFriendDetailsStep
{
	public static FriendDetials get(EasySeleniumBrowser easySeleniumBrowser, FriendDetials lastFriendDetials)
	{
		FriendProperties friend = SharedResources.INSTANCE.getLinkedingCircleExpanderProperties().getFriend();

		while (true)
		{
			try
			{
				WebElement parentElement = easySeleniumBrowser.document().getElement(SearchBy.CLASS_NAME, friend.getParentClass(), 0, WaitCondition.ELEMENT_CREATION, 3);

				if (null != lastFriendDetials && lastFriendDetials.getWebElement() == parentElement)
				{
					return lastFriendDetials;
				}

				String name = getText(easySeleniumBrowser, parentElement, friend.getNameClass());
				String description = getText(easySeleniumBrowser, parentElement, friend.getDescriptionClass()).toLowerCase();
				String insight = getText(easySeleniumBrowser, parentElement, friend.getInsightClass()).toLowerCase();

				return new FriendDetials(parentElement, name, description, insight);
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

	private static String getText(EasySeleniumBrowser easySeleniumBrowser, WebElement webElement, String className)
	{
		try
		{
			WebElement nameElement = easySeleniumBrowser.document().getElement(webElement, SearchBy.CLASS_NAME, className, 0, WaitCondition.ELEMENT_CREATION, 2);
			return nameElement.getText();
		}
		catch (SeleniumActionTimeout e)
		{
			return "unknown";
		}
	}
}

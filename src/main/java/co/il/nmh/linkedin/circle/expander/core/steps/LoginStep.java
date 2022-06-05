package co.il.nmh.linkedin.circle.expander.core.steps;

import co.il.nmh.easy.selenium.EasySeleniumBrowser;
import co.il.nmh.easy.selenium.data.VerificationResponse;
import co.il.nmh.easy.selenium.enums.CompareString;
import co.il.nmh.easy.selenium.enums.MouseButton;
import co.il.nmh.easy.selenium.enums.SearchBy;
import co.il.nmh.easy.selenium.enums.WaitCondition;
import co.il.nmh.easy.selenium.exceptions.SeleniumActionTimeout;
import co.il.nmh.linkedin.circle.expander.data.enums.LoginStatusEnum;
import co.il.nmh.linkedin.circle.expander.properties.LinkedinCircleExpanderProperties;
import co.il.nmh.linkedin.circle.expander.properties.LoginProperties;
import co.il.nmh.linkedin.circle.expander.utils.SharedResources;
import org.openqa.selenium.WebElement;

/**
 * @author Maor Hamami
 */

public class LoginStep
{
	public static LoginStatusEnum login(EasySeleniumBrowser easySeleniumBrowser, String username, String password)
	{
		LinkedinCircleExpanderProperties linkedinCircleExpanderProperties = SharedResources.INSTANCE.getLinkedinCircleExpanderProperties();
		LoginProperties loginProperties = linkedinCircleExpanderProperties.getLogin();

		VerificationResponse networkPage = easySeleniumBrowser.veirfy().verifyURL(CompareString.CONTAINS, linkedinCircleExpanderProperties.getMyNetworkPage(), true, 2);

		if (networkPage.isSuccess())
		{
			return LoginStatusEnum.ALREADY_LOGGED_IN;
		}

		WebElement usernameTb;
		WebElement passwordTb;
		WebElement connectBtn;

		try
		{
			easySeleniumBrowser.navigator().navigate(loginProperties.getUrl(), 15);

			usernameTb = easySeleniumBrowser.document().getElement(SearchBy.ID, loginProperties.getUsernameTbId(), 0, WaitCondition.ELEMENT_CREATION, 10);
			passwordTb = easySeleniumBrowser.document().getElement(SearchBy.ID, loginProperties.getPasswordTbId(), 0, WaitCondition.ELEMENT_CREATION, 10);
			connectBtn = easySeleniumBrowser.document().getElement(SearchBy.CSS_SELECTOR, loginProperties.getConnectBtnCSSSelector(), 0, WaitCondition.ELEMENT_CREATION, 10);
		}
		catch (SeleniumActionTimeout e)
		{
			return LoginStatusEnum.LOGIC_FAILURE;
		}

		easySeleniumBrowser.action().setTextboxValue(usernameTb, username);
		easySeleniumBrowser.action().setTextboxValue(passwordTb, password);
		easySeleniumBrowser.action().click(connectBtn, MouseButton.LEFT);

		VerificationResponse verifyURL = easySeleniumBrowser.veirfy().verifyURL(CompareString.CONTAINS, loginProperties.getVerifyUrl(), true, 10);

		if (verifyURL.isSuccess())
		{
			try
			{
				easySeleniumBrowser.navigator().navigate(linkedinCircleExpanderProperties.getMyNetworkPage(), 10);

				verifyURL = easySeleniumBrowser.veirfy().verifyURL(CompareString.CONTAINS, linkedinCircleExpanderProperties.getVerifyUrl(), true, 10);

				if (verifyURL.isSuccess())
				{
					return LoginStatusEnum.SUCCESS;
				}

				return LoginStatusEnum.FAILED;
			}
			catch (SeleniumActionTimeout e)
			{
			}
		}

		return LoginStatusEnum.FAILED;
	}
}

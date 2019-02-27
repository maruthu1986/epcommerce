package com.elasticpath.selenium.common;

import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.dialogs.SignInDialog;
import com.elasticpath.selenium.setup.PublishEnvSetUp;

/**
 * CM Object. Blank page before each page loads.
 */
public class CM extends AbstractPageObject {
	/**
	 * constructor.
	 *
	 * @param driver WebDriver.
	 */
	public CM(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Launch CM.
	 *
	 * @return SignInDialog.
	 */
	public SignInDialog openCM() {
		getDriver().get(getSiteURL());
		return new SignInDialog(getDriver());
	}

	/**
	 * Launch publish CM.
	 *
	 * @return SignInDialog.
	 */
	public SignInDialog openPublishCM() {
		PublishEnvSetUp.getDriver().get(getPublishEnvURL());
		return new SignInDialog(PublishEnvSetUp.getDriver());
	}

}

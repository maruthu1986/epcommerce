package com.elasticpath.selenium.setup;

import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.framework.util.PropertyManager;
import com.elasticpath.selenium.framework.webdriverfactories.ChromeWebDriverFactory;

/**
 * Class to create new web driver.
 */
public final class DriverFactory {

	private static final PropertyManager PROPERTY_MANAGER = PropertyManager.getInstance();

	private DriverFactory() {

	}

	/**
	 * Creates WebDriver.
	 *
	 * @return webDriver
	 */
	public static WebDriver createWebDriver() {
		String browser = PROPERTY_MANAGER.getProperty("selenium.session.browser");
		String remoteWebDriverURL = PROPERTY_MANAGER.getProperty("remote.web.driver.url");

		if ("chrome".equals(browser)) {
			return new ChromeWebDriverFactory().createWebDriver();
		} else if ("remote_chrome".equals(browser)) {
			return new ChromeWebDriverFactory().createRemoteWebDriver(remoteWebDriverURL);
		}
		return null;
	}

}

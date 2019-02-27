package com.elasticpath.selenium.setup;

import org.openqa.selenium.WebDriver;

/**
 * Creates webDriver for Publish environment.
 */
public final class PublishEnvSetUp {

	private static final Object LOCKOBJ = new Object();
	private static WebDriver driver;

	private PublishEnvSetUp() {
	}

	/**
	 * Gets Publish environment driver.
	 *
	 * @return webDriver.
	 */
	public static WebDriver getDriver() {
		synchronized (LOCKOBJ) {
			if (driver == null) {
				driver = DriverFactory.createWebDriver();
			}
		}
		return driver;
	}

	/**
	 * Quits the Publish environment driver which will also close browser.
	 */
	public static void quitDriver() {
		synchronized (LOCKOBJ) {
			if (driver != null) {
				driver.quit();
				driver = null;
			}
		}
	}
}

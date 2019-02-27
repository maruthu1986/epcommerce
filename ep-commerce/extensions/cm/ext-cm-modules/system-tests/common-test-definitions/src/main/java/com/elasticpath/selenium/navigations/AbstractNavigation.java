package com.elasticpath.selenium.navigations;

import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;

/**
 * Abstract Navigation for common navigation actions.
 */
public abstract class AbstractNavigation extends AbstractPageObject {

	/**
	 * constructor.
	 *
	 * @param driver WebDriver which drives this webpage.
	 */
	public AbstractNavigation(final WebDriver driver) {
		super(driver);
	}

}

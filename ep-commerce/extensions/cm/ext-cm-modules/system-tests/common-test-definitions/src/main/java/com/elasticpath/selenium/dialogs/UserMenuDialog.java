package com.elasticpath.selenium.dialogs;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * User menu dialog.
 */
public class UserMenuDialog extends AbstractDialog {

	/**
	 * CSS selector used to identify the dialog.
	 */
	public static final String LOGOUT_CSS = "div[appearance-id='menu'] div[seeable='true'][widget-id='Logout']";
	private static final String CHANGE_PAGINATION_CSS = "div[appearance-id='menu'] div[seeable='true'][widget-id='Change Pagination Settings']";
	private static final String CHANGE_PASSWORD_CSS = "div[appearance-id='menu'] div[seeable='true'][widget-id='Change Password']";
	private static final String CHANGE_TIMEZONE_CSS = "div[appearance-id='menu'] div[seeable='true'][widget-id='Set Time Zone']";


	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public UserMenuDialog(final WebDriver driver) {
		super(driver);
		//Hover mouse to escape the User Menu tooltip which blocks the menu.
		hoverMouseOverElement(getDriver().findElement(By.cssSelector(CHANGE_PASSWORD_CSS)));
	}

	/**
	 * Clicks Logout.
	 *
	 * @return SignInDialog
	 */
	public SignInDialog clickLogout() {
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(LOGOUT_CSS)));
		return new SignInDialog(getDriver());
	}

	/**
	 * Clicks Change Pagination.
	 */
	public void clickChangePagination() {
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(CHANGE_PAGINATION_CSS)));
	}

	/**
	 * Clicks Change Password.
	 *
	 * @return ChangePasswordDialog
	 */
	public ChangePasswordDialog clickChangePassword() {
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(CHANGE_PASSWORD_CSS)));
		return new ChangePasswordDialog(getDriver());
	}

	/**
	 * Clicks Change Timezone.
	 *
	 * @return ChangeTimezoneDialog
	 */
	public ChangeTimezoneDialog clickChangeTimezone() {
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(CHANGE_TIMEZONE_CSS)));
		return new ChangeTimezoneDialog(getDriver());
	}
}
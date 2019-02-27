package com.elasticpath.selenium.dialogs;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Delete Confirm dialog.
 */
public class ConfirmDialog extends AbstractDialog {
	/**
	 * CSS selector used to identify the dialog.
	 */
	public static final String CONFIRM_OK_BUTTON_CSS = "div[automation-id*='%s'] div[widget-id='OK'][seeable='true']";
	public static final String DELETE_BUTTON_CSS = "div[automation-id*='%s'] div[widget-id='Delete'][seeable='true']";
	public static final String INCLUDE_DATA_POLICY_BUTTON_CSS = "div[automation-id='com.elasticpath.cmclient.fulfillment.FulfillmentMessages"
			+ ".IncludeDataPointsWithGrantedConsent_Label'][seeable='true']";
	private static final String NO_BUTTON_CSS = "div[automation-id*='%s'] div[widget-id='No'][seeable='true']";
	private static final String YES_BUTTON_CSS = "div[automation-id*='%s'] div[widget-id='Yes'][seeable='true']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public ConfirmDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Clicks OK in confirmation dialog.
	 *
	 * @param dialogAutomationId the dialog automation id
	 */
	public void clickOKButton(final String dialogAutomationId) {
		clickButton(String.format(CONFIRM_OK_BUTTON_CSS, dialogAutomationId), "Confirm_OK");
		waitTillElementDisappears(By.cssSelector(String.format(CONFIRM_OK_BUTTON_CSS, dialogAutomationId)));
	}

	/**
	 * Clicks Cancel button.
	 */
	public void clickCancelButton() {
		clickButton(CANCEL_BUTTON_CSS, "Cancel");
		waitTillElementDisappears(By.cssSelector(CANCEL_BUTTON_CSS));
	}

	/**
	 * Clicks No button.
	 *
	 * @param dialogAutomationId the dialog automation id
	 */
	public void clickNoButton(final String dialogAutomationId) {
		clickButton(String.format(NO_BUTTON_CSS, dialogAutomationId), "No");
		waitTillElementDisappears(By.cssSelector(String.format(NO_BUTTON_CSS, dialogAutomationId)));
	}

	/**
	 * Clicks Yes button.
	 *
	 * @param dialogAutomationId the dialog automation id
	 */
	public void clickYesButton(final String dialogAutomationId) {
		clickButton(String.format(YES_BUTTON_CSS, dialogAutomationId), "Yes");
		waitTillElementDisappears(By.cssSelector(String.format(YES_BUTTON_CSS, dialogAutomationId)));
	}

	/**
	 * Clicks Delete in confirmation dialog.
	 *
	 * @param dialogAutomationId the dialog automation id
	 */
	public void clickDeleteButton(final String dialogAutomationId) {
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(INCLUDE_DATA_POLICY_BUTTON_CSS)));
		clickButton(String.format(DELETE_BUTTON_CSS, dialogAutomationId), "Delete");
		waitTillElementDisappears(By.cssSelector(String.format(DELETE_BUTTON_CSS, dialogAutomationId)));
	}

}

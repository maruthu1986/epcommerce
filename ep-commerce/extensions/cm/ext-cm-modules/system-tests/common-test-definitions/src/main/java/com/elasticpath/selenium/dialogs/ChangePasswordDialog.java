package com.elasticpath.selenium.dialogs;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Change Password Dialog.
 */
public class ChangePasswordDialog extends AbstractDialog {

	/**
	 * CSS selector used to identify the dialog.
	 */
	public static final String CHANGE_PASSWORD_PARENT_CSS = "div[widget-id='Change Password'][widget-type='Shell'] ";
	private static final String OLD_PASSWORD_INPUT_CSS = CHANGE_PASSWORD_PARENT_CSS + "div[widget-id='Old Password'] input";
	private static final String NEW_PASSWORD_INPUT_CSS = CHANGE_PASSWORD_PARENT_CSS + "div[widget-id='New Password'] input";
	private static final String CONFIRM_NEW_PASSWORD_INPUT_CSS = CHANGE_PASSWORD_PARENT_CSS + "div[widget-id='Confirm New Password'] input";
	private static final String SAVE_BUTTON_CSS = CHANGE_PASSWORD_PARENT_CSS + "div[widget-id='Save']";
	private static final String CANCEL_BUTTON_CSS = CHANGE_PASSWORD_PARENT_CSS + "div[widget-id='Cancel']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public ChangePasswordDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Inputs old password.
	 *
	 * @param oldPassword String
	 */
	public void enterOldPassword(final String oldPassword) {
		clearAndType(OLD_PASSWORD_INPUT_CSS, oldPassword);
	}

	/**
	 * Inputs new password.
	 *
	 * @param newPassword String
	 */
	public void enterNewPassword(final String newPassword) {
		clearAndType(NEW_PASSWORD_INPUT_CSS, newPassword);
	}

	/**
	 * Inputs confirm new password.
	 *
	 * @param newPassword String
	 */
	public void enterConfirmNewPassword(final String newPassword) {
		clearAndType(CONFIRM_NEW_PASSWORD_INPUT_CSS, newPassword);
	}

	/**
	 * Clicks Save button.
	 */
	public void clickSaveButton() {
		clickButton(SAVE_BUTTON_CSS, "Save");
	}

	/**
	 * Clicks cancel button.
	 */
	public void clickCancelButton() {
		clickButton(CANCEL_BUTTON_CSS, "Cancel");
		waitTillElementDisappears(By.cssSelector(CHANGE_PASSWORD_PARENT_CSS));
	}
}
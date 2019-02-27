package com.elasticpath.selenium.dialogs;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Change Timezone Dialog.
 */
public class ChangeTimezoneDialog extends AbstractDialog {

	/**
	 * CSS selector used to identify the dialog.
	 */
	public static final String CHANGE_TIMEZONE_PARENT = "div[widget-id='Change Time Zone'][widget-type='Shell'] ";
	private static final String USE_CUSTOM_TIMEZONE = CHANGE_TIMEZONE_PARENT + "div[widget-id='Use custom time zone']";
	private static final String CUSTOM_TIMEZONE_COMBOBOX = CHANGE_TIMEZONE_PARENT
			+ "div[automation-id='com.elasticpath.cmclient.core.CoreMessages.ChangeTimezoneDialog_Description'][appearance-id='ccombo']";
	private static final String UTC_TIMEZONE = "(UTC+00:00) Coordinated Universal Time";
	private static final String SAVE_BUTTON_CSS = CHANGE_TIMEZONE_PARENT + "div[widget-id='Save']";
	private static final String CANCEL_BUTTON_CSS = CHANGE_TIMEZONE_PARENT + "div[widget-id='Cancel']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public ChangeTimezoneDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Selects the UTC timezone from the dropdown.
	 */
	public void selectUTCTimezone() {
		click(By.cssSelector(USE_CUSTOM_TIMEZONE));
		selectComboBoxItem(CUSTOM_TIMEZONE_COMBOBOX, UTC_TIMEZONE);
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
		waitTillElementDisappears(By.cssSelector(CHANGE_TIMEZONE_PARENT));
	}
}
package com.elasticpath.selenium.dialogs;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Edit Attribute Value Dialog.
 */
public class EditShortTextMultiValueAttributeDialog extends AbstractDialog {

	/**
	 * CSS selector used to identify the dialog.
	 */
	public static final String SHORT_TEXT_MULTI_VALUE_DIALOG_CSS = "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages"
			+ ".ShortTextMultiValueDialog_WinTitle'] ";
	private static final String ADD_VALUE_BUTTON_CSS = "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages"
			+ ".ShortTextMultiValueDialog_AddValue']";
	private static final String OK_BUTTON_CSS = SHORT_TEXT_MULTI_VALUE_DIALOG_CSS + "div[widget-id='Save']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public EditShortTextMultiValueAttributeDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Clicks add value button.
	 *
	 * @return the dialog.
	 */
	public AddShortTextAttributeDialog clickAddValueButton() {
		clickButton(ADD_VALUE_BUTTON_CSS, "Add Value", AddShortTextAttributeDialog.ADD_SHORT_TEXT_DIALOG_CSS);
		return new AddShortTextAttributeDialog(getDriver());
	}

	/**
	 * Click ok button.
	 */
	public void clickOKButton() {
		clickButton(OK_BUTTON_CSS, "OK");
		waitTillElementDisappears(By.cssSelector(SHORT_TEXT_MULTI_VALUE_DIALOG_CSS));
	}
}

package com.elasticpath.selenium.dialogs;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Add Short Text Attribute Value Dialog.
 */
public class AddShortTextAttributeDialog extends AbstractDialog {

	/**
	 * CSS selector used to identify the dialog.
	 */
	public static final String ADD_SHORT_TEXT_DIALOG_CSS = "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages"
			+ ".ShortTextDialog_Add_Title'] ";
	private static final String TEXTAREA_CSS = ADD_SHORT_TEXT_DIALOG_CSS + "textarea";
	private static final String OK_BUTTON_CSS = "div[automation-id='com.elasticpath.cmclient.core.CoreMessages.AbstractEpDialog_ButtonOK']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public AddShortTextAttributeDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Inputs short text attribute value.
	 *
	 * @param shortText the short text.
	 */
	public void enterShortTextValue(final String shortText) {
		clearAndType(TEXTAREA_CSS, shortText);
	}

	/**
	 * Click ok button.
	 */
	public void clickOKButton() {
		clickButton(OK_BUTTON_CSS, "OK");
		waitTillElementDisappears(By.cssSelector(ADD_SHORT_TEXT_DIALOG_CSS));
	}
}

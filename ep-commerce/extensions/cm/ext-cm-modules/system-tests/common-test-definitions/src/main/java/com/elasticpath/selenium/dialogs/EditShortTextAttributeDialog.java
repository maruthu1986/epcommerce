package com.elasticpath.selenium.dialogs;

import org.openqa.selenium.WebDriver;

/**
 * Edit Short Text Attribute Value Dialog.
 */
public class EditShortTextAttributeDialog extends AbstractDialog {

	/**
	 * CSS selector used to identify the dialog.
	 */
	public static final String PARENT_EDIT_SHORT_TEXT_CSS = "div[widget-id='Edit Short Text'][widget-type='Shell'] ";
	private static final String TEXTAREA_CSS = PARENT_EDIT_SHORT_TEXT_CSS + "textarea";
	private static final String OK_BUTTON_CSS = PARENT_EDIT_SHORT_TEXT_CSS + "div[widget-id='OK']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public EditShortTextAttributeDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Inputs short text attribute value.
	 *
	 * @param shortText String
	 */
	public void enterShortTextValue(final String shortText) {
		clearAndType(TEXTAREA_CSS, shortText);
	}

	/**
	 * Clicks OK button.
	 */
	public void clickOKButton() {
		clickButton(OK_BUTTON_CSS, "OK");
	}
}

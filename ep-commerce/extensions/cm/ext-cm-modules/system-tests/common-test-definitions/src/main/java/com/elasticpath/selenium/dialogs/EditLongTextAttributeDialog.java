package com.elasticpath.selenium.dialogs;

import org.openqa.selenium.WebDriver;

/**
 * Edit Long Text Attribute Value Dialog.
 */
public class EditLongTextAttributeDialog extends AbstractDialog {

	/**
	 * CSS selector used to identify the dialog.
	 */
	public static final String PARENT_EDIT_LONG_TEXT_CSS = "div[widget-id='Edit Long Text Value'][widget-type='Shell'] ";
	private static final String TEXTAREA_CSS = PARENT_EDIT_LONG_TEXT_CSS + "textarea";
	private static final String OK_BUTTON_CSS = PARENT_EDIT_LONG_TEXT_CSS + "div[widget-id='OK']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public EditLongTextAttributeDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Inputs long text attribute value.
	 *
	 * @param longText String
	 */
	public void enterLongTextValue(final String longText) {
		clearAndType(TEXTAREA_CSS, longText);
	}

	/**
	 * Clicks OK button.
	 */
	public void clickOKButton() {
		clickButton(OK_BUTTON_CSS, "OK");
	}
}

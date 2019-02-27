package com.elasticpath.selenium.dialogs;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Edit Integer Value Dialog.
 */
public class EditIntegerValueAttributeDialog extends AbstractDialog {

	/**
	 * CSS selector used to identify the dialog.
	 */
	public static final String PARENT_INTEGER_VALUE_CSS = "div[widget-id='Edit Integer Value'][widget-type='Shell'] ";
	private static final String INPUT_CSS = PARENT_INTEGER_VALUE_CSS + "input";
	private static final String OK_BUTTON_CSS = PARENT_INTEGER_VALUE_CSS + "div[widget-id='OK']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public EditIntegerValueAttributeDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Inputs integer attribute value.
	 *
	 * @param integerValue The integer value.
	 */
	public void enterIntegerValue(final String integerValue) {
		clearAndType(INPUT_CSS, integerValue);
	}

	/**
	 * Click OK Button.
	 */
	public void clickOKButton() {
		clickButton(OK_BUTTON_CSS, "OK");
		waitTillElementDisappears(By.cssSelector(PARENT_INTEGER_VALUE_CSS));
	}
}

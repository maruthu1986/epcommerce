package com.elasticpath.selenium.dialogs;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Edit Decimal Value Dialog.
 */
public class EditDecimalValueAttributeDialog extends AbstractDialog {

	/**
	 * CSS selector used to identify the dialog.
	 */
	public static final String PARENT_DECIMAL_VALUE_CSS = "div[widget-id='Edit Decimal Value'][widget-type='Shell'] ";
	private static final String INPUT_CSS = PARENT_DECIMAL_VALUE_CSS + "input";
	private static final String OK_BUTTON_CSS = PARENT_DECIMAL_VALUE_CSS + "div[widget-id='OK']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public EditDecimalValueAttributeDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Inputs decimal attribute value.
	 *
	 * @param decimalValue the decimal value.
	 */
	public void enterDecimalValue(final String decimalValue) {
		clearAndType(INPUT_CSS, decimalValue);
	}

	/**
	 * Click ok button.
	 */
	public void clickOKButton() {
		clickButton(OK_BUTTON_CSS, "OK");
		waitTillElementDisappears(By.cssSelector(OK_BUTTON_CSS));
	}
}

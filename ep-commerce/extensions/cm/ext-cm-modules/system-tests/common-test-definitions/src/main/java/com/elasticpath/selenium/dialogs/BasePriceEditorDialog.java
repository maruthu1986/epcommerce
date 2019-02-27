package com.elasticpath.selenium.dialogs;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Base Price Editor Dialog.
 */
public class BasePriceEditorDialog extends AbstractDialog {

	/**
	 * CSS selector used to identify the dialog.
	 */
	public static final String PARENT_BASE_PRICE_EDITOR_CSS = "div[widget-id='Price Editor'][widget-type='Shell'] ";
	private static final String INPUT_CSS = PARENT_BASE_PRICE_EDITOR_CSS + "div[widget-id='List Price'] > input";
	private static final String OK_BUTTON_CSS = PARENT_BASE_PRICE_EDITOR_CSS + "div[widget-id='OK']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public BasePriceEditorDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Inputs list price.
	 *
	 * @param listPrice the list price.
	 */
	public void enterListPrice(final String listPrice) {
		clearAndType(INPUT_CSS, listPrice);
	}

	/**
	 * Click OK Button.
	 */
	public void clickOKButton() {
		clickButton(OK_BUTTON_CSS, "OK");
		waitTillElementDisappears(By.cssSelector(PARENT_BASE_PRICE_EDITOR_CSS));
	}
}

package com.elasticpath.selenium.dialogs;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Add Price Tier Dialog.
 */
public class AddPriceTierDialog extends AbstractDialog {

	private static final String ADD_PRICE_TIER_DIALOG_PARENT_CSS = "div[widget-id='Add Pricing Tier'][widget-type='Shell'] ";
	private static final String LIST_PRICE_INPUT_CSS = ADD_PRICE_TIER_DIALOG_PARENT_CSS + "div[widget-id='List Price'] > input";
	private static final String QTY_INPUT_CSS = ADD_PRICE_TIER_DIALOG_PARENT_CSS + "div[widget-id='Quantity'] input";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public AddPriceTierDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Clicks OK button.
	 */
	public void clickOKButton() {
		clickButton(OK_BUTTON_CSS, "OK");
		waitTillElementDisappears(By.cssSelector(ADD_PRICE_TIER_DIALOG_PARENT_CSS));
	}

	/**
	 * Inputs list price and QTY.
	 *
	 * @param listPrice the list price.
	 * @param quantity  quantity.
	 */
	public void addPriceTierData(final String listPrice, final String quantity) {
		clearAndType(QTY_INPUT_CSS, quantity);
		clearAndType(LIST_PRICE_INPUT_CSS, listPrice);
	}

}

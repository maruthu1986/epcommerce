package com.elasticpath.selenium.dialogs;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Select a SKU Dialog.
 */
public class SelectASkuDialog extends AbstractDialog {

	/**
	 * CSS selector used to identify the dialog.
	 */
	public static final String SELECT_A_SKU_PARENT_CSS = "div[widget-id='Select a SKU'][widget-type='Shell'] ";
	private static final String PRODUCT_CODE_INPUT_CSS = SELECT_A_SKU_PARENT_CSS + "div[widget-id='Product Code'] > input";
	private static final String PRODUCT_NAME_INPUT_CSS = SELECT_A_SKU_PARENT_CSS + "div[widget-id='Product Name'] > input";
	private static final String SKU_CODE_INPUT_CSS = SELECT_A_SKU_PARENT_CSS + "div[widget-id='SKU Code'] > input";
	private static final String SEARCH_BUTTON_CSS = SELECT_A_SKU_PARENT_CSS + "div[widget-id='Search'][widget-type='Button']";
	private static final String OK_BUTTON_CSS = SELECT_A_SKU_PARENT_CSS + "div[widget-id='OK']";
	private static final String SKU_RESULT_PARENT_CSS = "div[widget-id='Sku Finder'][widget-type='Table'] ";
	private static final String SKU_RESULT_COLUMN_CSS = SKU_RESULT_PARENT_CSS + "div[column-id='%s']";
	private static final String PRICE_LIST_RESULT_PARENT_CSS = "div[widget-id='Price Table'][widget-type='Table'] ";
	private static final String PRICE_LIST_RESULT_COLUMN_CSS = PRICE_LIST_RESULT_PARENT_CSS + "div[column-id='%s']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public SelectASkuDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Inputs product code.
	 *
	 * @param productCode the product code.
	 */
	public void enterProductCode(final String productCode) {
		clearAndType(PRODUCT_CODE_INPUT_CSS, productCode);
	}

	/**
	 * Inputs product name.
	 *
	 * @param productName the product name.
	 */
	public void enterProductName(final String productName) {
		clearAndType(PRODUCT_NAME_INPUT_CSS, productName);
	}

	/**
	 * Inputs sku code.
	 *
	 * @param skuCode the sku code.
	 */
	public void enterSkuCode(final String skuCode) {
		clearAndType(SKU_CODE_INPUT_CSS, skuCode);
	}

	/**
	 * Clicks search button.
	 */
	public void clickSearchButton() {
		clickButton(SEARCH_BUTTON_CSS, "Search");
	}

	/**
	 * Selects the sku in search result list.
	 *
	 * @param skuCode the sku code.
	 */
	public void selectSkuCodeInSearchResult(final String skuCode) {
		assertThat(selectItemInDialog(SKU_RESULT_PARENT_CSS, SKU_RESULT_COLUMN_CSS, skuCode, "SKU Code"))
				.as("Unable to find sku code - " + skuCode)
				.isTrue();
	}

	/**
	 * Searches by sku code.
	 *
	 * @param skuCode the product code.
	 */
	public void searchBySkuCode(final String skuCode) {
		enterSkuCode(skuCode);
		clickSearchButton();
	}

	/**
	 * Selects the item in price table.
	 *
	 * @param priceListName the price list name.
	 */
	public void selectPriceList(final String priceListName) {
		assertThat(selectItemInDialog(PRICE_LIST_RESULT_PARENT_CSS, PRICE_LIST_RESULT_COLUMN_CSS, priceListName, "Name"))
				.as("Unable to find price list name - " + priceListName)
				.isTrue();
	}

	/**
	 * Clicks OK.
	 */
	public void clickOKButton() {
		clickButton(OK_BUTTON_CSS, "OK");
	}

	/**
	 * Selects sku and price list.
	 *
	 * @param skuCode       the price list name
	 * @param priceListName the price list name.
	 */
	public void selectSkuAndPriceList(final String skuCode, final String priceListName) {
		searchBySkuCode(skuCode);
		selectSkuCodeInSearchResult(skuCode);
		selectPriceList(priceListName);
		clickOK();
		waitTillElementDisappears(By.cssSelector(SELECT_A_SKU_PARENT_CSS));
	}

}

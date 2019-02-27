package com.elasticpath.selenium.dialogs;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Select a Product Dialog.
 */
public class SelectAProductDialog extends AbstractDialog {

	/**
	 * CSS selector used to identify the dialog.
	 */
	public static final String SELECT_A_PRODUCT_PARENT_CSS = "div[widget-id='Select a Product'][widget-type='Shell'] ";
	private static final String PRODUCT_CODE_INPUT_CSS = SELECT_A_PRODUCT_PARENT_CSS + "div[widget-id='Product Code'] > input";
	private static final String PRODUCT_NAME_INPUT_CSS = SELECT_A_PRODUCT_PARENT_CSS + "div[widget-id='Product Name'] > input";
	private static final String SEARCH_BUTTON_CSS = SELECT_A_PRODUCT_PARENT_CSS + "div[widget-id='Search'][widget-type='Button']";
	private static final String OK_BUTTON_CSS = SELECT_A_PRODUCT_PARENT_CSS + "div[widget-id='OK']";
	private static final String PRODUCT_RESULT_PARENT_CSS = "div[widget-id='Product Finder'][widget-type='Table'] ";
	private static final String PRODUCT_RESULT_COLUMN_CSS = PRODUCT_RESULT_PARENT_CSS + "div[column-id='%s']";


	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public SelectAProductDialog(final WebDriver driver) {
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
	 * Clicks search button.
	 */
	public void clickSearchButton() {
		clickButton(SEARCH_BUTTON_CSS, "Search");
	}

	/**
	 * Selects the item in search result list.
	 *
	 * @param productCode the product code.
	 */
	public void selectProductByCode(final String productCode) {
		assertThat(selectItemInDialog(PRODUCT_RESULT_PARENT_CSS, PRODUCT_RESULT_COLUMN_CSS, productCode, "Product Code"))
				.as("Unable to find product code - " + productCode)
				.isTrue();
	}

	/**
	 * Selects the item in search result list.
	 *
	 * @param productName the product name.
	 */
	public void selectProductByName(final String productName) {
		assertThat(selectItemInDialog(PRODUCT_RESULT_PARENT_CSS, PRODUCT_RESULT_COLUMN_CSS, productName, "Product Name"))
				.as("Unable to find product name - " + productName)
				.isTrue();
	}

	/**
	 * Clicks OK.
	 */
	public void clickOKButton() {
		clickButton(OK_BUTTON_CSS, "OK");
		waitTillElementDisappears(By.cssSelector(SELECT_A_PRODUCT_PARENT_CSS));
	}

}

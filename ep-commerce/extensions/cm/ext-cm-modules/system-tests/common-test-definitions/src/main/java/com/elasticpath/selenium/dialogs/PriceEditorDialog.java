package com.elasticpath.selenium.dialogs;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.WebDriver;

/**
 * Base Price Editor Dialog.
 */
public class PriceEditorDialog extends AbstractDialog {

	/**
	 * CSS selector used to identify the dialog.
	 */
	public static final String PRICE_EDITOR_PARENT_CSS = "div[widget-id='Price Editor'][widget-type='Shell'] ";
	private static final String LIST_PRICE_INPUT_CSS = PRICE_EDITOR_PARENT_CSS + "div[widget-id='List Price'] > input";
	private static final String SALE_PRICE_INPUT_CSS = PRICE_EDITOR_PARENT_CSS + "div[widget-id='Sale Price'] > input";
	private static final String SELECT_PRODUCT_IMAGE_LINK_CSS = PRICE_EDITOR_PARENT_CSS + "div[widget-type='ImageHyperlink'] > div[style*='.png']";
	private static final String OK_BUTTON_CSS = PRICE_EDITOR_PARENT_CSS + "div[widget-id='OK']";
	private static final String TYPE_COMBO_PARENT_CSS = PRICE_EDITOR_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.pricelistmanager.PriceListManagerMessages.BaseAmount_ObjectType'][widget-type='CCombo']";
	private static final String DIALOG_ERROR_VALIDATION =
			"div[automation-id='com.elasticpath.cmclient.pricelistmanager.PriceListManagerMessages.BaseAmount_ObjectCode'] + div > img";
	private static final String QUANTITY_INPUT_CSS = PRICE_EDITOR_PARENT_CSS + "div[widget-id='Quantity']";
	private static final String INCREASE_QUANTITY_BUTTON_CSS = QUANTITY_INPUT_CSS + " div[appearance-id='spinner-button-up']";
	private static final int SLEEP_TIME = 500;

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public PriceEditorDialog(final WebDriver driver) {
		super(driver);
	}


	/**
	 * Inputs List Price.
	 *
	 * @param listPrice String
	 */
	public void enterListPrice(final String listPrice) {
		getWaitDriver().waitForElementToBeInteractable(LIST_PRICE_INPUT_CSS);
		clearAndType(LIST_PRICE_INPUT_CSS, listPrice);
	}

	/**
	 * Inputs Sale Price.
	 *
	 * @param salePrice String
	 */
	public void enterSalePrice(final String salePrice) {
		getWaitDriver().waitForElementToBeInteractable(SALE_PRICE_INPUT_CSS);
		clearAndType(SALE_PRICE_INPUT_CSS, salePrice);
	}

	/**
	 * Clicks OK button.
	 */
	public void clickOKButton() {
		clickButton(OK_BUTTON_CSS, "OK");
		waitTillElementDisappears(By.cssSelector(PRICE_EDITOR_PARENT_CSS));
	}

	/**
	 * Clicks Select Product image link.
	 *
	 * @return SelectAProductDialog
	 */
	public SelectAProductDialog clickSelectProductImageLink() {
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(SELECT_PRODUCT_IMAGE_LINK_CSS)));
		return new SelectAProductDialog(getDriver());
	}

	/**
	 * Selects type sku.
	 */
	public void selectTypeSku() {
		selectType("SKU");
	}

	/**
	 * Selects type product.
	 */
	public void selectTypeProduct() {
		selectType("PRODUCT");
	}

	/**
	 * Selects type.
	 *
	 * @param type the type
	 */
	private void selectType(final String type) {
		assertThat(selectComboBoxItem(TYPE_COMBO_PARENT_CSS, type))
				.as("Unable to find type " + type)
				.isTrue();
	}

	/**
	 * Click select sku image link.
	 *
	 * @return SelectASkuDialog.
	 */
	public SelectASkuDialog clickSelectSkuImageLink() {
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(SELECT_PRODUCT_IMAGE_LINK_CSS)));
		return new SelectASkuDialog(getDriver());
	}

	/**
	 * Verify validation error.
	 *
	 * @param errorMsg the error message.
	 */
	public void verifyValidationErrorIsPresent(final String errorMsg) {
		try {
			click(getWaitDriver().waitForElementToBeVisible(By.cssSelector(PRICE_EDITOR_PARENT_CSS + DIALOG_ERROR_VALIDATION)));
			sleep(SLEEP_TIME);
			assertThat(getDriver().getPageSource().contains(errorMsg))
					.as("unable to find error message '" + errorMsg + "'")
					.isTrue();
		} catch (ElementNotVisibleException e) {
			assertThat(false)
					.as("Unable to find error message " + errorMsg)
					.isTrue();
		}
	}

	/**
	 * Enter the quantity.
	 *
	 * @param quantity the quantity
	 */
	public void enterQuantity(final String quantity) {
		for (int i = 1; i < Integer.parseInt(quantity); i++) {
			click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(INCREASE_QUANTITY_BUTTON_CSS)));
		}
	}
}

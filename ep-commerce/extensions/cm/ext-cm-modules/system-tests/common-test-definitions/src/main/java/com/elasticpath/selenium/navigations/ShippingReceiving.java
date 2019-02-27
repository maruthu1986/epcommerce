package com.elasticpath.selenium.navigations;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.editor.InventoryEditor;
import com.elasticpath.selenium.resultspane.RmaSearchResultPane;

/**
 * Shipping/Receiving.
 */
public class ShippingReceiving extends AbstractNavigation {

	private static final String SKU_CODE_INPUT_CSS = "div[widget-id='Retrieve SKU Inventory'] "
			+ "div[automation-id='com.elasticpath.cmclient.warehouse.WarehouseMessages.SearchView_SkuCodeLabel'] > input";
	private static final String INVENTORY_RETRIEVE_BUTTON_CSS = "div[widget-id='Retrieve'][widget-type='Button']";
	private static final String ACTIVE_LEFT_PANE = "div[pane-location='left-pane-inner'] div[active-editor='true'] ";
	private static final String APPEARANCE_ID_CSS = "div[appearance-id='ctab-item']";
	private static final String RETURNS_TAB_CSS = APPEARANCE_ID_CSS + "[widget-id='Returns']";
	private static final String ORDER_NUMBER_INPUT_CSS = ACTIVE_LEFT_PANE + "div[widget-id='Order Number'] > input";
	private static final String SEARCH_BUTTON_CSS = "div[pane-location='left-pane-inner'] div[widget-id='Search'][seeable='true']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public ShippingReceiving(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Inputs sku code.
	 *
	 * @param skuCode the sku code.
	 */
	public void enterSkuCode(final String skuCode) {
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(SKU_CODE_INPUT_CSS)));
		clearAndType(SKU_CODE_INPUT_CSS, skuCode);
	}

	/**
	 * Clicks on Retrieve button.
	 *
	 * @return InventoryEditor
	 */
	public InventoryEditor clickRetrieveButton() {
		clickButton(INVENTORY_RETRIEVE_BUTTON_CSS, "Retrieve");
		return new InventoryEditor(getDriver());
	}

	/**
	 * Clicks Returns tab.
	 */
	public void clickReturnsTab() {
		getWaitDriver().waitForElementToBeInteractable(RETURNS_TAB_CSS);
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(RETURNS_TAB_CSS)));
	}

	/**
	 * Enters Order Number.
	 *
	 * @param orderNumber String
	 */
	public void enterOrderNumber(final String orderNumber) {
		getWaitDriver().waitForElementToBeInteractable(ORDER_NUMBER_INPUT_CSS);
		clearAndType(ORDER_NUMBER_INPUT_CSS, orderNumber);
	}

	/**
	 * Clicks Search for return.
	 *
	 * @return RmaSearchResultsPane
	 */
	public RmaSearchResultPane clickReturnsSearch() {
		clickButton(SEARCH_BUTTON_CSS, "Search");
		return new RmaSearchResultPane(getDriver());
	}
}

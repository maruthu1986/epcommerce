package com.elasticpath.selenium.editor.product.tabs;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.dialogs.AddPriceTierDialog;

/**
 * Pricing Tab class.
 */
public class PricingTab extends AbstractPageObject {

	private static final String PRODUCT_EDITOR_PARENT_CSS = "div[pane-location='editor-pane'] div[active-editor='true'] ";
	private static final String PRICE_TIER_ITEM_PARENT_CSS = "div[widget-id='Base Amount'][widget-type='Table'] ";
	private static final String PRICE_TIER_ITEM_COLUMN_CSS = PRICE_TIER_ITEM_PARENT_CSS + "div[column-id='%s']";
	private static final String PRICE_TIER_ITEM_ROW_XPATH = "//div[@widget-id='Base Amount'][@widget-type='Table'] //div[@column-id='%s']/..";
	private static final String PRICE_TIER_ITEM_EDIT_BUTTON =
			"div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.ProductEditorSection_EditPriceTierButton']";
	private static final String PRICE_TIER_ITEM_ADD_BUTTON = PRODUCT_EDITOR_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.ProductEditorSection_AddPriceTierButton']";
	private static final String PRICE_TIER_ITEM_REMOVE_BUTTON =
			"div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.ProductEditorSection_RemovePriceTierButton']";
	private static final String PRICE_TIER_REMOVE_ITEM_DIALOG_OK_BUTTON =
			"div[automation-id='com.elasticpath.cmclient.pricelistmanager.PriceListManagerMessages.BaseAmount_Delete_Title'] "
					+ "div[widget-id='OK']";
	private static final String PRICE_TIER_EDIT_ITEM_DIALOG_OK_BUTTON =
			"div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.EditPriceTierDialog_WindowTitle'] "
					+ "div[widget-id='OK']";
	private static final String EDIT_ITEM_DIALOG_PRICE_FIELD =
			"div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.EditPriceTierDialog_WindowTitle'] "
					+ "div[widget-id='List Price'] > input";
	private static final String TAB_CSS
			= "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.Product%sPage_Title'][seeable='true']";
	private static final String PRICE_LIST_COMBO_PARENT_CSS = PRODUCT_EDITOR_PARENT_CSS + "div[widget-id='Price List:'][widget-type='CCombo']";

	/**
	 * constructor.
	 *
	 * @param driver WebDriver which drives this webpage.
	 */
	public PricingTab(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Clicks on tab to select it.
	 *
	 * @param tabName the tab name.
	 */
	public void selectTab(final String tabName) {
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(String.format(TAB_CSS, tabName))));
	}

	/**
	 * Selects price list in combo box.
	 *
	 * @param priceListName the price list name.
	 */
	public void selectPriceList(final String priceListName) {
		assertThat(selectComboBoxItem(PRICE_LIST_COMBO_PARENT_CSS, priceListName))
				.as("Unable to find price list - " + priceListName)
				.isTrue();
	}

	/**
	 * Clicks 'Add Item' button.
	 *
	 * @return AddItemDialog
	 */
	public AddPriceTierDialog clickAddPriceTierButton() {
		clickButton(PRICE_TIER_ITEM_ADD_BUTTON, "Add Tier..");
		return new AddPriceTierDialog(getDriver());
	}

	/**
	 * Verifies added Price tier exists.
	 *
	 * @param listPrice the list price.
	 */
	public void verifyAddedPriceTierExists(final String listPrice) {
		assertThat(selectItemInEditorPane(PRICE_TIER_ITEM_PARENT_CSS, PRICE_TIER_ITEM_COLUMN_CSS, listPrice, "List Price"))
				.as("Unable to find added price tier List Price - " + listPrice)
				.isTrue();
	}

	/**
	 * Clicks edit Price tier List Price.
	 *
	 * @param oldListPrice the list price.
	 * @param newListPrice the list price.
	 */
	public void clickEditPriceTierButton(final String oldListPrice, final String newListPrice) {
		selectPriceTierRow(oldListPrice);
		clickButton(PRICE_TIER_ITEM_EDIT_BUTTON, "Edit Tier..");
		clearAndType(EDIT_ITEM_DIALOG_PRICE_FIELD, newListPrice);
		clickButton(PRICE_TIER_EDIT_ITEM_DIALOG_OK_BUTTON, "OK");
		waitTillElementDisappears(By.cssSelector(PRICE_TIER_EDIT_ITEM_DIALOG_OK_BUTTON));
	}

	/**
	 * Selects price tier row.
	 *
	 * @param price the price tier price
	 */
	public void selectPriceTierRow(final String price) {
		getDriver().findElement(By.xpath(String.format(PRICE_TIER_ITEM_ROW_XPATH, price))).click();
	}

	/**
	 * Delete newly created Price tier.
	 *
	 * @param listPrice the list price.
	 */
	public void deletePriceTierListPrice(final String listPrice) {
		selectPriceTierRow(listPrice);
		clickButton(PRICE_TIER_ITEM_REMOVE_BUTTON, "Remove Tier..");
		clickButton(PRICE_TIER_REMOVE_ITEM_DIALOG_OK_BUTTON, "OK");
		waitTillElementDisappears(By.cssSelector(PRICE_TIER_REMOVE_ITEM_DIALOG_OK_BUTTON));

	}

	/**
	 * Verify newly created Price tier is deleted.
	 *
	 * @param listPrice the list price.
	 */
	public void verifyPriceTierDelete(final String listPrice) {
		setWebDriverImplicitWait(1);
		assertThat(verifyItemIsNotInEditorPane(PRICE_TIER_ITEM_PARENT_CSS, PRICE_TIER_ITEM_COLUMN_CSS, listPrice, "List Price"))
				.as("Delete failed, Price tier with List price is still present - " + listPrice)
				.isFalse();
		setWebDriverImplicitWaitToDefault();
	}
}

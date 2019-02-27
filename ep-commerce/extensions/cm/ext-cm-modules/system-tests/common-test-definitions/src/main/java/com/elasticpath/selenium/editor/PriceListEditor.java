package com.elasticpath.selenium.editor;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.dialogs.ConfirmDialog;
import com.elasticpath.selenium.dialogs.PriceEditorDialog;
import com.elasticpath.selenium.editor.product.ProductEditor;
import com.elasticpath.selenium.util.Constants;

/**
 * Price List Details pane.
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.GodClass"})
public class PriceListEditor extends AbstractPageObject {

	/**
	 * Page Object Id.
	 */
	public static final String PRICE_LIST_EDITOR_PARENT_CSS = "div[pane-location='editor-pane'] div[active-editor='true'] ";
	private static final String PRICE_LIST_NAME_INPUT_CSS = PRICE_LIST_EDITOR_PARENT_CSS + "div[widget-id='Price List'] > input";
	private static final String PRICE_LIST_DESCRIPTION_INPUT_CSS = PRICE_LIST_EDITOR_PARENT_CSS + "div[widget-id='Description'] > input";
	private static final String CURRENCY_CODE_INPUT_CSS = PRICE_LIST_EDITOR_PARENT_CSS + "div[widget-id='Currency'] > input";
	private static final String PRICES_TAB_CSS = "div[pane-location='editor-pane'] div[widget-id='Prices']";
	private static final String PRICE_LIST_SUMMARY_TAB_CSS = "div[pane-location='editor-pane'] div[widget-id='Price List Summary']";
	private static final String PRICE_LIST_EDITOR_CLOSE_ICON_CSS = "div[widget-id='%s'][appearance-id='ctab-item'][active-tab='true'] > "
			+ "div[style*='.gif']";
	private static final String ADD_PRICE_BUTTON_CSS = PRICE_LIST_EDITOR_PARENT_CSS + "div[widget-id='Add Price...'][seeable='true']";
	private static final String DELETE_PRICE_BUTTON_CSS = PRICE_LIST_EDITOR_PARENT_CSS + "div[widget-id='Delete Price...'][seeable='true']";
	private static final String EDIT_PRICE_BUTTON_CSS = PRICE_LIST_EDITOR_PARENT_CSS + "div[widget-id='Edit Price...'][seeable='true']";
	private static final String OPEN_ITEM_BUTTON_CSS = PRICE_LIST_EDITOR_PARENT_CSS + "div[widget-id='Open Item...'][seeable='true']";
	private static final String BASE_AMOUNT_PARENT_CSS = PRICE_LIST_EDITOR_PARENT_CSS + "div[widget-id='Base Amount'][widget-type='Table'] ";
	private static final String BASE_AMOUNT_COLUMN_CSS = BASE_AMOUNT_PARENT_CSS + "div[parent-widget-id='Base "
			+ "Amount'][widget-type='table_row'] > div[column-id='%s']";
	private static final String PRICE_LIST_ROW_CSS = BASE_AMOUNT_PARENT_CSS + "div[parent-widget-id='Base Amount'][row-id='%s']";
	private static final String PRICE_LIST_COLUMN_CSS = "div[column-id='%s']";
	private static final String SEARCH_BUTTON_CSS = PRICE_LIST_EDITOR_PARENT_CSS + "div[widget-id='Search']";
	private static final String SEARCH_TEXT_BOX_LIST_CSS = PRICE_LIST_EDITOR_PARENT_CSS + "div[widget-id=''][widget-type='Text'] > input";
	private static final String BASE_AMOUNT_EDITOR_PAGE_CSS = "div[automation-id='com.elasticpath.cmclient.pricelistmanager.editors"
			+ ".BaseAmountEditorPage']";
	private static final String PRICE_LIST_SUMMARY_EDITOR_CSS = "div[automation-id='PriceListDescriptorEditor']";
	private static final String BASE_PRICE_LIST_SEARCH_INPUTS_XPATH = "//div[@automation-id='com.elasticpath.cmclient.pricelistmanager"
			+ ".PriceListManagerMessages.PriceListBaseAmountSearch_Search']";
	private static final String BASE_PRICE_LIST_FILTER_INPUTS_XPATH = "//div[@automation-id='com.elasticpath.cmclient.pricelistmanager"
			+ ".PriceListManagerMessages.PriceListBaseAmountFilter_Search']";
	private static final String PRICE_LIST_PRICE_FROM_INPUT_XPATH = "/..//div[@automation-id='com.elasticpath.cmclient.pricelistmanager"
			+ ".PriceListManagerMessages.PriceListBaseAmountFilter_PriceFrom']/input";
	private static final String PRICE_LIST_PRICE_TO_INPUT_XPATH = "/..//div[@automation-id='com.elasticpath.cmclient.pricelistmanager"
			+ ".PriceListManagerMessages.PriceListBaseAmountFilter_PriceTo']/input";
	private static final String PRICE_LIST_SEARCH_FROM_INPUT_XPATH = BASE_PRICE_LIST_SEARCH_INPUTS_XPATH + PRICE_LIST_PRICE_FROM_INPUT_XPATH;
	private static final String PRICE_LIST_SEARCH_TO_INPUT_XPATH = BASE_PRICE_LIST_SEARCH_INPUTS_XPATH + PRICE_LIST_PRICE_TO_INPUT_XPATH;
	private static final String PRICE_LIST_FILTER_FROM_INPUT_XPATH = BASE_PRICE_LIST_FILTER_INPUTS_XPATH + PRICE_LIST_PRICE_FROM_INPUT_XPATH;
	private static final String PRICE_LIST_FILTER_TO_INPUT_XPATH = BASE_PRICE_LIST_FILTER_INPUTS_XPATH + PRICE_LIST_PRICE_TO_INPUT_XPATH;
	private static final String PRICE_LIST_SEARCH_BUTTON_XPATH = BASE_PRICE_LIST_SEARCH_INPUTS_XPATH + "/..//div[@widget-id='Search']";
	private static final String PRICE_LIST_FILTER_BUTTON_XPATH = BASE_PRICE_LIST_FILTER_INPUTS_XPATH + "/..//div[@widget-id='Filter']";
	private static final String PRICES_TABLE_ROWS_XPATH = "//div[@widget-id='Base Amount'][@widget-type='Table']//div[@widget-type='table_row']";

	/**
	 * Column header 'Product Name'.
	 */
	public static final String COLUMN_PRODUCT_NAME = "Product Name";
	/**
	 * Column header 'Sku Code'.
	 */
	public static final String COLUMN_SKU_CODE = "SKU Code";
	/**
	 * Column header 'Product Code'.
	 */
	public static final String COLUMN_PRODUCT_CODE = "Product Code";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public PriceListEditor(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Verifies price list summary editor visible.
	 */
	public void verifyPriceListSummaryEditorExists() {
		getWaitDriver().waitForElementToBeVisible(By.cssSelector(PRICE_LIST_SUMMARY_EDITOR_CSS));
	}

	/**
	 * Verifies base amount editor visible.
	 */
	public void verifyBaseAmountEditorExists() {
		getWaitDriver().waitForElementToBeVisible(By.cssSelector(BASE_AMOUNT_EDITOR_PAGE_CSS));
	}

	/**
	 * Enters price list name.
	 *
	 * @param priceListName the price list name.
	 */
	public void enterPriceListName(final String priceListName) {
		clearAndType(PRICE_LIST_NAME_INPUT_CSS, priceListName);
	}

	/**
	 * Enters price list description.
	 *
	 * @param priceListDescription the price list description.
	 */
	public void enterPriceListDescription(final String priceListDescription) {
		clearAndType(PRICE_LIST_DESCRIPTION_INPUT_CSS, priceListDescription);
	}

	/**
	 * Enters price list currency.
	 *
	 * @param priceListCurrency the price list currency.
	 */
	public void enterPriceListCurrency(final String priceListCurrency) {
		clearAndType(CURRENCY_CODE_INPUT_CSS, priceListCurrency);
	}

	/**
	 * Enters Code.
	 *
	 * @param code the code to search for.
	 */
	public void enterCodeToSearch(final String code) {
		clearAndType(getDriver().findElements(By.cssSelector(SEARCH_TEXT_BOX_LIST_CSS)).get(0), code);
	}

	/**
	 * Clears price list name.
	 */
	public void clearPriceListName() {
		clearField(getDriver().findElement(By.cssSelector(PRICE_LIST_NAME_INPUT_CSS)));
	}

	/**
	 * Clears price list description.
	 */
	public void clearPriceListDescription() {
		clearField(getDriver().findElement(By.cssSelector(PRICE_LIST_DESCRIPTION_INPUT_CSS)));
	}

	/**
	 * Clears price list currency.
	 */
	public void clearPriceListCurrency() {
		clearField(getDriver().findElement(By.cssSelector(CURRENCY_CODE_INPUT_CSS)));
	}

	/**
	 * Clears all fields.
	 */
	public void clearAll() {
		clearPriceListName();
		clearPriceListDescription();
		clearPriceListCurrency();
	}

	/**
	 * Selects Prices tab.
	 */
	public void selectPricesTab() {
		resizeWindow(PRICES_TAB_CSS);
		click(getDriver().findElement(By.cssSelector(PRICES_TAB_CSS)));
	}

	/**
	 * Selects Price List Summary tab.
	 */
	public void selectPriceListSummaryTab() {
		resizeWindow(PRICE_LIST_SUMMARY_TAB_CSS);
		click(getDriver().findElement(By.cssSelector(PRICE_LIST_SUMMARY_TAB_CSS)));
	}

	/**
	 * Close Price List Editor.
	 *
	 * @param priceListName String
	 */
	public void closePriceListEditor(final String priceListName) {
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(String.format(PRICE_LIST_EDITOR_CLOSE_ICON_CSS, priceListName))));
	}

	/**
	 * Clicks Add Price button.
	 *
	 * @return the product editor dialog.
	 */
	public PriceEditorDialog clickAddPriceButton() {
		clickButton(ADD_PRICE_BUTTON_CSS, "Add Price", PriceEditorDialog.PRICE_EDITOR_PARENT_CSS);
		return new PriceEditorDialog(getDriver());
	}

	/**
	 * Clicks Delete Price button.
	 */
	public void clickDeletePriceButton() {
		clickButton(DELETE_PRICE_BUTTON_CSS, "Delete Price");
		new ConfirmDialog(getDriver()).clickOKButton("PriceListManagerMessages.BaseAmount_Delete");
	}

	/**
	 * Clicks Edit Price button.
	 *
	 * @return the product editor dialog.
	 */
	public PriceEditorDialog clickEditPriceButton() {
		clickButton(EDIT_PRICE_BUTTON_CSS, "Edit Price", PriceEditorDialog.PRICE_EDITOR_PARENT_CSS);
		return new PriceEditorDialog(getDriver());
	}

	/**
	 * Clicks Open Item button.
	 *
	 * @return the product editor.
	 */
	public ProductEditor clickOpenItemButton() {
		clickButton(OPEN_ITEM_BUTTON_CSS, "Open Item", ProductEditor.PRODUCT_EDITOR_PARENT_CSS);
		return new ProductEditor(getDriver());
	}

	/**
	 * Clicks Search button.
	 */
	public void clickSearchButton() {
		clickButton(SEARCH_BUTTON_CSS, "Search");
	}

	/**
	 * Verify product code is present in price list.
	 *
	 * @param productCode the product code.
	 */
	public void verifyProductCodeIsPresentInPriceList(final String productCode) {
		selectPriceRowByColumnName(productCode, COLUMN_PRODUCT_CODE);
	}

	/**
	 * Verify sku code is present in price list.
	 *
	 * @param skuCode the sku code.
	 */
	public void verifySkuCodeIsPresentInPriceList(final String skuCode) {
		selectPriceRowByColumnName(skuCode, COLUMN_SKU_CODE);
	}

	/**
	 * Verify product name is present in price list.
	 *
	 * @param productName the product name.
	 */
	public void verifyProductNameIsPresentInPriceList(final String productName) {
		selectPriceRowByColumnName(productName, COLUMN_PRODUCT_NAME);
	}

	/**
	 * Verify product code is not present in price list.
	 *
	 * @param productCode the product code.
	 */
	public void verifyProductCodeIsNotPresentInPriceList(final String productCode) {
		setWebDriverImplicitWait(1);
		assertThat(verifyItemIsNotInEditorPane(BASE_AMOUNT_PARENT_CSS, BASE_AMOUNT_COLUMN_CSS, productCode, COLUMN_PRODUCT_CODE))
				.as("Product code should not be in price list - " + productCode)
				.isFalse();
		setWebDriverImplicitWaitToDefault();
	}

	/**
	 * Select product row by sku or product code.
	 *
	 * @param value      the column value
	 * @param columnName the column name
	 */
	public void selectPriceRowByColumnName(final String value, final String columnName) {
		assertThat(selectItemInEditorPane(BASE_AMOUNT_PARENT_CSS, BASE_AMOUNT_COLUMN_CSS, value, columnName))
				.as("Unable to find " + columnName + " - " + value)
				.isTrue();
	}

	/**
	 * Verify list price in price list.
	 *
	 * @param productName the produce name.
	 * @param listPrice   the list price.
	 */
	public void verifyListPriceInPriceList(final String productName, final String listPrice) {
		verifyProductPrice(productName, listPrice);
	}

	/**
	 * Verify sale price in price list.
	 *
	 * @param productName the product name.
	 * @param salePrice   the sale price.
	 */
	public void verifySalePriceInPriceList(final String productName, final String salePrice) {
		verifyProductPrice(productName, salePrice);
	}

	/**
	 * Verify product price.
	 *
	 * @param productName the produce name.
	 * @param price       the price.
	 */
	private void verifyProductPrice(final String productName, final String price) {
		getWaitDriver().waitForElementsToBeNotStale(getDriver().findElements(By.cssSelector(String.format(PRICE_LIST_ROW_CSS, productName))));
		WebElement row = getWaitDriver().waitForElementToBeClickable(By.cssSelector(String.format(PRICE_LIST_ROW_CSS, productName)));
		getWaitDriver().waitForElementsToBeNotStale(getDriver().findElements(By.cssSelector(String.format(PRICE_LIST_COLUMN_CSS, price))));
		assertThat(row.findElement(By.cssSelector(String.format(PRICE_LIST_COLUMN_CSS, price))).getText())
				.as("Price validation failed")
				.isEqualTo(price);
	}

	/**
	 * Search a price list by price.
	 *
	 * @param searchFromPrice Search from price.
	 * @param searchToPrice   Search to price.
	 */
	public void searchByPrice(final String searchFromPrice, final String searchToPrice) {
		clearAndType(getDriver().findElement(By.xpath(PRICE_LIST_SEARCH_FROM_INPUT_XPATH)), searchFromPrice);
		clearAndType(getDriver().findElement(By.xpath(PRICE_LIST_SEARCH_TO_INPUT_XPATH)), searchToPrice);
		click(By.xpath(PRICE_LIST_SEARCH_BUTTON_XPATH));
	}

	/**
	 * Filter a price list by price.
	 *
	 * @param filterFromPrice Filter from price.
	 * @param filterToPrice   Filter to price.
	 */
	public void filterByPrice(final String filterFromPrice, final String filterToPrice) {
		clearAndType(getDriver().findElement(By.xpath(PRICE_LIST_FILTER_FROM_INPUT_XPATH)), filterFromPrice);
		clearAndType(getDriver().findElement(By.xpath(PRICE_LIST_FILTER_TO_INPUT_XPATH)), filterToPrice);
		click(By.xpath(PRICE_LIST_FILTER_BUTTON_XPATH));
	}

	/**
	 * Verify number of proces displayed in table.
	 *
	 * @param expResultsReturned number of results expected to be returned
	 */
	public void verifyResultsReturned(final int expResultsReturned) {
		Integer pricesRowCount = getDriver().findElements(By.xpath(PRICES_TABLE_ROWS_XPATH)).size();
		if (pricesRowCount != expResultsReturned) {
			sleep(Constants.SLEEP_FIVE_SECONDS_IN_MILLIS);
		}
		assertThat(pricesRowCount)
				.as("incorrect number of results returned")
				.isEqualTo(expResultsReturned);
	}
}

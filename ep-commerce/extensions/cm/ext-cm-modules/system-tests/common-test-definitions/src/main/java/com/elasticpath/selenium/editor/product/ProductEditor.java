package com.elasticpath.selenium.editor.product;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.dialogs.ConfirmDialog;
import com.elasticpath.selenium.dialogs.SelectACategoryDialog;
import com.elasticpath.selenium.editor.SkuDetailsEditor;
import com.elasticpath.selenium.util.Constants;
import com.elasticpath.selenium.wizards.AddSkuWizard;

/**
 * Product Editor.
 */
public class ProductEditor extends AbstractPageObject {

	/**
	 * Page Object Id.
	 */
	public static final String PRODUCT_EDITOR_PARENT_CSS = "div[pane-location='editor-pane'] div[active-editor='true'] ";
	private static final String PRODUCT_NAME_INPUT_CSS = PRODUCT_EDITOR_PARENT_CSS + "div[automation-id='com.elasticpath.cmclient.catalog"
			+ ".CatalogMessages.ProductEditorSummaySection_ProductName'][widget-type='Text'] > input";
	private static final String TAB_CSS = "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages"
			+ ".Product%sPage_Title'][seeable='true']";
	private static final String CATEGORY_ASSIGNMENT_MERCHANDISING_CATALOG_TAB_CSS = PRODUCT_EDITOR_PARENT_CSS
			+ "div[widget-id='%s'][seeable='true']";
	private static final String PRODUCT_EDITOR_WITH_PRODUCT_CODE_CLOSE_ICON_CSS = "div[pane-location='editor-pane'] "
			+ "[widget-id*='%s'][appearance-id='ctab-item'][active-tab='true'] > div[style*='.gif']";
	private static final String ADD_SKU_BUTTON_CSS = PRODUCT_EDITOR_PARENT_CSS + "div[widget-id='Add SKU...'][seeable='true']";
	private static final String OPEN_SKU_BUTTON_CSS = PRODUCT_EDITOR_PARENT_CSS + "div[widget-id='Open SKU...'][seeable='true']";
	private static final String REMOVE_SKU_BUTTON_CSS = PRODUCT_EDITOR_PARENT_CSS + "div[widget-id='Remove SKU...'][seeable='true']";
	private static final String SKU_DETAILS_ITEM_PARENT_CSS = "div[widget-id='Multi Sku'][widget-type='Table'] ";
	private static final String SKU_DETAILS_ITEM_COLUMN_CSS = SKU_DETAILS_ITEM_PARENT_CSS + "div[column-id='%s']";
	private static final String ADD_CATEGORY_BUTTON_CSS = PRODUCT_EDITOR_PARENT_CSS + "div[widget-id='Add Category...'][seeable='true']";
	private static final String REMOVE_CATEGORY_BUTTON_CSS = PRODUCT_EDITOR_PARENT_CSS + "div[widget-id='Remove Category...'][seeable='true']";
	private static final String CATEGORY_DETAILS_ITEM_PARENT_CSS = "div[widget-id='Product Category'][widget-type='Table'] ";
	private static final String CATEGORY_DETAILS_ITEM_COLUMN_CSS = CATEGORY_DETAILS_ITEM_PARENT_CSS + "div[column-id='%s']";
	private static final String PRODUCT_EDITOR = "div[widget-id='%s'][appearance-id='ctab-item']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public ProductEditor(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Verify product name.
	 *
	 * @param productName the product name.
	 */
	public void verifyProductName(final String productName) {
		assertThat(getWaitDriver().waitForElementToBeVisible(By.cssSelector(PRODUCT_NAME_INPUT_CSS)).getAttribute("value"))
				.as("Product name validation failed")
				.isEqualTo(productName);
	}

	/**
	 * Clicks on tab to select it.
	 *
	 * @param tabName the tab name.
	 */
	public void selectTab(final String tabName) {
		String cssSelector = String.format(TAB_CSS, tabName);
		resizeWindow(cssSelector);
		getWaitDriver().waitForElementToBeInteractable(cssSelector);
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(cssSelector)));
	}

	/**
	 * Verifies Catalog Tab is not present.
	 *
	 * @param catalogName the catalog name.
	 */
	public void verifyCatalogTabIsNotPresent(final String catalogName) {
		setWebDriverImplicitWait(1);
		assertThat((isElementPresent(By.cssSelector(String.format(CATEGORY_ASSIGNMENT_MERCHANDISING_CATALOG_TAB_CSS, catalogName)))))
				.as("Category Assignment tab shows unexpected catalog tab -" + catalogName)
				.isFalse();
		setWebDriverImplicitWaitToDefault();
	}

	/**
	 * Verifies Catalog Tab is not present.
	 *
	 * @param catalogName the catalog name.
	 */
	public void verifyCatalogTabIsPresent(final String catalogName) {
		getWaitDriver().waitForElementToBeInteractable(PRODUCT_EDITOR_PARENT_CSS);
		setWebDriverImplicitWait(Constants.IMPLICIT_WAIT_FOR_ELEMENT_THREE_SECONDS);
		assertThat((isElementPresent(By.cssSelector(String.format(CATEGORY_ASSIGNMENT_MERCHANDISING_CATALOG_TAB_CSS, catalogName)))))
				.as("Category Assignment tab does not show catalog tab " + catalogName)
				.isTrue();
		setWebDriverImplicitWaitToDefault();
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
	 * Checks that product name is there and is readOnly.
	 */
	public void tryProductNameChange() {
		getWaitDriver().waitForElementToBePresent(By.cssSelector(PRODUCT_NAME_INPUT_CSS + "[readOnly]"));
	}

	/**
	 * Close Product Editor with the given product code.
	 *
	 * @param productCode the product code.
	 */
	public void closeProductEditor(final String productCode) {
		String formattedTabCSS = String.format(PRODUCT_EDITOR_WITH_PRODUCT_CODE_CLOSE_ICON_CSS, productCode);
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(formattedTabCSS)));
		waitTillElementDisappears(By.cssSelector(formattedTabCSS));
	}

	/**
	 * Close Product Editor with the given product code without saving changes.
	 *
	 * @param productCode the product code.
	 */
	public void closeProductEditorWithoutSave(final String productCode) {
		String formattedTabCSS = String.format(PRODUCT_EDITOR_WITH_PRODUCT_CODE_CLOSE_ICON_CSS, productCode);
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(formattedTabCSS)));
		new ConfirmDialog(getDriver()).clickNoButton("com.elasticpath.cmclient.core.CoreMessages.AbstractCmClientFormEditor_OkTitle_save");
		waitTillElementDisappears(By.cssSelector(formattedTabCSS));
	}

	/**
	 * Click add sku button.
	 *
	 * @return the sku dialog.
	 */
	public AddSkuWizard clickAddSkuButton() {
		clickButton(ADD_SKU_BUTTON_CSS, "Add SKU...");
		return new AddSkuWizard(getDriver());
	}

	/**
	 * Selects sku from product.
	 *
	 * @param skuDetails the sku details
	 */
	public void selectSkuDetails(final String skuDetails) {
		verifySkuDetails(skuDetails);
	}

	/**
	 * Verifies sku details.
	 *
	 * @param skuDetails the product sku
	 */
	public void verifySkuDetails(final String skuDetails) {
		assertThat(selectItemInEditorPaneWithScrollBar(SKU_DETAILS_ITEM_PARENT_CSS, SKU_DETAILS_ITEM_COLUMN_CSS, skuDetails))
				.as("Unable to find sku option - " + skuDetails)
				.isTrue();
	}

	/**
	 * Click open sku button.
	 *
	 * @return the sku editor.
	 */
	public SkuDetailsEditor clickOpenSkuButton() {
		clickButton(OPEN_SKU_BUTTON_CSS, "Open SKU...");
		return new SkuDetailsEditor(getDriver());
	}

	/**
	 * Clicks remove sku button.
	 */
	public void clickRemoveSkuDetailsButton() {
		clickButton(REMOVE_SKU_BUTTON_CSS, "Remove Selection");
	}

	/**
	 * Verifies Product Sku is deleted.
	 *
	 * @param skuDetails String
	 */
	public void verifyProductSkuIsDeleted(final String skuDetails) {
		setWebDriverImplicitWait(Constants.IMPLICIT_WAIT_FOR_ELEMENT_NOT_EXISTS);
		assertThat(verifyItemIsNotInEditorPaneWithScrollBar(SKU_DETAILS_ITEM_PARENT_CSS, SKU_DETAILS_ITEM_COLUMN_CSS, skuDetails))
				.as("Delete failed, sku is still in the list - " + skuDetails)
				.isFalse();
		setWebDriverImplicitWaitToDefault();
	}

	/**
	 * Click add category button.
	 *
	 * @return the Selects a Category dialog.
	 */
	public SelectACategoryDialog clickAddCategoryButton() {
		clickButton(ADD_CATEGORY_BUTTON_CSS, "Add Category...");
		return new SelectACategoryDialog(getDriver());
	}

	/**
	 * Selects category from product.
	 *
	 * @param categoryDetails the category details
	 */
	public void selectCategoryDetails(final String categoryDetails) {
		verifyCategoryDetails(categoryDetails);
	}

	/**
	 * Verifies category details.
	 *
	 * @param categoryDetails the product category assignment
	 */
	public void verifyCategoryDetails(final String categoryDetails) {
		assertThat(selectItemInEditorPaneWithScrollBar(CATEGORY_DETAILS_ITEM_PARENT_CSS, CATEGORY_DETAILS_ITEM_COLUMN_CSS, categoryDetails))
				.as("Unable to find category details - " + categoryDetails)
				.isTrue();
	}

	/**
	 * Clicks remove category button.
	 */
	public void clickRemoveCategoryButton() {
		clickButton(REMOVE_CATEGORY_BUTTON_CSS, "Remove Category");
	}


	/**
	 * Selects product's editor.
	 *
	 * @param productName the change set name
	 */
	public void selectproductEditor(final String productName) {
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(String.format(PRODUCT_EDITOR, productName))));
	}

}

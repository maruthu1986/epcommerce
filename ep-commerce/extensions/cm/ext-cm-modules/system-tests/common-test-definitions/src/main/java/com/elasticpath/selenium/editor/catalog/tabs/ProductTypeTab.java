package com.elasticpath.selenium.editor.catalog.tabs;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.wizards.AddEditProductTypeWizard;

/**
 * ProductTypeTab.
 */
public class ProductTypeTab extends AbstractPageObject {
	private static final String PRODUCT_TYPE_PARENT_CSS = "div[widget-id='Catlog Product Types'][widget-type='Table'][seeable='true'] ";
	private static final String PRODUCT_TYPE_COLUMN_CSS = PRODUCT_TYPE_PARENT_CSS + "div[column-id='%s']";
	private static final String BUTTON_CSS
			= "div[automation-id='com.elasticpath.cmclient.core.CoreMessages.Button_%s'][seeable='true']";
	private static final String TAB_CSS
			= "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.Catalog%sPage_Title'][seeable='true']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public ProductTypeTab(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Clicks add product type button.
	 *
	 * @return AddProductTypeDialog
	 */
	public AddEditProductTypeWizard clickAddProductTypeButton() {
		clickButton("Add");
		return new AddEditProductTypeWizard(getDriver());
	}

	/**
	 * Verifies product type.
	 *
	 * @param productType the product type
	 */
	public void verifyProductType(final String productType) {
		assertThat(selectItemInEditorPaneWithScrollBar(PRODUCT_TYPE_PARENT_CSS, PRODUCT_TYPE_COLUMN_CSS, productType))
				.as("Unable to find product type - " + productType)
				.isTrue();
	}

	/**
	 * Selects product type.
	 *
	 * @param productType the product type
	 */
	public void selectProductType(final String productType) {
		verifyProductType(productType);
	}

	/**
	 * Clicks edit category type button.
	 *
	 * @return AddEditCategoryTypeDialog
	 */
	public AddEditProductTypeWizard clickEditProductTypeButton() {
		clickButton("Edit");
		return new AddEditProductTypeWizard(getDriver());
	}


	/**
	 * Clicks remove product type button.
	 */
	public void clickRemoveProductTypeButton() {
		clickButton("Remove");
	}

	/**
	 * Clicks button.
	 *
	 * @param buttonName the button name
	 */
	private void clickButton(final String buttonName) {
		clickButton(String.format(BUTTON_CSS, buttonName), buttonName);
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
	 * Verify product type is deleted.
	 *
	 * @param productType the product type
	 */
	public void verifyProductTypeDelete(final String productType) {
		setWebDriverImplicitWait(1);
		assertThat(verifyItemIsNotInEditorPaneWithScrollBar(PRODUCT_TYPE_PARENT_CSS, PRODUCT_TYPE_PARENT_CSS, productType))
				.as("Delete failed, product type is still in the list - " + productType)
				.isFalse();
		setWebDriverImplicitWaitToDefault();
	}

}

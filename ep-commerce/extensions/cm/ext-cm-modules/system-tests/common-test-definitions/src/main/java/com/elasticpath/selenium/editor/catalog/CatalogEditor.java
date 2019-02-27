package com.elasticpath.selenium.editor.catalog;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.dialogs.AddAttributeDialog;
import com.elasticpath.selenium.dialogs.AddEditBrandDialog;
import com.elasticpath.selenium.dialogs.EditAttributeDialog;

/**
 * Catalog Editor.
 */
public class CatalogEditor extends AbstractPageObject {

	private static final String ATTRIBUTE_PARENT_CSS = "div[widget-id='Catalog Attributes'][widget-type='Table'][seeable='true'] ";
	private static final String ATTRIBUTE_COLUMN_CSS = ATTRIBUTE_PARENT_CSS + "div[column-id='%s']";
	private static final String TAB_CSS
			= "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.Catalog%sPage_Title'][seeable='true']";
	/**
	 * CSS selector template for some buttons in CatalogEditor.
	 */
	protected static final String BUTTON_CSS
			= "div[automation-id='com.elasticpath.cmclient.core.CoreMessages.Button_%s'][seeable='true']";
	private static final String CATALOG_EDITOR = "div[widget-id='%s'][appearance-id='ctab-item']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public CatalogEditor(final WebDriver driver) {
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
	 * Selects catalog editor.
	 *
	 * @param catalogName the catalog name
	 */
	public void selectCatalogEditor(final String catalogName) {
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(String.format(CATALOG_EDITOR, catalogName))));
	}

	/**
	 * Verifies catalog attribute value.
	 *
	 * @param attributValue the attribute value.
	 */
	public void verifyCatalogAttributeValue(final String attributValue) {
		assertThat(selectItemInEditorPaneWithScrollBar(ATTRIBUTE_PARENT_CSS, ATTRIBUTE_COLUMN_CSS, attributValue))
				.as("Unable to find attribute value - " + attributValue)
				.isTrue();
	}

	/**
	 * Verify catalog attribute is deleted.
	 *
	 * @param attributeValue the attribute value.
	 */
	public void verifyCatalogAttributeDelete(final String attributeValue) {
		setWebDriverImplicitWait(1);
		assertThat(verifyItemIsNotInEditorPaneWithScrollBar(ATTRIBUTE_PARENT_CSS, ATTRIBUTE_COLUMN_CSS, attributeValue))
				.as("Delete failed, attribute is still in the list - " + attributeValue)
				.isFalse();
		setWebDriverImplicitWaitToDefault();
	}

	/**
	 * Selects catalog attribute value.
	 *
	 * @param attributValue the attribute value.
	 */
	public void selectCatalogAttributeValue(final String attributValue) {
		assertThat(selectItemInEditorPaneWithScrollBar(ATTRIBUTE_PARENT_CSS, ATTRIBUTE_COLUMN_CSS, attributValue))
				.as("Unable to find attribute value - " + attributValue)
				.isTrue();
	}

	/**
	 * Clicks button.
	 *
	 * @param buttonName the button name
	 */
	/*public void clickButton(final String buttonName) {
		clickButton(String.format(BUTTON_CSS, buttonName), buttonName);
	}*/

	/**
	 * Clicks add attribute button.
	 *
	 * @return AddAttributeDialog
	 */
	public AddAttributeDialog clickAddAttributeButton() {
		clickButton(String.format(BUTTON_CSS, "Add"), "Add", AddAttributeDialog.ADD_ATTRUBUTE_PARENT_CSS);
		return new AddAttributeDialog(getDriver());
	}

	/**
	 * Clicks edit attribute button.
	 *
	 * @return EditAttributeDialog
	 */
	public EditAttributeDialog clickEditAttributeButton() {
		clickButton(String.format(BUTTON_CSS, "Edit"), "Edit", EditAttributeDialog.EDIT_ATTRUBUTE_PARENT_CSS);
		return new EditAttributeDialog(getDriver());
	}

	/**
	 * Clicks remove attribute button.
	 */
	public void clickRemoveAttributeButton() {
		clickButton(String.format(BUTTON_CSS, "Remove"), "Remove");
	}

	/**
	 * Clicks Add Brand.
	 *
	 * @return AddEditBrandDialog.
	 */
	public AddEditBrandDialog clickAddBrandButton() {
		final String dialogName = "Add";
		clickButton(dialogName, String.format(AddEditBrandDialog.ADD_EDIT_BRAND_DIALOGCSS_TEMPLATE, dialogName));
		return new AddEditBrandDialog(getDriver(), dialogName);
	}

	/**
	 * Select Brands Tab.
	 */
	public void selectBrandsTab() {
		selectTab("Brands");
	}

}
package com.elasticpath.selenium.editor.catalog.tabs;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.dialogs.AddEditSkuOptionDialog;
import com.elasticpath.selenium.dialogs.AddEditSkuOptionValueDialog;

/**
 * SkuOptionsTab.
 */
public class SkuOptionsTab extends AbstractPageObject {
	private static final String SKU_OPTION_EDIT_BUTTON =
			"[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.CatalogSkuOptionsSection_EditSelectionButton']";
	private static final String SKU_OPTION_PARENT_CSS = "[active-editor='true'] [appearance-id='tree'] ";
	private static final String SKU_OPTION_COLUMN_CSS = SKU_OPTION_PARENT_CSS + "div[column-id*='%s']";
	private static final String SKU_OPTION_ROW_CSS = SKU_OPTION_PARENT_CSS + "div[row-id='%s']";
	private static final String SKU_OPTION_ADD_BUTTON =
			"[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.CatalogSkuOptionsSection_AddSkuOptionButton']";
	private static final String SKU_OPTION_REMOVE_BUTTON =
			"[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.CatalogSkuOptionsSection_RemoveSelectionButton']";
	private static final String SKU_OPTION_VALUE_ADD_BUTTON =
			"[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.CatalogSkuOptionsSection_AddSkuOptionValueButton']";
	private static final String SKU_OPTION_TREE_EXPAND_ICON_CSS = SKU_OPTION_ROW_CSS + " div[expand-icon='']";
	private static final String TAB_CSS
			= "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.Catalog%sPage_Title'][seeable='true']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public SkuOptionsTab(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Clicks add SkuOption button.
	 *
	 * @return AddEditSkuOptionDialog
	 */
	public AddEditSkuOptionDialog clickAddSkuOptionButton() {
		clickButton(SKU_OPTION_ADD_BUTTON, "Add Sku Option", AddEditSkuOptionDialog.ADD_SKU_OPTION_PARENT_CSS);
		return new AddEditSkuOptionDialog(getDriver());
	}

	/**
	 * Clicks add SkuOption Value button.
	 *
	 * @return AddEditSkuOptionValueDialog
	 */
	public AddEditSkuOptionValueDialog clickAddSkuOptionValueButton() {
		clickButton(SKU_OPTION_VALUE_ADD_BUTTON, "Add Sku Option Value");
		return new AddEditSkuOptionValueDialog(getDriver());
	}

	/**
	 * Verifies sku option type.
	 *
	 * @param skuOption the sku option type
	 */
	public void verifySkuOption(final String skuOption) {
		assertThat(selectItemInEditorPaneWithScrollBar(SKU_OPTION_PARENT_CSS, SKU_OPTION_COLUMN_CSS, skuOption))
				.as("Unable to find sku option - " + skuOption)
				.isTrue();
	}

	/**
	 * Clicks edit SkuOption button.
	 */
	public void clickEditSkuOptionButton() {
		clickButton(SKU_OPTION_EDIT_BUTTON, "Edit Selection");
	}

	/**
	 * Verifies sku option value exists.
	 *
	 * @param skuOption      the sku option
	 * @param skuOptionValue the sku option value
	 */
	public void verifySkuOptionValue(final String skuOption, final String skuOptionValue) {
		assertThat(selectItemInEditorPaneWithScrollBar(SKU_OPTION_PARENT_CSS, SKU_OPTION_COLUMN_CSS, skuOption))
				.as("Unable to find sku option - " + skuOption)
				.isTrue();
		expandSkuOptionTreeRow(String.format(SKU_OPTION_TREE_EXPAND_ICON_CSS, skuOption));
		final String treeItemCss = String.format(SKU_OPTION_COLUMN_CSS, skuOptionValue);
		assertThat(selectTreeItem(treeItemCss, skuOptionValue))
				.as("Unable to find sku option value- " + skuOptionValue)
				.isTrue();
	}

	/**
	 * Expand the sku option tree row with the given expand icon css.
	 *
	 * @param expandIconFullCss the full css selector of the expand icon.
	 */
	public void expandSkuOptionTreeRow(final String expandIconFullCss) {
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(expandIconFullCss)));
		getWaitDriver().waitFor(1);
	}

	/**
	 * Selects sku option.
	 *
	 * @param skuOption the sku option
	 */
	public void selectSkuOption(final String skuOption) {
		verifySkuOption(skuOption);
	}

	/**
	 * Clicks remove sku option button.
	 */
	public void clickRemoveSkuOptionButton() {
		clickButton(SKU_OPTION_REMOVE_BUTTON, "Remove Selection");
	}

	/**
	 * Verifies sku option type.
	 *
	 * @param skuOption the sku option type
	 */
	public void verifySkuOptionDelete(final String skuOption) {
		setWebDriverImplicitWait(1);
		assertThat(verifyItemIsNotInEditorPaneWithScrollBar(SKU_OPTION_PARENT_CSS, SKU_OPTION_COLUMN_CSS, skuOption))
				.as("Unable to find sku option - " + skuOption)
				.isFalse();
		setWebDriverImplicitWaitToDefault();
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
	 * Selects item from any tree.
	 *
	 * @param treeItemCss the tree item css.
	 * @param value       the tree item text.
	 * @return true if selected.
	 */
	public boolean selectTreeItem(final String treeItemCss, final String value) {
		boolean itemExists = false;
		if (isElementPresent(By.cssSelector(treeItemCss))) {
			WebElement treeItem = getDriver().findElement(By.cssSelector(treeItemCss));
			String treeItemText = treeItem.getText();
			//using treeItemText.contains(value) instead of treeItemText.equals(value) because sometimes
			//treeItemText contains &bnsp sequences at the beginning of the value.
			if (treeItemText != null && treeItemText.contains(value)) {
				click(treeItem);
				itemExists = true;
			}
		}
		return itemExists;
	}
}

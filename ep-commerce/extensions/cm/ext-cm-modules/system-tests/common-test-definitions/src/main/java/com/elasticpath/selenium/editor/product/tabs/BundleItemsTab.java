package com.elasticpath.selenium.editor.product.tabs;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.dialogs.AddItemDialog;
import com.elasticpath.selenium.util.Constants;

/**
 * Bundle Items Tab class.
 */
public class BundleItemsTab extends AbstractPageObject {
	/**
	 * Page Object Id.
	 */
	public static final String BUNDLE_ITEM_PARENT_CSS = "div[widget-id='Constituents'][widget-type='Table'] ";
	private static final String BUNDLE_ITEM_COLUMN_CSS = BUNDLE_ITEM_PARENT_CSS + "div[column-id='%s']";
	private static final String BUNDLE_ITEM_EDIT_BUTTON =
			"[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.ProductEditorContituentSection_EditButton']";
	private static final String BUNDLE_ITEM_ADD_BUTTON =
			"[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.ProductEditorContituentSection_AddButton']";
	private static final String BUNDLE_ITEM_REMOVE_BUTTON =
			"[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.ProductEditorContituentSection_RemoveButton']";
	private static final String REMOVE_ITEM_DIALOG_OK_BUTTON =
			"div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.ProductEditorContituentSection_DialogRemoveTitle'] "
					+ "div[widget-id='OK']";
	private static final String EDIT_ITEM_DIALOG_OK_BUTTON =
			"div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.ProductBundleEditConstituentsDialog_Title'] "
					+ "div[widget-id='OK']";
	private static final String EDIT_ITEM_DIALOG_QUANTITY_FIELD =
			"div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.ProductBundleEditConstituentsDialog_Title'] "
					+ "div[widget-id='Quantity'] input";
	private static final String TAB_CSS
			= "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.Product%sPage_Title'][seeable='true']";
	private static final String BUNDLE_SELECTION_RULE_PARAMETER_CSS = "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages"
			+ ".Bundle_Selection_Parameter'] input";
	private static final String BUNDLE_SELECTION_RULE_CCOMBO_CSS = "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages"
			+ ".Bundle_Selection_Rule'][widget-type='CCombo'][seeable='true']";
	private static final String BUNDLE_SELECTION_RULE_SELECTED_VALUE_CSS = BUNDLE_SELECTION_RULE_CCOMBO_CSS + "  div[appearance-id='ccombo-field']";

	/**
	 * constructor.
	 *
	 * @param driver WebDriver which drives this webpage.
	 */
	public BundleItemsTab(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Clicks 'Add Item' button.
	 *
	 * @return AddItemDialog
	 */
	public AddItemDialog clickAddBundleItemButton() {
		clickButton(BUNDLE_ITEM_ADD_BUTTON, "Add Item", AddItemDialog.ADD_ITEM_PARENT_CSS);
		return new AddItemDialog(getDriver());
	}

	/**
	 * Verifies bundle Item exists.
	 *
	 * @param bundleItemCode the bundle Item code
	 */
	public void verifyBundleItemExists(final String bundleItemCode) {
		assertThat(selectItemInEditorPane(BUNDLE_ITEM_PARENT_CSS, BUNDLE_ITEM_COLUMN_CSS, bundleItemCode, "Product Code"))
				.as("Unable to find bundle Item - " + bundleItemCode)
				.isTrue();
	}

	/**
	 * Verifies bundle Item does not exist.
	 *
	 * @param bundleItemCode the bundle Item code
	 */
	public void verifyBundleItemDeleted(final String bundleItemCode) {
		waitTillElementDisappears(By.cssSelector(REMOVE_ITEM_DIALOG_OK_BUTTON));
		setWebDriverImplicitWait(Constants.IMPLICIT_WAIT_FOR_ELEMENT_NOT_EXISTS);
		assertThat(verifyItemIsNotInEditorPane(BUNDLE_ITEM_PARENT_CSS, BUNDLE_ITEM_COLUMN_CSS, bundleItemCode, "Product Code"))
				.as("Unable to find bundle Item - " + bundleItemCode)
				.isFalse();
		setWebDriverImplicitWaitToDefault();
	}

	/**
	 * Clicks edit BundleItem button and changes quantity.
	 *
	 * @param quantity       quantity
	 * @param bundleItemCode the bundle Item code
	 */
	public void editBundleItemQuantityAndVerify(final String bundleItemCode, final String quantity) {
		selectBundleItem(bundleItemCode);
		clickButton(BUNDLE_ITEM_EDIT_BUTTON, "Edit Item");
		clearAndType(EDIT_ITEM_DIALOG_QUANTITY_FIELD, quantity);
		clickButton(EDIT_ITEM_DIALOG_OK_BUTTON, "OK");
		waitTillElementDisappears(By.cssSelector(EDIT_ITEM_DIALOG_OK_BUTTON));
	}

	/**
	 * Verifies bundle item quantity.
	 *
	 * @param bundleItemCode product code
	 * @param quantity       quantity
	 */
	public void verifyBundleItemQuantity(final String bundleItemCode, final String quantity) {
		selectBundleItem(bundleItemCode);
		assertThat(selectItemInEditorPane(BUNDLE_ITEM_PARENT_CSS, BUNDLE_ITEM_COLUMN_CSS, quantity, "Quantity"))
				.as("Unable to find bundle item product code - " + bundleItemCode + " with quantity - " + quantity)
				.isTrue();
	}

	/**
	 * Selects bundle Item.
	 *
	 * @param bundleItem the bundle Item
	 */
	public void selectBundleItem(final String bundleItem) {
		verifyBundleItemExists(bundleItem);
	}

	/**
	 * Clicks remove bundle Item button.
	 *
	 * @param productCode product code.
	 */
	public void removeBundleItem(final String productCode) {
		selectBundleItem(productCode);
		clickButton(BUNDLE_ITEM_REMOVE_BUTTON, "Remove Item");
		clickButton(REMOVE_ITEM_DIALOG_OK_BUTTON, "OK");
	}

//	/**
//	 * Clicks button.
//	 *
//	 * @param automationId the button name
//	 */
//	public void clickButtonWithId(final String automationId) {
//		getWaitDriver().waitForElementToBeInteractable(automationId);
//		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(automationId)));
//	}

	/**
	 * Clicks on tab to select it.
	 *
	 * @param tabName the tab name.
	 */
	public void selectTab(final String tabName) {
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(String.format(TAB_CSS, tabName))));
	}


	/**
	 * Verify bundle selection rule.
	 *
	 * @param expSelectionRule Expected selection rule.
	 */
	public void verifyBundleSelectionRule(final String expSelectionRule) {
		assertThat(getWaitDriver().waitForElementToBeVisible(By.cssSelector(BUNDLE_SELECTION_RULE_SELECTED_VALUE_CSS)).getAttribute("widget-id"))
				.as("Bundle selection rule  verification failed")
				.isEqualTo(expSelectionRule);
	}

	/**
	 * Verify bundle selection rule parameter.
	 *
	 * @param expSelectionParameter Expected selection rule parameter.
	 */
	public void verifyBundleSelectionParameter(final String expSelectionParameter) {
		assertThat(getWaitDriver().waitForElementToBeVisible(By.cssSelector(BUNDLE_SELECTION_RULE_PARAMETER_CSS)).getAttribute("value"))
				.as("Bundle selection rule parameter verification failed")
				.isEqualTo(expSelectionParameter);
	}

	/**
	 * Edit the value of the bundle selection rule parameter.
	 *
	 * @param selectionParameter New value for selection parameter.
	 */
	public void editSelectionParameter(final String selectionParameter) {
		clearAndType(BUNDLE_SELECTION_RULE_PARAMETER_CSS, selectionParameter);
	}

	/**
	 * Select the bundle selection rule.
	 *
	 * @param selectionRule Selection rule.
	 */
	public void editSelectionRule(final String selectionRule) {
		assertThat(selectComboBoxItem(BUNDLE_SELECTION_RULE_CCOMBO_CSS, selectionRule))
				.as("Unable to find selection rule - " + selectionRule)
				.isTrue();
	}
}

package com.elasticpath.selenium.editor.catalog.tabs;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.dialogs.AddEditCartItemModifierGroupDialog;

/**
 * Cart Item Modifier Groups Tab.
 */
public class CartItemModifierGroupsTab extends AbstractPageObject {

	private static final String GROUP_TABLE_CSS = "div[widget-id='Catalog Cart Item'][widget-type='Table'][seeable='true'] ";
	private static final String GROUP_COLUMN_CSS = GROUP_TABLE_CSS + "div[column-id='%s']";
	private static final String BUTTON_CSS
			= "div[automation-id='com.elasticpath.cmclient.core.CoreMessages.Button_%s'][seeable='true']";
	private static final String TAB_CSS
			= "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.Catalog%sPage_Title'][seeable='true']";
	private static final String CART_ITEM_MODIFIERGROUP_EDITOR_TITLE = "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages"
			+ ".CatalogCartItemModifierGroupsPage_Form_Title'][seeable='true']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public CartItemModifierGroupsTab(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Clicks add group button.
	 *
	 * @return AddEditGroupDialog
	 */
	public AddEditCartItemModifierGroupDialog clickAddGroupButton() {
		final String buttonName = "Add";
		clickButton(String.format(BUTTON_CSS, buttonName), buttonName, AddEditCartItemModifierGroupDialog.ADD_CART_ITEM_MODIFIER_GROUP_PARENT_CSS);
		return new AddEditCartItemModifierGroupDialog(getDriver());
	}

	/**
	 * Verifies group.
	 *
	 * @param group the group
	 */
	public void verifyGroup(final String group) {
		assertThat(selectItemInEditorPaneWithScrollBar(GROUP_TABLE_CSS, GROUP_COLUMN_CSS, group))
				.as("Unable to find group - " + group)
				.isTrue();
	}

	/**
	 * Selects group.
	 *
	 * @param group the group
	 */
	public void selectGroup(final String group) {
		verifyGroup(group);
	}

	/**
	 * Clicks edit group button.
	 *
	 * @return AddEditGroupDialog
	 */
	public AddEditCartItemModifierGroupDialog clickEditGroupButton() {
		final String buttonName = "Edit";
		clickButton(String.format(BUTTON_CSS, buttonName), buttonName, AddEditCartItemModifierGroupDialog.ADD_CART_ITEM_MODIFIER_GROUP_PARENT_CSS);
		return new AddEditCartItemModifierGroupDialog(getDriver());
	}

	/**
	 * Clicks remove group button.
	 */
	public void clickRemoveGroupButton() {
		clickButton("Remove");
	}

	/**
	 * Verify group is deleted.
	 *
	 * @param group the group
	 */
	public void verifyGroupDelete(final String group) {
		setWebDriverImplicitWait(1);
		assertThat(verifyItemIsNotInEditorPaneWithScrollBar(GROUP_TABLE_CSS, GROUP_TABLE_CSS, group))
				.as("Delete failed, group is still in the list - " + group)
				.isFalse();
		setWebDriverImplicitWaitToDefault();
	}

	/**
	 * Clicks on tab to select it.
	 *
	 * @param tabName the tab name.
	 */
	@Override
	public void selectTab(final String tabName) {
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(String.format(TAB_CSS, tabName))));
		getWaitDriver().waitForElementToBeVisible(By.cssSelector(CART_ITEM_MODIFIERGROUP_EDITOR_TITLE));
	}

	/**
	 * Clicks button.
	 *
	 * @param buttonName the button name
	 */
	public void clickButton(final String buttonName) {
		clickButton(String.format(BUTTON_CSS, buttonName), buttonName);
	}
}

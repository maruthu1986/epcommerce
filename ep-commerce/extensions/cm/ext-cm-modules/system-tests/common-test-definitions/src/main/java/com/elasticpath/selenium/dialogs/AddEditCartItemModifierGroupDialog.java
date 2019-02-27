package com.elasticpath.selenium.dialogs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.Locale;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.util.Constants;

/**
 * Add Edit Cart Item Modifier Group Dialog.
 */
public class AddEditCartItemModifierGroupDialog extends AbstractDialog {
	/**
	 * CSS selector used to identify the dialog.
	 */
	public static final String ADD_CART_ITEM_MODIFIER_GROUP_PARENT_CSS
			= "div[automation-id*='com.elasticpath.cmclient.catalog.CatalogMessages.GroupAddEditDialog'][widget-type='Shell'] ";
	private static final String FIELD_TABLE_CSS = "div[widget-id='Cart Modifier Group'][widget-type='Table'][seeable='true'] ";
	private static final String FIELD_COLUMN_CSS = FIELD_TABLE_CSS + "div[column-id='%s']";
	private static final String GROUP_NAME_INPUT_CSS = ADD_CART_ITEM_MODIFIER_GROUP_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.GroupAddEditDialog_GroupName'][widget-type='CCombo']"
			+ "+[widget-type='Text'] input";
	private static final String GROUP_CODE_INPUT_CSS = ADD_CART_ITEM_MODIFIER_GROUP_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.GroupAddEditDialog_GroupCode'] input";
	private static final String ADD_BUTTON_CSS = ADD_CART_ITEM_MODIFIER_GROUP_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.AddEditCartItemModifierFieldOptionDialog_Add']";
	private static final String OK_BUTTON_CSS = ADD_CART_ITEM_MODIFIER_GROUP_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.core.CoreMessages.AbstractEpDialog_ButtonOK']";
	private static final String ADD_FIELD_BUTTON_CSS = ADD_CART_ITEM_MODIFIER_GROUP_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.GroupAddEditDialog_TableAddButton']";
	private static final String REMOVE_FIELD_BUTTON_CSS = ADD_CART_ITEM_MODIFIER_GROUP_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.GroupAddEditDialog_TableRemoveButton']";
	private static final String EDIT_FIELD_BUTTON_CSS = ADD_CART_ITEM_MODIFIER_GROUP_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.GroupAddEditDialog_TableEditButton']";
	private static final String MOVE_UP_BUTTON_CSS = ADD_CART_ITEM_MODIFIER_GROUP_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.core.CoreMessages.button_MoveUp']";
	private static final String MOVE_DOWN_BUTTON_CSS = ADD_CART_ITEM_MODIFIER_GROUP_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.core.CoreMessages.button_MoveDown']";


	private String groupName;

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public AddEditCartItemModifierGroupDialog(final WebDriver driver) {
		super(driver);
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(final String groupName) {
		this.groupName = groupName;
	}

	/**
	 * Inputs cart item modifier group code.
	 *
	 * @param groupCode the cart item modifier group code
	 */
	public void enterModifierGroupCode(final String groupCode) {
		clearAndType(GROUP_CODE_INPUT_CSS, groupCode);
	}

	/**
	 * Inputs cart item modifier group name.
	 *
	 * @param groupName the cart item modifier group name
	 */
	public void enterModifierGroupName(final String groupName) {
		clearAndType(GROUP_NAME_INPUT_CSS, groupName);
		this.groupName = groupName;
	}

	/**
	 * Clicks 'Add Field' button.
	 *
	 * @return AddEditCartItemModifierGroupFieldDialog
	 */
	public AddEditCartItemModifierGroupFieldDialog clickAddFieldButton() {
		final String buttonName = "Add Field";
		clickButton(String.format(ADD_FIELD_BUTTON_CSS, buttonName), buttonName, AddEditCartItemModifierGroupFieldDialog
				.ADD_CART_ITEM_MODIFIER_FIELD_PARENT_CSS);
		return new AddEditCartItemModifierGroupFieldDialog(getDriver());
	}

	/**
	 * Clicks 'Edit Field' button.
	 *
	 * @return AddEditCartItemModifierGroupFieldDialog
	 */
	public AddEditCartItemModifierGroupFieldDialog clickEditFieldButton() {
		final String buttonName = "Edit Field";
		clickButton(String.format(EDIT_FIELD_BUTTON_CSS, buttonName), buttonName, AddEditCartItemModifierGroupFieldDialog
				.ADD_CART_ITEM_MODIFIER_FIELD_PARENT_CSS);
		return new AddEditCartItemModifierGroupFieldDialog(getDriver());
	}

	/**
	 * Clicks 'Remove Field' button.
	 */
	public void clickRemoveFieldButton() {
		clickButton("Remove Field");
	}

	/**
	 * Clicks 'Move up' button.
	 */
	public void clickMoveUpButton() {
		clickButton("Move Up");
	}

	/**
	 * Clicks 'Move down' button.
	 */
	public void clickMoveDownButton() {
		clickButton("Move down");
	}

	/**
	 * Clicks add button.
	 */
	public void clickAddButton() {
		clickButton(ADD_BUTTON_CSS, "Add");
		waitTillElementDisappears(By.cssSelector(ADD_CART_ITEM_MODIFIER_GROUP_PARENT_CSS));
	}

	/**
	 * Clicks ok button.
	 */
	public void clickOkButton() {
		clickButton(OK_BUTTON_CSS, "OK");
		waitTillElementDisappears(By.cssSelector(ADD_CART_ITEM_MODIFIER_GROUP_PARENT_CSS));
	}

	/**
	 * Verifies field.
	 *
	 * @param field the field
	 */
	public void verifyField(final String field) {
		assertThat(selectItemInEditorPaneWithScrollBar(FIELD_TABLE_CSS, FIELD_COLUMN_CSS, field))
				.as("Unable to find field - " + field)
				.isTrue();
	}

	/**
	 * Verifies field deleted.
	 *
	 * @param field the field
	 */
	public void verfiyFieldDelete(final String field) {
		setWebDriverImplicitWait(1);
		assertThat(verifyItemIsNotInEditorPaneWithScrollBar(FIELD_TABLE_CSS, FIELD_COLUMN_CSS, field))
				.as("Delete failed, field is still in the list - " + field)
				.isFalse();
		setWebDriverImplicitWaitToDefault();
	}

	/**
	 * Selects field.
	 *
	 * @param field the field
	 */
	public void selectField(final String field) {
		verifyField(field);
	}

	/**
	 * Clicks button.
	 *
	 * @param buttonName the button name
	 */
	private void clickButton(final String buttonName) {
		String buttonNameLowerCase = buttonName.toLowerCase(Locale.ENGLISH);
		String buttonCss;
		switch (buttonNameLowerCase) {
			case "add field":
				buttonCss = ADD_FIELD_BUTTON_CSS;
				break;
			case "remove field":
				buttonCss = REMOVE_FIELD_BUTTON_CSS;
				break;
			case "edit field":
				buttonCss = EDIT_FIELD_BUTTON_CSS;
				break;
			case "move up":
				buttonCss = MOVE_UP_BUTTON_CSS;
				break;
			case "move down":
				buttonCss = MOVE_DOWN_BUTTON_CSS;
				break;
			default:
				fail("the specified button does not exist in this dialog. Please check button text");
				return;
		}

		clickButton(String.format(buttonCss, buttonNameLowerCase), buttonName);
		sleep(Constants.SLEEP_HALFSECOND_IN_MILLIS);
	}

}

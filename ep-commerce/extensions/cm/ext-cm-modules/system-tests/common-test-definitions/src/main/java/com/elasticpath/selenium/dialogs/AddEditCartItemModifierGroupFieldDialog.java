package com.elasticpath.selenium.dialogs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.Locale;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.util.Utility;

/**
 * Add Edit Cart Item Modifier Group Field Dialog.
 */
public class AddEditCartItemModifierGroupFieldDialog extends AbstractDialog {
	/**
	 * CSS selector used to identify the dialog.
	 */
	public static final String ADD_CART_ITEM_MODIFIER_FIELD_PARENT_CSS
			= "div[automation-id*='com.elasticpath.cmclient.catalog.CatalogMessages."
			+ "CatalogCartItemModifierGroupsSectionAddEditDialog_WinIitle'][widget-type='Shell'] ";
	private static final String DISPLAY_NAME_INPUT_CSS = ADD_CART_ITEM_MODIFIER_FIELD_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages."
			+ "AddEditCartItemModifierFieldDialog_DisplayName'][widget-type='CCombo']"
			+ "+[widget-type='Text'] input";
	private static final String FIELD_CODE_INPUT_CSS = ADD_CART_ITEM_MODIFIER_FIELD_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.AddEditCartItemModifierFieldDialog_Code'] input";
	private static final String FIELD_TYPE_INPUT_CSS = ADD_CART_ITEM_MODIFIER_FIELD_PARENT_CSS
			+ "[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages."
			+ "AddEditCartItemModifierFieldDialog_FieldType'][widget-type='CCombo']";
	private static final String FIELD_SIZE_INPUT_CSS = ADD_CART_ITEM_MODIFIER_FIELD_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.AddEditCartItemModifierFieldDialog_MaxSize'] input";
	private static final String OPTION_TABLE_CSS = "div[widget-id='Cart Modifier Field'][widget-type='Table'][seeable='true'] ";
	private static final String OPTION_COLUMN_CSS = OPTION_TABLE_CSS + "div[column-id='%s']";
	private static final String ADD_BUTTON_CSS = ADD_CART_ITEM_MODIFIER_FIELD_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.AddEditCartItemModifierFieldDialog_Add']";
	private static final String OK_BUTTON_CSS = ADD_CART_ITEM_MODIFIER_FIELD_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.core.CoreMessages.AbstractEpDialog_ButtonOK']";
	private static final String ADD_OPTION_BUTTON_CSS = ADD_CART_ITEM_MODIFIER_FIELD_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.AddEditCartItemModifierFieldDialog_TableAddButton']";
	private static final String REMOVE_OPTION_BUTTON_CSS = ADD_CART_ITEM_MODIFIER_FIELD_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.AddEditCartItemModifierFieldDialog_TableRemoveButton']";
	private static final String EDIT_OPTION_BUTTON_CSS = ADD_CART_ITEM_MODIFIER_FIELD_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.AddEditCartItemModifierFieldDialog_TableEditButton']";
	private static final String MOVE_UP_BUTTON_CSS = ADD_CART_ITEM_MODIFIER_FIELD_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.core.CoreMessages.button_MoveUp']";
	private static final String MOVE_DOWN_BUTTON_CSS = ADD_CART_ITEM_MODIFIER_FIELD_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.core.CoreMessages.button_MoveDown']";
	private AddEditCartItemModifierFieldOptionDialog fieldOptionDialog;

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public AddEditCartItemModifierGroupFieldDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Creates an option for this field.
	 *
	 * @param optueionValue the option code.
	 * @param optionName    the option name.
	 */
	public void createOption(final String optueionValue, final String optionName) {
		fieldOptionDialog = clickAddOptionButton();
		fieldOptionDialog.enterFieldOptionCode(optueionValue);
		fieldOptionDialog.enterFieldOptionDisplayName(optionName);
		fieldOptionDialog.clickAddButton();
	}

	/**
	 * Inputs cart item modifier field code.
	 *
	 * @param fieldCode the cart item modifier field code
	 */
	public void enterModifierFieldCode(final String fieldCode) {
		clearAndType(FIELD_CODE_INPUT_CSS, fieldCode);
	}

	/**
	 * Inputs cart item display name.
	 *
	 * @param displayName the cart item  display name
	 */
	public void enterDisplayName(final String displayName) {
		clearAndType(DISPLAY_NAME_INPUT_CSS, displayName);
	}

	/**
	 * Selects field type.
	 *
	 * @param type field type to select.
	 */
	public void selectFieldType(final String type) {
		assertThat(selectComboBoxItem(FIELD_TYPE_INPUT_CSS, type))
				.as("Unable to field type - " + type)
				.isTrue();
	}

	/**
	 * Inputs cart item size.
	 *
	 * @param size the cart item  size
	 */
	public void enterMaxSize(final String size) {
		clearAndType(FIELD_SIZE_INPUT_CSS, size);
	}


	/**
	 * Clicks 'Add Option' button.
	 *
	 * @return AddEditCartItemModifierFieldOptionDialog
	 */
	private AddEditCartItemModifierFieldOptionDialog clickAddOptionButton() {
		clickButton("Add Option", new String[]{AddEditCartItemModifierFieldOptionDialog.ADD_FIELD_OPTION_PARENT_CSS});
		return new AddEditCartItemModifierFieldOptionDialog(getDriver());
	}

	/**
	 * Clicks 'Edit Option' button.
	 *
	 * @return AddEditCartItemModifierFieldOptionDialog
	 */
	public AddEditCartItemModifierFieldOptionDialog clickEditOptionButton() {
		clickButton("Edit Option", new String[]{AddEditCartItemModifierFieldOptionDialog.ADD_FIELD_OPTION_PARENT_CSS});
		return new AddEditCartItemModifierFieldOptionDialog(getDriver());
	}

	/**
	 * Clicks 'Remove Option' button.
	 *
	 * @return AddEditCartItemModifierFieldOptionDialog
	 */
	public AddEditCartItemModifierFieldOptionDialog clickRemoveOptionButton() {
		clickButton("Remove Option", new String[]{AddEditCartItemModifierFieldOptionDialog.ADD_FIELD_OPTION_PARENT_CSS});
		return new AddEditCartItemModifierFieldOptionDialog(getDriver());
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
		waitTillElementDisappears(By.cssSelector(ADD_CART_ITEM_MODIFIER_FIELD_PARENT_CSS));
	}

	/**
	 * Clicks ok button.
	 */
	public void clickOkButton() {
		clickButton(OK_BUTTON_CSS, "OK");
		waitTillElementDisappears(By.cssSelector(ADD_CART_ITEM_MODIFIER_FIELD_PARENT_CSS));
	}


	/**
	 * Edits option name and verifies the edited option name.
	 *
	 * @param optionName Option Name.
	 */
	public void editAndVerifyOptionName(final String optionName) {
		//select old display name
		selectOption(optionName);
		clickEditOptionButton();
		String editedOptionName = "EditOptionName" + "_" + Utility.getRandomUUID();
		fieldOptionDialog.enterFieldOptionDisplayName(editedOptionName);
		fieldOptionDialog.clickOkButton();
		//select new/edited display name
		selectOption(editedOptionName);

	}

	/**
	 * Verifies option.
	 *
	 * @param option the option
	 */
	public void verifyOption(final String option) {
		assertThat(selectItemInEditorPaneWithScrollBar(OPTION_TABLE_CSS, OPTION_COLUMN_CSS, option))
				.as("Unable to find option - " + option)
				.isTrue();
	}

	/**
	 * Selects option.
	 *
	 * @param option the option
	 */
	public void selectOption(final String option) {
		verifyOption(option);
	}

	/**
	 * Clicks button.
	 *
	 * @param buttonName   the button name
	 * @param pageObjectId optional argument page object id
	 */
	public void clickButton(final String buttonName, final String... pageObjectId) {
		String buttonNameLowerCase = buttonName.toLowerCase(Locale.ENGLISH);
		String buttonCss;
		switch (buttonNameLowerCase) {
			case "add option":
				buttonCss = ADD_OPTION_BUTTON_CSS;
				break;
			case "remove option":
				buttonCss = REMOVE_OPTION_BUTTON_CSS;
				break;
			case "edit option":
				buttonCss = EDIT_OPTION_BUTTON_CSS;
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
		if (pageObjectId.length > 0) {
			clickButton(String.format(buttonCss, buttonNameLowerCase), buttonName, pageObjectId[0]);
		} else {
			clickButton(String.format(buttonCss, buttonNameLowerCase), buttonName);
		}
	}

}

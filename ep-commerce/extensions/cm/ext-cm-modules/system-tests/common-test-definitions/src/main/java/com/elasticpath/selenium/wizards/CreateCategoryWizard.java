package com.elasticpath.selenium.wizards;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.dialogs.EditDecimalValueAttributeDialog;
import com.elasticpath.selenium.dialogs.EditLongTextAttributeDialog;
import com.elasticpath.selenium.dialogs.EditShortTextAttributeDialog;

/**
 * Create Category Wizard.
 */
public class CreateCategoryWizard extends AbstractWizard {

	private static final String CREATE_CATEGORY_PARENT_CSS = "div[widget-id='Create Category'][widget-type='Shell'] ";
	private static final String CATEGORY_CODE_INPUT_CSS = CREATE_CATEGORY_PARENT_CSS + "div[widget-id='Category Code'] input";
	private static final String CATEGORY_NAME_INPUT_CSS = CREATE_CATEGORY_PARENT_CSS + "div[widget-id='Category Name'] input";
	private static final String CATEGORY_TYPE_COMBO_CSS = CREATE_CATEGORY_PARENT_CSS + "div[widget-id='Category Type'][widget-type='CCombo']";
	private static final String ENABLE_DATE_TIME_CALENDAR_ICON_CSS = CREATE_CATEGORY_PARENT_CSS + "div[widget-id='Enable Date/Time'] "
			+ "div[style*='.png']";
	private static final String STORE_VISIBLE_CHECKBOX_CSS = CREATE_CATEGORY_PARENT_CSS + "div[widget-type='Button'] > div[style*='e53cf03a.png']";
	private static final String CALENDAR_OK_BUTTON_CSS = "div[widget-id= 'Edit Date and Time Value'] div[widget-id='OK'][seeable='true']";
	private static final String CREATE_CATEGORY_ATTRIBUTE_PARENT_CSS = "div[widget-id='Attributes'] ";
	private static final String CREATE_CATEGORY_ATTRIBUTE_COLUMN_CSS = CREATE_CATEGORY_ATTRIBUTE_PARENT_CSS + "div[column-id='%s']";
	private static final String EDIT_ATTRIBUTE_VALUE_BUTTON_CSS = CREATE_CATEGORY_PARENT_CSS + "div[widget-id='Edit Attribute Value...']";
	private static final String ATTRIBUTE_NAME_COLUMNNAME = "Name";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public CreateCategoryWizard(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Inputs category code.
	 *
	 * @param categoryCode the category code.
	 */
	public void enterCategoryCode(final String categoryCode) {
		clearAndType(CATEGORY_CODE_INPUT_CSS, categoryCode);
	}

	/**
	 * Inputs category name.
	 *
	 * @param categoryName the category name.
	 */
	public void enterCategoryName(final String categoryName) {
		clearAndType(CATEGORY_NAME_INPUT_CSS, categoryName);
	}

	/**
	 * Selects category type in combo box.
	 *
	 * @param categoryType the category type.
	 */
	public void selectCategoryType(final String categoryType) {
		assertThat(selectComboBoxItem(CATEGORY_TYPE_COMBO_CSS, categoryType))
				.as("Unable to find category type - " + categoryType)
				.isTrue();
	}

	/**
	 * Enters current enable date/time.
	 */
	public void enterCurrentEnableDateTime() {
		click(getDriver().findElement(By.cssSelector(ENABLE_DATE_TIME_CALENDAR_ICON_CSS)));
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(CALENDAR_OK_BUTTON_CSS)));
	}

	/**
	 * Clicks to select store visible check box.
	 */
	public void checkStoreVisibleBox() {
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(STORE_VISIBLE_CHECKBOX_CSS)));
	}

	/**
	 * Click edit attribute button for long text.
	 *
	 * @return the dialog.
	 */
	public EditLongTextAttributeDialog clickEditAttributeButtonLongText() {
		clickEditAttributeValueButton(EditLongTextAttributeDialog.PARENT_EDIT_LONG_TEXT_CSS);
		return new EditLongTextAttributeDialog(getDriver());
	}

	/**
	 * Click edit attribute button for Decimal value.
	 *
	 * @return the dialog.
	 */
	public EditDecimalValueAttributeDialog clickEditAttributeButtonDecimalValue() {
		clickEditAttributeValueButton(EditDecimalValueAttributeDialog.PARENT_DECIMAL_VALUE_CSS);
		return new EditDecimalValueAttributeDialog(getDriver());
	}

	/**
	 * Click edit attribute button for short text.
	 *
	 * @return the dialog.
	 */
	public EditShortTextAttributeDialog clickEditAttributeButtonShortText() {
		clickEditAttributeValueButton(EditShortTextAttributeDialog.PARENT_EDIT_SHORT_TEXT_CSS);
		return new EditShortTextAttributeDialog(getDriver());
	}

	/**
	 * Clicks the Edit Attribute Value button.
	 */
	private void clickEditAttributeValueButton(final String pageObjectId) {
		clickButton(EDIT_ATTRIBUTE_VALUE_BUTTON_CSS, "Edit Attribute Value", pageObjectId);
	}

	/**
	 * Enter attribute long text.
	 *
	 * @param value         the value.
	 * @param attributeName the attribute name.
	 */
	public void enterAttributeLongText(final String value, final String attributeName) {
		assertThat(selectItemInDialog(CREATE_CATEGORY_ATTRIBUTE_PARENT_CSS, CREATE_CATEGORY_ATTRIBUTE_COLUMN_CSS, attributeName,
				ATTRIBUTE_NAME_COLUMNNAME))
				.as("Unable to find attribute - " + attributeName)
				.isTrue();

		EditLongTextAttributeDialog editLongTextAttributeDialog = clickEditAttributeButtonLongText();
		editLongTextAttributeDialog.enterLongTextValue(value);
		editLongTextAttributeDialog.clickOKButton();
	}

	/**
	 * Enter attribute decimal value.
	 *
	 * @param value         the value.
	 * @param attributeName the attribute name.
	 */
	public void enterAttributeDecimalValue(final String value, final String attributeName) {
		assertThat(selectItemInDialog(CREATE_CATEGORY_ATTRIBUTE_PARENT_CSS, CREATE_CATEGORY_ATTRIBUTE_COLUMN_CSS, attributeName,
				ATTRIBUTE_NAME_COLUMNNAME))
				.as("Unable to find attribute - " + attributeName)
				.isTrue();

		EditDecimalValueAttributeDialog editDecimalValueAttributeDialog = clickEditAttributeButtonDecimalValue();
		editDecimalValueAttributeDialog.enterDecimalValue(value);
		editDecimalValueAttributeDialog.clickOKButton();
	}

	/**
	 * Enter attribute short text.
	 *
	 * @param value         the value.
	 * @param attributeName the attribute name.
	 */
	public void enterAttributeShortText(final String value, final String attributeName) {
		assertThat(selectItemInDialog(CREATE_CATEGORY_ATTRIBUTE_PARENT_CSS, CREATE_CATEGORY_ATTRIBUTE_COLUMN_CSS, attributeName,
				ATTRIBUTE_NAME_COLUMNNAME))
				.as("Unable to find attribute - " + attributeName)
				.isTrue();

		EditShortTextAttributeDialog editShortTextAttributeDialog = clickEditAttributeButtonShortText();
		editShortTextAttributeDialog.enterShortTextValue(value);
		editShortTextAttributeDialog.clickOKButton();
	}

}
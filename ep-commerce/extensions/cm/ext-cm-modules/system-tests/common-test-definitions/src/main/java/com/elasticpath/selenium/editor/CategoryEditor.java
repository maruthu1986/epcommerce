package com.elasticpath.selenium.editor;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.dialogs.ConfirmDialog;
import com.elasticpath.selenium.dialogs.EditLongTextAttributeDialog;
import com.elasticpath.selenium.util.Constants;

/**
 * Category Editor.
 */
public class CategoryEditor extends AbstractPageObject {

	private static final String ATTRIBUTE_PARENT_CSS = "div[widget-id='Attributes'][widget-type='Table'] ";
	private static final String ATTRIBUTE_COLUMN_CSS = ATTRIBUTE_PARENT_CSS + "div[column-id='%s']";
	private static final String CATEGORY_TYPE_INPUT_CSS = "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages"
			+ ".CategoryEditorOverviewSection_CategoryType'][widget-id='Category Type'] div[widget-id='%s']";
	private static final String CATEGORY_SUMMARY_PARENT_CSS = "div[automation-id='categorySummary'][seeable='true'] ";
	private static final String CATEGORY_NAME_INPUT_CSS = CATEGORY_SUMMARY_PARENT_CSS + "div[widget-id='Category Name'] input";
	private static final String CATEGORY_STORE_VISIBLE_CHECKBOX_CSS = CATEGORY_SUMMARY_PARENT_CSS
			+ "div[widget-type='Button'][appearance-id='check-box']";
	private static final String CATEGORY_TYPE_COMBOMBOX_CSS = CATEGORY_SUMMARY_PARENT_CSS + "div[widget-id='Category Type'][widget-type='CCombo']";
	private static final String ATTRIBUTE_EDIT_VALUE_BUTTON_CSS = "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages"
			+ ".AttributePage_ButtonEdit']";
	private static final String CLEAR_ATTRIBUTE_VALUE_BUTTON_CSS = "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages."
			+ "AttributePage_ButtonReset']";
	private static final String STORE_VISIBLE_UNCHECKED = "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.Item_StoreVisible'] "
			+ "+ div[appearance-id='check-box'] > div[style*='e53cf03a.png']";
	private static final String STORE_VISIBLE_CHECKED = "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.Item_StoreVisible'] "
			+ "+ div[appearance-id='check-box'] > div[style*='1882de9d.png']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public CategoryEditor(final WebDriver driver) {
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
	 * Verifies attribute value.
	 *
	 * @param attributValue the attribute value.
	 */
	public void verifyAttributeValue(final String attributValue) {
		assertThat(selectItemInEditorPane(ATTRIBUTE_PARENT_CSS, ATTRIBUTE_COLUMN_CSS, attributValue, ""))
				.as("Unable to find attribute value - " + attributValue)
				.isTrue();
		new ConfirmDialog(getDriver()).clickCancelButton();
	}

	/**
	 * Verifies category type value.
	 *
	 * @param categoryType the category type.
	 */
	public void verifyCategoryTypeValue(final String categoryType) {
		assertThat(getWaitDriver().waitForElementToBeVisible(By.cssSelector(String.format(CATEGORY_TYPE_INPUT_CSS, categoryType))).isDisplayed())
				.as("Expected category type not present.")
				.isTrue();
	}

	/**
	 * Edts category name.
	 *
	 * @param newCategoryName String
	 */
	public void enterNewCategoryName(final String newCategoryName) {
		clearAndType(CATEGORY_NAME_INPUT_CSS, newCategoryName);
	}

	/**
	 * Selects a category type.
	 *
	 * @param newCategoryType String
	 */
	public void selectCategoryType(final String newCategoryType) {
		assertThat(selectComboBoxItem(CATEGORY_TYPE_COMBOMBOX_CSS, newCategoryType))
				.as("Unable to find category type - " + newCategoryType)
				.isTrue();
	}

	/**
	 * Clicks store visibility checkbox.
	 */
	public void changeStoreVisibility() {
		if (getDriver().findElement(By.cssSelector(STORE_VISIBLE_CHECKED)).isDisplayed()) {

			click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(CATEGORY_STORE_VISIBLE_CHECKBOX_CSS)));
		}
	}

	/**
	 * Verifies category name.
	 *
	 * @param expCategoryName String
	 */
	public void verifyCategoryName(final String expCategoryName) {
		assertThat(getWaitDriver().waitForElementToBeVisible(By.cssSelector(String.format(CATEGORY_NAME_INPUT_CSS, expCategoryName))).isDisplayed())
				.as("Expected category name not present.")
				.isTrue();
	}

	/**
	 * Checks Category Store Visibility status.
	 */
	public void checkCategoryStoreVisibilityFalse() {
		setWebDriverImplicitWait(Constants.IMPLICIT_WAIT_FOR_ELEMENT_NOT_EXISTS);
		try {
			getDriver().findElement(By.cssSelector(CATEGORY_NAME_INPUT_CSS)).click();
			getDriver().findElement(By.cssSelector(STORE_VISIBLE_UNCHECKED));
		} catch (Exception e) {
			assertThat(false)
					.as("Store visible is still selected.")
					.isTrue();
		}
		setWebDriverImplicitWaitToDefault();
	}

	/**
	 * Clicks edit value for a category attributes.
	 *
	 * @param attributeName String
	 * @param newValue      String
	 * @return Edit Long Text Attribute dialog
	 */
	public EditLongTextAttributeDialog editCategoryAttributeValue(final String attributeName, final String newValue) {
		assertThat(selectItemInDialog(ATTRIBUTE_PARENT_CSS, ATTRIBUTE_COLUMN_CSS, attributeName, "Name"))
				.as("Unable to find setting - " + attributeName)
				.isTrue();
		clickButton(ATTRIBUTE_EDIT_VALUE_BUTTON_CSS, "Edit Value Button", EditLongTextAttributeDialog.PARENT_EDIT_LONG_TEXT_CSS);
		return new EditLongTextAttributeDialog(getDriver());
	}

	/**
	 * Clears the value of a given attribute.
	 *
	 * @param attributeName String
	 */
	public void clickClearAttribute(final String attributeName) {
		assertThat(selectItemInDialog(ATTRIBUTE_PARENT_CSS, ATTRIBUTE_COLUMN_CSS, attributeName, "Name"))
				.as("Unable to find setting - " + attributeName)
				.isTrue();
		clickButton(CLEAR_ATTRIBUTE_VALUE_BUTTON_CSS, "Clear Atrribute Value");
	}
}

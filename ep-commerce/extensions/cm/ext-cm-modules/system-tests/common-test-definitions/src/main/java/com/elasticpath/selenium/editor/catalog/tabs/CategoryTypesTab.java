package com.elasticpath.selenium.editor.catalog.tabs;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.dialogs.AddEditCategoryTypeDialog;

/**
 * CategoryTypesTab.
 */
public class CategoryTypesTab extends AbstractPageObject {

	private static final String CATEGORY_TYPE_PARENT_CSS = "div[widget-id='Catalog Category Types'][widget-type='Table'][seeable='true'] ";
	private static final String CATEGORY_TYPE_COLUMN_CSS = CategoryTypesTab.CATEGORY_TYPE_PARENT_CSS + "div[column-id='%s']";
	private static final String BUTTON_CSS
			= "div[automation-id='com.elasticpath.cmclient.core.CoreMessages.Button_%s'][seeable='true']";
	private static final String TAB_CSS
			= "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.Catalog%sPage_Title'][seeable='true']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public CategoryTypesTab(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Clicks add category type button.
	 *
	 * @return AddEditCategoryTypeDialog
	 */
	public AddEditCategoryTypeDialog clickAddCategoryTypeButton() {
		final String buttonName = "Add";
		clickButton(String.format(BUTTON_CSS, buttonName), buttonName, AddEditCategoryTypeDialog.ADD_CATEGORY_TYPE_PARENT_CSS);
		return new AddEditCategoryTypeDialog(getDriver());
	}

	/**
	 * Verifies category type.
	 *
	 * @param categoryType the category type
	 */
	public void verifyCategoryType(final String categoryType) {
		assertThat(selectItemInEditorPaneWithScrollBar(CATEGORY_TYPE_PARENT_CSS, CATEGORY_TYPE_COLUMN_CSS, categoryType))
				.as("Unable to find category type - " + categoryType)
				.isTrue();
	}

	/**
	 * Selects category type.
	 *
	 * @param categoryType the category type
	 */
	public void selectCategoryType(final String categoryType) {
		verifyCategoryType(categoryType);
	}

	/**
	 * Clicks edit category type button.
	 */
	public void clickEditCategoryTypeButton() {
		clickButton("Edit");
	}

	/**
	 * Clicks remove category type button.
	 */
	public void clickRemoveCategoryTypeButton() {
		clickButton("Remove");
	}

	/**
	 * Verify category type is deleted.
	 *
	 * @param categoryType the category type
	 */
	public void verifyCategoryTypeDelete(final String categoryType) {
		setWebDriverImplicitWait(1);
		assertThat(verifyItemIsNotInEditorPaneWithScrollBar(CATEGORY_TYPE_PARENT_CSS, CATEGORY_TYPE_PARENT_CSS, categoryType))
				.as("Delete failed, category type is still in the list - " + categoryType)
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
	 * Clicks button.
	 *
	 * @param buttonName the button name
	 */
	public void clickButton(final String buttonName) {
		clickButton(String.format(BUTTON_CSS, buttonName), buttonName);
	}
}

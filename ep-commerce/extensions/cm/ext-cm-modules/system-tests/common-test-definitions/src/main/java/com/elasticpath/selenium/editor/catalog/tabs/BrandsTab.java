package com.elasticpath.selenium.editor.catalog.tabs;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.dialogs.AddEditBrandDialog;
import com.elasticpath.selenium.dialogs.ConfirmDialog;
import com.elasticpath.selenium.dialogs.ErrorDialog;
import com.elasticpath.selenium.editor.catalog.CatalogEditor;

/**
 * Brands Tab.
 */
public class BrandsTab extends CatalogEditor {

	private static final String BRAND_COLUMN_CSS = "div[column-id='%s']";
	private static final String BRANDS_TABLE_PARENT_CSS = "div[widget-id='Catalog Brands'][widget-type='Table'][seeable='true'] ";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public BrandsTab(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Clicks AddBrandButton.
	 *
	 * @return AddEditBrandDialog
	 */
	public AddEditBrandDialog clickAddBrandButton() {
		final String buttonName = "Add";
		clickButton(String.format(BUTTON_CSS, buttonName), buttonName, String.format(AddEditBrandDialog.ADD_EDIT_BRAND_DIALOGCSS_TEMPLATE,
				buttonName));
		return new AddEditBrandDialog(getDriver(), "Add");
	}

	/**
	 * Verify brand exists.
	 *
	 * @param brandCode the brandCode.
	 */
	public void verifyAndSelectBrand(final String brandCode) {
		assertThat(selectItemInEditorPaneWithScrollBar(BRANDS_TABLE_PARENT_CSS, BRANDS_TABLE_PARENT_CSS + BRAND_COLUMN_CSS, brandCode))
				.as("Unable to find brand code - " + brandCode)
				.isTrue();
	}

	/**
	 * Clicks remove brand button.
	 */
	public void clickRemoveBrandButton() {
		final String buttonName = "Remove";
		final String dialogAutomationId = "catalog.CatalogMessages.CatalogBrandsSection_RemoveDialog_title";
		clickButton(String.format(BUTTON_CSS, buttonName), buttonName, String.format(ConfirmDialog.CONFIRM_OK_BUTTON_CSS, dialogAutomationId));
		new ConfirmDialog(getDriver()).clickOKButton(dialogAutomationId);
	}

	/**
	 * Verify brand is deleted.
	 *
	 * @param brand the brand
	 */
	public void verifyBrandDelete(final String brand) {
		setWebDriverImplicitWait(1);
		assertThat(verifyItemIsNotInEditorPaneWithScrollBar(BRANDS_TABLE_PARENT_CSS, BRAND_COLUMN_CSS, brand))
				.as("Delete failed, brand is still in the list - " + brand)
				.isFalse();
		setWebDriverImplicitWaitToDefault();
	}

	/**
	 * Verify brand name exists.
	 *
	 * @param brandName the brandCode.
	 */
	public void verifyAndSelectBrandByName(final String brandName) {
		assertThat(selectItemInEditorPaneWithScrollBar(BRANDS_TABLE_PARENT_CSS, BRANDS_TABLE_PARENT_CSS + BRAND_COLUMN_CSS, brandName))
				.as("Unable to find brand name - " + brandName)
				.isTrue();
	}

	/**
	 * Clicks EditBrandButton.
	 *
	 * @return AddEditBrandDialog
	 */
	public AddEditBrandDialog clickEditBrandButton() {
		final String dialogName = "Edit";
		clickButton(String.format(BUTTON_CSS, dialogName), dialogName, String.format(AddEditBrandDialog.ADD_EDIT_BRAND_DIALOGCSS_TEMPLATE,
				dialogName));
		return new AddEditBrandDialog(getDriver(), dialogName);
	}

	/**
	 * Verifies error message is displayed.
	 *
	 * @param expErrorMessage String
	 */
	public void verifyErrorMessageDisplayed(final String expErrorMessage) {
		new ErrorDialog(getDriver()).verifyErrorMessage(expErrorMessage);
	}
}
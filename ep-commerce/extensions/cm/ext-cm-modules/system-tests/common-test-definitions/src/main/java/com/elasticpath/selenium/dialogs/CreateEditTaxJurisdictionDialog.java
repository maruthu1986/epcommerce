package com.elasticpath.selenium.dialogs;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Create and Edit Tax Jurisdiction dialogs.
 */
public class CreateEditTaxJurisdictionDialog extends AbstractDialog {

	private final String taxjurisdictionParentCSS;
	private final String taxjurisdictionCountryComboListCSS;
	private final String taxjurisdictionTaxcalculationListCSS;
	private final String saveButtonCSS;
	private final String addTaxButtonCSS;
	private final String editTaxButtonCSS;
	private final String removeTaxButtonCSS;
	private final String taxlistCSS;
	/**
	 * CSS selector used to identify the dialog.
	 */
	public static final String TAXJURISDICTION_PARENT_CSS_TEMPLATE = "div[automation-id*='com.elasticpath.cmclient.admin.taxes.TaxesMessages"
			+ ".%sTaxJurisdictionDialogTitle'][widget-type='Shell'] ";
	private static final String TAXJURISDICTION_COUNTRY_COMBO_LIST_CSS_TEMPLATE = "div[automation-id='com.elasticpath.cmclient.admin.taxes"
			+ ".TaxesMessages.TaxJurisdictionCountry'][widget-type='CCombo']";
	private static final String TAXJURISDICTION_TAXCALCULATION_LIST_CSS_TEAMPLE = "div[automation-id='com.elasticpath.cmclient.admin.taxes"
			+ ".TaxesMessages.TaxJurisdictionCalculationMethod'][widget-type='CCombo']";
	private static final String SAVE_BUTTON_CSS_TEMPLATE = "div[automation-id='com.elasticpath.cmclient.core.CoreMessages"
			+ ".AbstractEpDialog_ButtonSave']";
	private static final String ADD_TAX_BUTTON_CSS_TEMPLATE = "div[automation-id='com.elasticpath.cmclient.admin.taxes.TaxesMessages.AddTax']";
	private static final String EDIT_TAX_BUTTON_CSS_TEMPLATE = "div[automation-id='com.elasticpath.cmclient.admin.taxes.TaxesMessages.EditTax']";
	private static final String REMOVE_TAX_BUTTON_CSS_TEMPLATE = "div[automation-id='com.elasticpath.cmclient.admin.taxes.TaxesMessages.RemoveTax']";
	private static final String TAX_LIST_CSS_TEMPLATE = "div[column-id='%s']";
	private static final String REMOVE_TAX_CATEGORY_OK_BUTTON_CSS = "div[automation-id='com.elasticpath.cmclient.admin.taxes.TaxesMessages"
			+ ".ConfirmDeleteTaxCategoryMsgBoxTitle']";
	private static final String TAX_CATEGORIES_TABLE_PARENT_CSS = "div[widget-id='Tax Categories'][widget-type='Table'] ";
	private static final String TAX_NAME_COLUMN_CSS = "div[parent-widget-id='Tax Categories'] div[column-id='%s']";


	/**
	 * Constructor for CreateEditTaxJurisdictionDialog.
	 *
	 * @param driver     WebDriver which drives this page
	 * @param dialogName String for wild card dialog name
	 */
	public CreateEditTaxJurisdictionDialog(final WebDriver driver, final String dialogName) {
		super(driver);

		taxjurisdictionParentCSS = String.format(TAXJURISDICTION_PARENT_CSS_TEMPLATE, dialogName);
		taxjurisdictionCountryComboListCSS = taxjurisdictionParentCSS + TAXJURISDICTION_COUNTRY_COMBO_LIST_CSS_TEMPLATE;
		taxjurisdictionTaxcalculationListCSS = taxjurisdictionParentCSS + TAXJURISDICTION_TAXCALCULATION_LIST_CSS_TEAMPLE;
		saveButtonCSS = taxjurisdictionParentCSS + SAVE_BUTTON_CSS_TEMPLATE;
		addTaxButtonCSS = taxjurisdictionParentCSS + ADD_TAX_BUTTON_CSS_TEMPLATE;
		editTaxButtonCSS = taxjurisdictionParentCSS + EDIT_TAX_BUTTON_CSS_TEMPLATE;
		removeTaxButtonCSS = taxjurisdictionParentCSS + REMOVE_TAX_BUTTON_CSS_TEMPLATE;
		taxlistCSS = taxjurisdictionParentCSS + TAX_LIST_CSS_TEMPLATE;
	}

	/**
	 * Selects Jurisdiction country in combo box.
	 *
	 * @param country String
	 */
	public void selectJurisdictionCountry(final String country) {
		assertThat(selectComboBoxItem(taxjurisdictionCountryComboListCSS, country))
				.as("Unable to find country - " + country)
				.isTrue();
	}

	/**
	 * Selects Jurisdiction Calculation method in combo box.
	 *
	 * @param method String
	 */
	public void selectJurisdictionCalculation(final String method) {
		assertThat(selectComboBoxItem(taxjurisdictionTaxcalculationListCSS, method))
				.as("Unable to find calculation method - " + method)
				.isTrue();
	}

	/**
	 * Clicks Add Tax button.
	 *
	 * @return AddEditTaxDialog
	 */
	public AddEditTaxDialog clickAddTax() {
		final String dialogName = "Create";
		clickButton(addTaxButtonCSS, "Add Tax", String.format(AddEditTaxDialog.TAX_PARENT_CSS_TEMPLATE, dialogName));
		return new AddEditTaxDialog(getDriver(), dialogName);
	}

	/**
	 * Clicks Edit Tax button.
	 *
	 * @return AddEditTaxDialog
	 */
	public AddEditTaxDialog clickEditTax() {
		final String dialogName = "Edit";
		clickButton(editTaxButtonCSS, "Edit Tax", String.format(AddEditTaxDialog.TAX_PARENT_CSS_TEMPLATE, dialogName));
		return new AddEditTaxDialog(getDriver(), dialogName);
	}

	/**
	 * Clicks Remove Tax button.
	 */
	public void clickRemoveTax() {
		final String pageObjectId = "div[automation-id='com.elasticpath.cmclient.admin.taxes.TaxesMessages.ConfirmDeleteTaxCategoryMsgBoxTitle']";
		clickButton(removeTaxButtonCSS, "Remove Tax", pageObjectId);
		new ConfirmDialog(getDriver()).clickOKButton("com.elasticpath.cmclient.admin.taxes.TaxesMessages.ConfirmDeleteTaxCategoryMsgBoxTitle");
	}

	/**
	 * Clicks save button.
	 */
	@Override
	public void clickSave() {
		clickButton(saveButtonCSS, "Save");
		waitTillElementDisappears(By.cssSelector(taxjurisdictionParentCSS));
	}

	/**
	 * Clicks save button.
	 */
	@Override
	public void clickOK() {
		//TODO
		clickButton(saveButtonCSS, "Save");
		waitTillElementDisappears(By.cssSelector(REMOVE_TAX_CATEGORY_OK_BUTTON_CSS));
	}

	/**
	 * Verifies if Tax List exists.
	 *
	 * @param listTax String
	 */
	public void verifyTaxIsInList(final String listTax) {
		assertThat(selectItemInCenterPaneWithoutPagination(taxjurisdictionParentCSS, taxlistCSS, listTax,
				"Tax Name"))
				.as("Tax Name does not exist in the list - " + listTax)
				.isTrue();
	}

	/**
	 * Verifies the Tax has been removed.
	 *
	 * @param listTax String
	 */
	public void verifyTaxNameIsNotInList(final String listTax) {
		setWebDriverImplicitWait(1);
		assertThat(verifyItemIsNotInCenterPaneWithoutPagination(taxjurisdictionParentCSS, taxlistCSS, listTax,
				"Tax Name"))
				.as("Delete failed, Tax Name is still in the list - " + listTax)
				.isFalse();
		setWebDriverImplicitWaitToDefault();
	}

	/**
	 * Verifies the item in Tax has been removed.
	 *
	 * @param taxName String
	 */
	public void selectItemInTaxTable(final String taxName) {
		selectItemInDialog(TAX_CATEGORIES_TABLE_PARENT_CSS, TAX_NAME_COLUMN_CSS, taxName, "Tax Name");
	}

}

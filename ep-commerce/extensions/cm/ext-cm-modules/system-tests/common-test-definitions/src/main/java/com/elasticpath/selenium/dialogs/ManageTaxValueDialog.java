package com.elasticpath.selenium.dialogs;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;

/**
 * Manage Tax Value steps.
 */
public class ManageTaxValueDialog extends AbstractPageObject {

	/**
	 * CSS selector used to identify the dialog.
	 */
	public static final String MANAGE_TAX_VALUE_PARENT_CSS = "div[automation-id*='com.elasticpath.cmclient.admin.taxes.TaxesMessages"
			+ ".ManageTaxValuesTitleDialog'][widget-type='Shell'] ";
	private static final String MANAGE_TAX_VALUE_TAXJURISDICTION_COMBO_CSS = "div[automation-id='com.elasticpath.cmclient.admin.taxes"
			+ ".TaxesMessages.ManageTaxValuesTaxJurisdictionLabel'][widget-type='CCombo']";
	private static final String MANAGE_TAX_VALUE_TAX_COMBO_CSS = "div[automation-id='com.elasticpath.cmclient.admin.taxes.TaxesMessages"
			+ ".ManageTaxValuesTaxLabel'][widget-type='CCombo']";
	private static final String FILTER_BUTTON_CSS = "div[automation-id='com.elasticpath.cmclient.admin.taxes.TaxesMessages"
			+ ".ManageTaxValuesFilterButton']";
	private static final String MANAGE_TAX_VALUE_OK_BUTTON_CSS = "div[automation-id='com.elasticpath.cmclient.core.CoreMessages"
			+ ".AbstractEpDialog_ButtonOK']";
	private static final String MANAGE_TAX_VALUE_BUTTON_CSS_TEMPLATE = "div[automation-id*='com.elasticpath.cmclient.admin.taxes.TaxesMessages"
			+ ".TaxValue%sLabel']";
	private static final String MANAGE_TAX_SERVICE_VALUE_TABLE_CSS = "div[widget-id='%s']";
	private static final String DELETE_TAX_VALUE_OK_BUTTON_CSS = "div[automation-id='com.elasticpath.cmclient.admin.taxes.TaxesMessages"
			+ ".ConfirmDeleteTaxValueMsgBoxTitle'] div[appearance-id='composite'] div[widget-id='OK']";

	/**
	 * Constructor for the Tax Jurisdiction Pane.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public ManageTaxValueDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Selects Tax Jurisdiction in combo box.
	 *
	 * @param country String
	 */
	public void selectTaxJurisdictionCombo(final String country) {
		assertThat(selectComboBoxItem(MANAGE_TAX_VALUE_TAXJURISDICTION_COMBO_CSS, country))
				.as("Unable to find country combo - " + country)
				.isTrue();
	}

	/**
	 * Selects Tax in combo box.
	 *
	 * @param taxes String
	 */
	public void selectTaxCombo(final String taxes) {
		assertThat(selectComboBoxItem(MANAGE_TAX_VALUE_TAX_COMBO_CSS, taxes))
				.as("Unable to find Tax combo - " + taxes)
				.isTrue();
	}

	/**
	 * Clicks Filter button.
	 */
	public void clickFilterButton() {
		clickButton(FILTER_BUTTON_CSS, "Filter");
	}

	/**
	 * Clicks the Manage Tax Value OK button.
	 */
	public void clickOKButton() {
		clickButton(MANAGE_TAX_VALUE_OK_BUTTON_CSS, "OK");
		waitTillElementDisappears(By.cssSelector(MANAGE_TAX_VALUE_PARENT_CSS));
	}

	/**
	 * Clicks the Delete Tax Value OK button.
	 */
	public void clickDeleteOKButton() {
		clickButton(DELETE_TAX_VALUE_OK_BUTTON_CSS, "OK");
		waitTillElementDisappears(By.cssSelector(DELETE_TAX_VALUE_OK_BUTTON_CSS));
	}

	/**
	 * Clicks Add button.
	 *
	 * @return AddEditTaxRateDialog
	 */
	public AddEditTaxRateDialog clickAddButton() {
		final String add = "Add";
		clickButton(String.format(MANAGE_TAX_VALUE_BUTTON_CSS_TEMPLATE, add), add, String.format(AddEditTaxRateDialog.TAX_RATE_PARENT_CSS_TEMPLATE,
				add));
		return new AddEditTaxRateDialog(getDriver(), add);
	}

	/**
	 * Clicks Edit button.
	 *
	 * @return AddEditTaxRateDialog
	 */
	public AddEditTaxRateDialog clickEditButton() {
		clickButton(String.format(MANAGE_TAX_VALUE_BUTTON_CSS_TEMPLATE, "Edit"), "Edit", AddEditTaxRateDialog.TAX_RATE_PARENT_CSS_TEMPLATE);
		return new AddEditTaxRateDialog(getDriver(), "Edit");
	}

	/**
	 * Clicks Remove button.
	 */
	public void clickRemoveButton() {
		String pageObjectId = "div[automation-id='com.elasticpath.cmclient.admin.taxes.TaxesMessages.ConfirmDeleteTaxValueMsgBoxTitle']";
		clickButton(String.format(MANAGE_TAX_VALUE_BUTTON_CSS_TEMPLATE, "Remove"), "Remove", pageObjectId);
		new ConfirmDialog(getDriver()).clickOKButton("com.elasticpath.cmclient.admin.taxes.TaxesMessages.ConfirmDeleteTaxValueMsgBoxTitle");
	}

	/**
	 * Selects and verify the Manage Tax Value attribute value.
	 *
	 * @param attributeValue the attribute value.
	 */
	public void selectManageTaxAttributeValue(final String attributeValue) {
		assertThat(selectItemInDialog(MANAGE_TAX_VALUE_PARENT_CSS, MANAGE_TAX_SERVICE_VALUE_TABLE_CSS, attributeValue, ""))
				.as("Unable to find Managed Tax Value attribute value - " + attributeValue)
				.isTrue();
	}

	/**
	 * Checks to see if the Manage Tax Value attribute value is deled or not.
	 *
	 * @param attributeValue String
	 */
	public void verifyTaxNameIsNotInList(final String attributeValue) {
		setWebDriverImplicitWait(1);
		assertThat(verifyItemIsNotInCenterPaneWithoutPagination(MANAGE_TAX_VALUE_PARENT_CSS, MANAGE_TAX_SERVICE_VALUE_TABLE_CSS, attributeValue, ""))
				.as("Delete failed, Tax Jurisdiction Country is still in the list - " + attributeValue)
				.isFalse();
		setWebDriverImplicitWaitToDefault();
	}
}
package com.elasticpath.cucumber.definitions;

import cucumber.api.java.en.When;

import com.elasticpath.selenium.dialogs.AddEditTaxRateDialog;
import com.elasticpath.selenium.dialogs.ManageTaxValueDialog;
import com.elasticpath.selenium.setup.SetUp;
import com.elasticpath.selenium.toolbars.ConfigurationActionToolbar;
import com.elasticpath.selenium.util.Utility;

/**
 * Tax value steps.
 */
public class TaxValuesDefinition {

	private final ConfigurationActionToolbar configurationActionToolbar;
	private ManageTaxValueDialog manageTaxValueDialog;
	private AddEditTaxRateDialog addEditTaxRateDialog;
	private static String uniqueSubCountryName = "";

	/**
	 * Constructor for Tax Values.
	 */
	public TaxValuesDefinition() {
		configurationActionToolbar = new ConfigurationActionToolbar(SetUp.getDriver());
	}

	/**
	 * Click Tax Values.
	 */
	@When("^I go to Tax Values")
	public void clickTaxValues() {
		manageTaxValueDialog = configurationActionToolbar.clickTaxValue();
	}

	/**
	 * Adding a Manage Tax Values for selected country.
	 *
	 * @param subCountryName String
	 * @param country        String
	 */
	@When("^I add an sub country tax name (.+) for Tax Jurisdiction country (.+)")
	public void managingTaxValue(final String subCountryName, final String country) {
		uniqueSubCountryName = subCountryName + Utility.getRandomUUID();
		manageTaxValueDialog.selectTaxJurisdictionCombo(country);
		manageTaxValueDialog.clickFilterButton();
		addEditTaxRateDialog = manageTaxValueDialog.clickAddButton();
		addEditTaxRateDialog.enterSubCountryTaxName(uniqueSubCountryName);
		addEditTaxRateDialog.clickSave();
		manageTaxValueDialog.clickOKButton();
	}

	/**
	 * Deleting newly created Tax value name in specified country.
	 *
	 * @param country String
	 */
	@When("^I delete newly created Tax Value for country (.+)$")
	public void deleteTaxCode(final String country) {
		manageTaxValueDialog = configurationActionToolbar.clickTaxValue();
		manageTaxValueDialog.selectTaxJurisdictionCombo(country);
		manageTaxValueDialog.clickFilterButton();
		manageTaxValueDialog.selectManageTaxAttributeValue(uniqueSubCountryName);
		manageTaxValueDialog.clickRemoveButton();
		manageTaxValueDialog.clickOKButton();
	}

	/**
	 * Verify the Tax Name exist in the list.
	 *
	 * @param country String
	 */
	@When("^the newly created sub country Tax Name exists in the list from the Country (.+)$")
	public void verifyTaxNameExists(final String country) {
		manageTaxValueDialog = configurationActionToolbar.clickTaxValue();
		manageTaxValueDialog.selectTaxJurisdictionCombo(country);
		manageTaxValueDialog.clickFilterButton();
		manageTaxValueDialog.selectManageTaxAttributeValue(uniqueSubCountryName);
		manageTaxValueDialog.clickOKButton();
	}

	/**
	 * Verify the Tax Name doesn't exist in the list.
	 *
	 * @param country String
	 */
	@When("^the newly created sub country Tax Name doesn't in the list from the Country (.+)$")
	public void verifyTaxNameIsNotInList(final String country) {
		manageTaxValueDialog = configurationActionToolbar.clickTaxValue();
		manageTaxValueDialog.selectTaxJurisdictionCombo(country);
		manageTaxValueDialog.clickFilterButton();
		manageTaxValueDialog.verifyTaxNameIsNotInList(uniqueSubCountryName);
		manageTaxValueDialog.clickOKButton();
	}

}

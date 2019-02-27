package com.elasticpath.cucumber.definitions;

import java.util.Map;

import cucumber.api.java.After;
import cucumber.api.java.en.When;

import com.elasticpath.selenium.dialogs.AddEditTaxDialog;
import com.elasticpath.selenium.dialogs.CreateEditTaxJurisdictionDialog;
import com.elasticpath.selenium.resultspane.TaxJurisdictionsPane;
import com.elasticpath.selenium.setup.SetUp;
import com.elasticpath.selenium.toolbars.ConfigurationActionToolbar;
import com.elasticpath.selenium.util.Utility;

/**
 * Tax Jurisdiction step definitions.
 */
public class TaxJurisdictionDefinition {

	private final ConfigurationActionToolbar configurationActionToolbar;
	private TaxJurisdictionsPane taxJurisdictionsPane;
	private CreateEditTaxJurisdictionDialog createEditTaxJurisdictionDialog;
	private AddEditTaxDialog addEditTaxDialog;
	private static String uniqueTaxDisplayName = "";
	private String taxJurisdictionCountry = "";

	/**
	 * Constructor for Tax Jurisdiction.
	 */
	public TaxJurisdictionDefinition() {
		configurationActionToolbar = new ConfigurationActionToolbar(SetUp.getDriver());
	}

	/**
	 * Click add Jurisdiction Country combo box.
	 *
	 * @param jurisdictionMap map of the specified strings
	 */
	@When("^I (?:have|create) a Tax Jurisdiction with the following values$")
	public void haveCreateTaxJurisidction(final Map<String, String> jurisdictionMap) {
		taxJurisdictionsPane = configurationActionToolbar.clickTaxJurisdiction();

		createEditTaxJurisdictionDialog = taxJurisdictionsPane.clickCreateTaxJurisdictionButton();
		taxJurisdictionCountry = jurisdictionMap.get("country");

		createEditTaxJurisdictionDialog.selectJurisdictionCountry(jurisdictionMap.get("country"));
		createEditTaxJurisdictionDialog.selectJurisdictionCalculation(jurisdictionMap.get("method"));

		addEditTaxDialog = createEditTaxJurisdictionDialog.clickAddTax();
		addEditTaxDialog.enterTaxCodeName(jurisdictionMap.get("taxName"));
		addEditTaxDialog.selectAddressField(jurisdictionMap.get("addressField"));
		addEditTaxDialog.clickSave();

		createEditTaxJurisdictionDialog.clickSave();
	}

	/**
	 * Click edit tax Jurisdiction Country combo box.
	 *
	 * @param taxDisplayName String
	 * @param country        String
	 * @param taxName        String
	 */
	@When("^I edit the Tax Display Name (.+) in Tax Jurisdiction country (.+) of the Tax Name (.+)$")
	public void editTaxJurisidction(final String taxDisplayName, final String country, final String taxName) {
		createEditTaxJurisdictionDialog = taxJurisdictionsPane.clickEditTaxJurisdictionButton(country);
		createEditTaxJurisdictionDialog.selectItemInTaxTable(taxName);

		uniqueTaxDisplayName = taxDisplayName + Utility.getRandomUUID();
		addEditTaxDialog = createEditTaxJurisdictionDialog.clickEditTax();
		addEditTaxDialog.enterTaxDisplayName(uniqueTaxDisplayName);
		addEditTaxDialog.clickSave();

		createEditTaxJurisdictionDialog.clickSave();
	}

	/**
	 * Click remove Jurisdiction Country combo box.
	 *
	 * @param taxName String
	 * @param country String
	 */
	@When("^I remove tax name (.+) for Tax Jurisdiction country (.+)$")
	public void removeTaxJurisidctionTax(final String taxName, final String country) {
		createEditTaxJurisdictionDialog = taxJurisdictionsPane.clickEditTaxJurisdictionButton(country);
		createEditTaxJurisdictionDialog.selectItemInTaxTable(taxName);

		createEditTaxJurisdictionDialog.clickRemoveTax();
		createEditTaxJurisdictionDialog.clickSave();
	}

	/**
	 * Verify new Tax Jurisdiction exists.
	 *
	 * @param country String
	 */
	@When("^the (?:created|edited) Tax Jurisdiction country (.+) should exist in the list$")
	public void verifyTaxJurisdictionExists(final String country) {
		taxJurisdictionsPane.verifyTaxJurisdictionIsInList(country);
	}

	/**
	 * Verify the list that the Tax no longer exists.
	 *
	 * @param taxName String
	 * @param country String
	 */
	@When("^the removed Tax Name (.+) should no longer exists in Tax Jurisdiction Country (.+)$")
	public void verifyTaxJurisdictionTaxesIsDeleted(final String taxName, final String country) {
		taxJurisdictionsPane.clickEditTaxJurisdictionButton(country);
		createEditTaxJurisdictionDialog.verifyTaxNameIsNotInList(taxName);
		createEditTaxJurisdictionDialog.clickSave();
	}

	/**
	 * Deleting created Tax Jurisdiction countries.
	 *
	 * @param country String
	 */
	@When("^I delete newly created Tax Jurisdiction country (.+)$")
	public void deleteTaxJurisdictionCountry(final String country) {
		taxJurisdictionsPane.deleteTaxJusisdictionCountry(country);
	}

	/**
	 * Verify the Tax Jurisdiction doesn't exist in the list.
	 *
	 * @param country String
	 */
	@When("^the deleted Tax Jurisdiction country (.+) should no longer exist in list$")
	public void verifyTaxJurisdictionIsDeleted(final String country) {
		taxJurisdictionsPane.verifyTaxCodeIsNotInList(country);
	}

	/**
	 * Clean up Tax Jurisdiction list.
	 */
	@After("@cleanupTaxJurisdiction")
	public void cleanupTaxJurisdiction() {
		configurationActionToolbar.clickTaxJurisdiction();
		deleteTaxJurisdictionCountry(this.taxJurisdictionCountry);
		verifyTaxJurisdictionIsDeleted(this.taxJurisdictionCountry);
	}
}

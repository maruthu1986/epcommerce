package com.elasticpath.cucumber.definitions;

import cucumber.api.java.en.When;

import com.elasticpath.selenium.dialogs.CreateEditTaxCodeDialog;
import com.elasticpath.selenium.resultspane.TaxCodesPane;
import com.elasticpath.selenium.setup.SetUp;
import com.elasticpath.selenium.toolbars.ConfigurationActionToolbar;
import com.elasticpath.selenium.util.Utility;

/**
 * Tax Code step definitions.
 */
public class TaxCodeDefinition {

	private final ConfigurationActionToolbar configurationActionToolbar;
	private TaxCodesPane taxCodesPane;
	private CreateEditTaxCodeDialog createEditTaxCodeDialogDialog;
	private static String uniqueTaxCodeName = "";

	/**
	 * Constructor for Tax Code definitions.
	 */
	public TaxCodeDefinition() {
		configurationActionToolbar = new ConfigurationActionToolbar(SetUp.getDriver());
	}

	/**
	 * Click Tax Codes.
	 */
	@When("^I navigate to Tax Codes$")
	public void clickTaxCode() {
		taxCodesPane = configurationActionToolbar.clickTaxCode();
	}

	/**
	 * Create Tax Code.
	 *
	 * @param taxCodeName the name of the tax code.
	 */
	@When("^I create a Tax Code named (.+)$")
	public void createTaxCode(final String taxCodeName) {
		uniqueTaxCodeName = taxCodeName + Utility.getRandomUUID();
		createEditTaxCodeDialogDialog = taxCodesPane.clickCreateTaxCodeButton();

		createEditTaxCodeDialogDialog.enterTaxCodeName(uniqueTaxCodeName);
		createEditTaxCodeDialogDialog.clickSave();
	}

	/**
	 * Verify new shipping region exists.
	 */
	@When("^the new tax code should exist in the list$")
	public void verifyTaxCodeExists() {
		taxCodesPane.verifyTaxCodeIsInList(uniqueTaxCodeName);
	}

	/**
	 * Editing a new Tax Code name.
	 *
	 * @param newTaxCodeName editing the string of the tax code.
	 */
	@When("^I edit a Tax Code named (.+)$")
	public void editTaxCode(final String newTaxCodeName) {
		createEditTaxCodeDialogDialog = taxCodesPane.clickEditTaxCodeButton(uniqueTaxCodeName);

		uniqueTaxCodeName = newTaxCodeName + Utility.getRandomUUID();
		createEditTaxCodeDialogDialog.enterTaxCodeName(uniqueTaxCodeName);
		createEditTaxCodeDialogDialog.clickSave();
	}

	/**
	 * Deleting created Tax Code names.
	 */
	@When("^I delete newly created Tax Code$")
	public void deleteTaxCode() {
		taxCodesPane.deleteTaxCode(uniqueTaxCodeName);
	}

	/**
	 * Verify new tax code no longer exists.
	 */
	@When("^the newly created tax code no longer exists$")
	public void verifyNewTaxCodeIsDeleted() {
		taxCodesPane.verifyTaxCodeIsNotInList(uniqueTaxCodeName);
	}

}

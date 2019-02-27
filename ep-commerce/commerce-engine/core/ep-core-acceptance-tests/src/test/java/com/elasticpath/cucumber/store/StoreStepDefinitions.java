/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cucumber.store;

import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Store test step definitions class.
 * 
 */
public class StoreStepDefinitions {
	
	@Autowired
	private StoreStepDefinitionsHelper storeStepDefinitionsHelper;
	
	/**
	 * Sets up the store with tax jurisdiction for the current testing context.
	 * 
	 * @param taxCalculationMethod the tax calculation method: tax inclusive, or tax exclusive
	 * @param taxJurisdictionRegion the tax jurisdiction region name
	 */
	@Given("^a store with an \\[(.+)\\] tax jurisdiction of \\[(.+)\\]$")
	public void setUpStoreWithTaxJurisdictionOf(final String taxCalculationMethod, final String taxJurisdictionRegion) {
		
		storeStepDefinitionsHelper.setUpTaxJurisdiction(taxCalculationMethod, taxJurisdictionRegion);
	}
	
	/**
	 * Sets up tax categories for the test environment.
	 *
	 * @param dataTable the data of tax categories
	 */
	@And("^with tax rates of$")
	public void setUpTaxCategories(final DataTable dataTable) {
		
		storeStepDefinitionsHelper.setTaxCategories(dataTable.asMaps(String.class, String.class));
	}
	
	/**
	 * Changes tax rates for testing tax rate overriding. 
	 *
	 * @param taxDataTable tax rates data
	 */
	@When("^tax rates are changed to$")
	public void changeTaxRates(final DataTable taxDataTable) {
		
		storeStepDefinitionsHelper.setTaxRatesForStore(taxDataTable.asMaps(String.class, String.class));
	}
}
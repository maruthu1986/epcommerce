/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cucumber.tax;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Step definitions class for operations related to tax calculations on shopping carts in flight.
 *
 * @see OrderTaxStepDefinitions for tax calculations on completed orders.
 */
public class ShoppingCartTaxStepDefinitions {

	@Autowired
	private ShoppingCartTaxStepDefinitionsHelper helper;

	/**
	 * Requests that taxes are calculated on the current contents of the shopping cart.
	 */
	@When("^I request the taxes to be calculated on the shopping cart$")
	public void requestTaxCalculationOnShoppingCart() {
		getHelper().requestTaxCalculationOnShoppingCart();
	}

	/**
	 * Verifies the tax calculation contains the number of values expected.
	 *
	 * @param numberExpected the number of values expected.
	 */
	@Then("^I expect the tax calculation returned to contain (\\d+) tax values$")
	public void verifyTaxCalculationValues(final int numberExpected) {
		getHelper().verifyTaxCalculationValues(numberExpected);
	}

	/**
	 * Verifies the tax calculation contains the number of tax categories expected.
	 *
	 * @param numberExpected the number of tax categories expected.
	 */
	@Then("^I expect the tax calculation returned to contain (\\d+) tax categories$")
	public void verifyTaxCalculationCategories(final int numberExpected) {
		getHelper().verifyTaxCalculationCategories(numberExpected);
	}

	// Getters and Setters

	protected ShoppingCartTaxStepDefinitionsHelper getHelper() {
		return this.helper;
	}

	protected void setHelper(final ShoppingCartTaxStepDefinitionsHelper helper) {
		this.helper = helper;
	}
}

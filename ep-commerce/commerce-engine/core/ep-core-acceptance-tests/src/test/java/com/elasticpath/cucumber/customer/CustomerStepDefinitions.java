/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cucumber.customer;

import java.util.List;
import java.util.Map;

import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Store test step definitions class.
 */
public class CustomerStepDefinitions {

	@Autowired
	private CustomerStepDefinitionsHelper customerStepDefinitionsHelper;

	/**
	 * Sets up a default customer for the test environment.
	 */
	@Given("^(?:with )?a default customer$")
	public void setUpTaxCategories() {
		customerStepDefinitionsHelper.setUpDefaultCustomer();
	}

	/**
	 * Sets up a customer with address for the test environment.
	 *
	 * @param dataTable data table.
	 */
	@Given("^a customer with billing address of$")
	public void setUpCustomerWithAddress(final DataTable dataTable) {
		List<Map<String, String>> addressMap = dataTable.asMaps(String.class, String.class);
		customerStepDefinitionsHelper.setUpCustomerWithAddress(addressMap.get(0));
	}

}
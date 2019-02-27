package com.elasticpath.cucumber.definitions;

import cucumber.api.java.en.And;

import com.elasticpath.selenium.setup.SetUp;
import com.elasticpath.selenium.toolbars.CustomerServiceActionToolbar;

/**
 * Customer Service steps.
 */
public class CustomerServiceDefinition {
	private final CustomerServiceActionToolbar customerServiceActionToolbar;

	/**
	 * Constructor.
	 */
	public CustomerServiceDefinition() {
		customerServiceActionToolbar = new CustomerServiceActionToolbar(SetUp.getDriver());
	}

	/**
	 * Verify View Customer Import Jobs button is present.
	 */
	@And("^I can view Customer Import Jobs button")
	public void verifyCustomerImportJobsButtonIsPresent() {
		customerServiceActionToolbar.verifyCustomerImportJobsButtonIsPresent();
	}


}

/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.cucumber.tax;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * ProductSku test step definitions class.
 */
public class TaxCodeRetrieverStepDefinitions {

	@Autowired
	private TaxCodeRetrieverStepDefinitionsHelper helper;

	/**
	 * Create a product with the given tax code.
	 *
	 * @param taxCode the tax code
	 */
	@Given("^Product P1 with Tax Code \\[(.+)\\]$")
	public void setUpProductWithTaxCode(final String taxCode) {
		helper.prepareProductWithTaxCode(taxCode);
	}

	/**
	 * Create a product SKU.
	 */
	@Given("^a SKU of Product P1$")
	public void setUpProductSku() {
		// Do nothing; a SKU is created as a part of product setup.
	}

	/**
	 * Set the product SKU tax code.
	 *
	 * @param taxCode the tax code
	 */
	@Given("^the SKU has a Tax Code of \\[(.+)\\]$")
	public void setProductSkuTaxCode(final String taxCode) {
		helper.setProductSkuTaxCode(taxCode);
	}

	/**
	 * Set the product SKU tax code to null.
	 */
	@Given("^the SKU does not specify a Tax Code$")
	public void setNullProductSkuTaxCode() {
		helper.setNullProductSkuTaxCode();
	}

	/**
	 * Get and store the product SKU's tax code.
	 */
	@When("^the SKU is examined for its Tax Code$")
	public void fetchProductSkuTaxCode() {
		helper.fetchProductSkuTaxCode();
	}

	/**
	 * Compare the stored tax code against a desired tax code.
	 * 
	 * @param desiredTaxCode the desired tax code
	 */
	@Then("^the resulting Tax Code is \\[(.+)\\]$")
	public void checkProductSkuTaxCode(final String desiredTaxCode) {
		helper.checkProductSkuTaxCode(desiredTaxCode);
	}

}

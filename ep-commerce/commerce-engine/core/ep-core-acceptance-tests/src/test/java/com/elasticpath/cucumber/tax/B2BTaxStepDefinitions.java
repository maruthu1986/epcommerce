/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.cucumber.tax;

import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.common.dto.sellingchannel.ShoppingItemDtoFactory;
import com.elasticpath.cucumber.category.CategoryStepDefinitionsHelper;
import com.elasticpath.cucumber.shoppingcart.ShoppingCartStepDefinitionsHelper;
import com.elasticpath.cucumber.store.StoreStepDefinitionsHelper;

/**
 * B2BTaxStepDefinitions.
 */
public class B2BTaxStepDefinitions {

	@Autowired
	private B2BTaxStepDefinitionsHelper b2bTaxStepDefinitionsHelper;

	@Autowired
	private StoreStepDefinitionsHelper storeStepDefinitionsHelper;

	@Autowired
	private CategoryStepDefinitionsHelper categoryStepDefinitionsHelper;

	@Autowired
	private ShoppingCartStepDefinitionsHelper shoppingCartStepDefinitionsHelper;

	@Autowired
	private ShoppingItemDtoFactory shoppingItemDtoFactory;

	/**
	 * Assign a business number to a customer.
	 *
	 * @param businessNumber the business number to assign
	 */
	@Given("^a customer with a business number of \\[(.+)\\]$")
	public void assignCustomerBusinessNumber(final String businessNumber) {
		b2bTaxStepDefinitionsHelper.setBusinessNumberOnCustomer(businessNumber);
	}

	/**
	 * Assign a tax exemption id to a customer.
	 *
	 * @param taxExemptionId the tax exemption id to assign
	 */
	@Given("^(?:a customer with a|a) tax exemption id of \\[(.+)\\]$")
	public void assignCustomerTaxExemptionId(final String taxExemptionId) {
		b2bTaxStepDefinitionsHelper.setCustomerTaxExemptionIdOnCustomer(taxExemptionId);
	}

	/**
	 * Assign a tax exemption id directly to the cart.
	 *
	 * @param taxExemptionId the tax exemption id to assign
	 * @throws Throwable delete this
	 */
	@Given("^a shopping cart with a manually entered tax exemption id of \\[(.+)\\]$")
	public void assignShoppingCartTaxExemptionId(final String taxExemptionId) throws Throwable {
		b2bTaxStepDefinitionsHelper.setTaxExemptionOnCart(taxExemptionId);
	}

	/**
	 * Call the tax calculation API to enable examining what information is provided.
	 */
	@When("^I examine the tax details sent to the tax API during checkout$")
	public void requestTaxDetailsPassedToTaxAPI() {
		storeStepDefinitionsHelper.setUpTaxJurisdiction("exclusive", "CA");

		Map<String, String> taxCategories = ImmutableMap.of("taxName", "GST", "taxRate", "5", "taxRegion", "CA");
		storeStepDefinitionsHelper.setTaxCategories(ImmutableList.of(taxCategories));

		Map<String, String> products = ImmutableMap.of("skuCode", "anything", "price", "10.00", "type", "digital");
		categoryStepDefinitionsHelper.setUpProducts(ImmutableList.of(products));

		shoppingCartStepDefinitionsHelper.addItems(ImmutableList.of(shoppingItemDtoFactory.createDto("anything", 1)));

		b2bTaxStepDefinitionsHelper.requestTaxCalculation();
	}

	/**
	 * Verify that the given business number was passed to the tax API.
	 *
	 * @param businessNumber the business number to verify
	 */
	@Then("^I expect the business number to be (.+)$")
	public void verifyTaxAPICalledWithBusinessNumber(final String businessNumber) {
		b2bTaxStepDefinitionsHelper.verifyTaxOperationContextBusinessNumber(businessNumber);
	}

	/**
	 * Verify that the given tax exemption id was passed to the tax API.
	 *
	 * @param taxExemptionId the tax exemption id to verify
	 */
	@Then("^I expect the tax exemption id to be (.+)$")
	public void verifyTaxAPICalledWithTaxExemptionId(final String taxExemptionId) {
		b2bTaxStepDefinitionsHelper.verifyTaxOperationContextTaxExemptionId(taxExemptionId);
	}



}

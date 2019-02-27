package com.elasticpath.cucumber.definitions;


import java.util.List;

import com.elaticpath.selenium.example.ExampleCatalogManagement;
import com.elaticpath.selenium.example.ExampleProductListingPane;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import com.elasticpath.selenium.framework.util.SeleniumDriverSetup;

/**
 * Example step definitions.
 */
public class ExampleDefinition {

	private final ExampleCatalogManagement exampleCatalogManagement;
	private final ExampleProductListingPane exampleProductListingPane;

	/**
	 * Constructor.
	 */
	public ExampleDefinition() {
		exampleCatalogManagement = new ExampleCatalogManagement(SeleniumDriverSetup.getDriver());
		exampleProductListingPane = new ExampleProductListingPane(SeleniumDriverSetup.getDriver());
	}

	/**
	 * Double click category.
	 *
	 * @param categoryName the category name.
	 */
	@When("^Example Definition - I open category (.+) to view products list$")
	public void doubleClickCategory(final String categoryName) {
		exampleCatalogManagement.doubleClickTheCategory(categoryName);
	}

	/**
	 * Verify product in product listing.
	 *
	 * @param productNameList the product name list.
	 */
	@Then("^Example Definition - Product Listing should contain following products$")
	public void verifyProductInProductListing(final List<String> productNameList) {
		for (String productName : productNameList) {
			exampleProductListingPane.verifyProductNameExists(productName);
		}
		exampleProductListingPane.closeProductListingPane();

	}

}

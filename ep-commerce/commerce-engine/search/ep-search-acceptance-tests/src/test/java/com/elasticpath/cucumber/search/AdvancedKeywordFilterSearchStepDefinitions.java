/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cucumber.search;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Definition for the Advanced Keyword Search Filter feature.
 */
public class AdvancedKeywordFilterSearchStepDefinitions {

	@Autowired
	private AdvancedKeywordFilterSearchStepDefinitionsHelper advancedKeywordFilterSearchStepDefinitionsHelper;
	
	/**
	 * The setup statement.
	 */
	@Given("^the Advanced Search has been implemented for text attribute fields$")
	public void advancedSearchSetup() {
		advancedKeywordFilterSearchStepDefinitionsHelper.advancedSearchSetup();
	}
	
	/**
	 * The statement that specifies the attribute name and the type of the attribute.
	 * @param attributeName attribute name
	 */
	@And("attribute (.+) is configured as a text attribute")
	public void configureAttributeType(final String attributeName) {
		advancedKeywordFilterSearchStepDefinitionsHelper.configureAttributeType(attributeName);
	}

	/**
	 * The statement that specifies the attribute text value for the given attribute key.
	 * @param attrKey the attribute key
	 * @param productAttrValue the product attribute value
	 */
	@And("Product A attribute (.+) contains (.+)")
	public void setUpAttributeValue(final String attrKey, final String productAttrValue) {
		advancedKeywordFilterSearchStepDefinitionsHelper.setUpAttributeValue(attrKey, productAttrValue);
	}
	
	/**
	 * The statement that specifies the search term applied on the attribute key.
	 * @param searchTerm the search term
	 * @param attrKey the attribute key
	 */
	@When("(.+) is provided as a (.+) advanced search term")
	public void runAdvancedSearch(final String searchTerm, final String attrKey) {
		advancedKeywordFilterSearchStepDefinitionsHelper.runAdvancedSearch(searchTerm, attrKey);
	}
	
	/**
	 * Check the results.
	 */
	@Then("^Product A is listed in the results$")
	public void ensureSearchServiceResultsIsSuccessful() {
		advancedKeywordFilterSearchStepDefinitionsHelper.ensureSearchServiceResultsIsSuccessful();
	}
	
	/**
	 * Check the results.
	 */
	@Then("^No products are listed in the results$")
	public void ensureSearchServiceResultsFails() {
		advancedKeywordFilterSearchStepDefinitionsHelper.ensureSearchServiceResultsFails();
	}
}

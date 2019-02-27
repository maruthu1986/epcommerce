/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cucumber.category;

import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Category test step definitions class.
 * 
 */
public class CategoryStepDefinitions {
	
	@Autowired
	private CategoryStepDefinitionsHelper categoryStepDefinitionsHelper;
	
	/**
	 * Sets up products for the current test environment.
	 *
	 * @param dataTable the data of products
	 */
	@And("^with products of$")
	public void setUpProducts(final DataTable dataTable) {
		
		categoryStepDefinitionsHelper.setUpProducts(dataTable.asMaps(String.class, String.class));
	}
}
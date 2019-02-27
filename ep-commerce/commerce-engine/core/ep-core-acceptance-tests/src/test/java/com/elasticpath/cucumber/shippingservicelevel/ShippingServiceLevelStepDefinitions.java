/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cucumber.shippingservicelevel;

import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * {@link com.elasticpath.domain.shipping.ShippingServiceLevel} test step definitions class.
 * 
 */
public class ShippingServiceLevelStepDefinitions {
	
	@Autowired
	private ShippingServiceLevelStepDefinitionsHelper helper;
	
	/**
	 * Sets up shipping region for the current test environment.
	 *
	 * @param dataTable the data of shipping region
	 */
	@And("with shipping regions of$")
	public void setUpShippingRegions(final DataTable dataTable) {
		
		helper.setUpShippingRegions(dataTable.asMaps(String.class, String.class));
	}
	
	/**
	 * Sets up shipping service levels for the current test environment. 
	 *
	 * @param dataTable the data of shipping service levels
	 */
	@And("^with shipping service levels of$")
	public void setUpShippingServiceLevels(final DataTable dataTable) {
		
		helper.setUpShippingServiceLevels(dataTable.asMaps(String.class, String.class));
	}
}
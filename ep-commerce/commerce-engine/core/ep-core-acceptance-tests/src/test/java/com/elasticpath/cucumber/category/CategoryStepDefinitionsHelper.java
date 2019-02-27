/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cucumber.category;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.cucumber.CucumberConstants;
import com.elasticpath.cucumber.ScenarioContextValueHolder;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.test.persister.CatalogTestPersister;
import com.elasticpath.test.persister.TestApplicationContext;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;

/**
 * Help class for {@link CategoryStepDefinitions} and updating the current test environment.
 */
public class CategoryStepDefinitionsHelper {
	
	@Inject
	@Named("storeHolder")
	private ScenarioContextValueHolder<Store> storeHolder;
	
	@Inject
	@Named("simpleStoreScenarioHolder")
	private ScenarioContextValueHolder<SimpleStoreScenario> simpleStoreScenarioHolder;
	
	@Autowired
	private TestApplicationContext tac;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private ProductSkuLookup productSkuLookup;

	/**
	 * Sets up products for test environment.
	 *
	 * @param productPropertiesList the data of products
	 */
	public void setUpProducts(final List<Map<String, String>> productPropertiesList) {

		Store store = storeHolder.get();
		SimpleStoreScenario scenario = simpleStoreScenarioHolder.get();
		CatalogTestPersister catalogTestPersister = tac.getPersistersFactory().getCatalogTestPersister();
		
		for (Map<String, String> properties : productPropertiesList) {
			
			String skuCode = properties.get(CucumberConstants.FIELD_SKU_CODE);
					
			// check if the product is already in, otherwise remove it
			sanityCheck(skuCode);
			
			String productSkuType = properties.get(CucumberConstants.FIELD_TYPE);
			
			// if no type defined, then treat the product as physical one
			if (productSkuType == null 
					|| StringUtils.equals(properties.get(CucumberConstants.FIELD_TYPE), 
													CucumberConstants.FIELD_PHYSICAL)) {
				catalogTestPersister.persistProductWithSkuAndInventory(
						store.getCatalog(), 
						scenario.getCategory(), 
						scenario.getWarehouse(), 
						new BigDecimal(properties.get(CucumberConstants.FIELD_PRICE)), 
						"Product_Name" + properties.get(CucumberConstants.FIELD_SKU_CODE), 
						skuCode);
			} else if (productSkuType != null 
					&& StringUtils.equals(properties.get(CucumberConstants.FIELD_TYPE), 
													CucumberConstants.FIELD_DIGITAL)) {
				catalogTestPersister.persistNonShippablePersistedProductWithSku(
						store.getCatalog(), 
						scenario.getCategory(), 
						scenario.getWarehouse(), 
						new BigDecimal(properties.get(CucumberConstants.FIELD_PRICE)), 
						"Product_Name" + properties.get(CucumberConstants.FIELD_SKU_CODE), 
						skuCode);
			}
		}
	}

	private void sanityCheck(final String skuCode) {
		ProductSku productSku = productSkuLookup.findBySkuCode(skuCode);
		
		if (productSku != null) {
		
			Product product = productSku.getProduct();
			if (productService.canDelete(product)) {
				productService.removeProductTree(product.getUidPk());
			}
		}
	}
}

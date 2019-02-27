/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.cucumber.product;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.cucumber.ScenarioContextValueHolder;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalog.ProductLookup;
import com.elasticpath.service.catalogview.ProductViewService;
import com.elasticpath.service.catalogview.impl.InventoryMessage;
import com.elasticpath.test.persister.CatalogTestPersister;
import com.elasticpath.test.persister.TestApplicationContext;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;

/**
 * Product steps.
 */
public class ProductStepDefinitions {

	@Inject
	@Named("storeHolder")
	private ScenarioContextValueHolder<Store> storeHolder;

	@Inject
	@Named("simpleStoreScenarioHolder")
	private ScenarioContextValueHolder<SimpleStoreScenario> simpleStoreScenarioHolder;

	@Autowired
	private TestApplicationContext tac;

	@Autowired
	private ProductLookup productLookup;

	@Autowired
	private ProductViewService productViewService;

	/**
	 * Create a product.
	 *
	 * Valid properties are:
	 * "availability" - ["when in stock", "for pre-order"]
	 * "in stock" - ["yes", "no"]
	 *
	 * @param code the product code
	 * @param properties the product properties
	 */
	@Given("^product (\\w+) with$")
	public void createProduct(final String code, final Map<String, String> properties) {
		AvailabilityCriteria availability = getAvailability(properties);
		boolean inStock = isInStock(properties);

		CatalogTestPersister catalogTestPersister = tac.getPersistersFactory().getCatalogTestPersister();
		Store store = storeHolder.get();
		SimpleStoreScenario scenario = simpleStoreScenarioHolder.get();
		catalogTestPersister.persistProductWithSkuAndInventory(
				store.getCatalog(),
				scenario.getCategory(),
				scenario.getWarehouse(),
				code,
				availability,
				inStock);
	}

	/**
	 * Creates a bundle with the given products.
	 * @param bundleCode bundle code
	 * @param productCode1 first product code
	 * @param productCode2 second product code
	 */
	@When("^bundle (\\w+) is created with product (\\w+) and (\\w+)$")
	public void createBundleOfTwoProducts(final String bundleCode, final String productCode1, final String productCode2) {
		CatalogTestPersister catalogTestPersister = tac.getPersistersFactory().getCatalogTestPersister();
		Store store = storeHolder.get();
		SimpleStoreScenario scenario = simpleStoreScenarioHolder.get();
		catalogTestPersister.persistProductBundle(store.getCatalog(),
				scenario.getCategory(),
				bundleCode,
				productCode1,
				productCode2);
	}

	/**
	 * Checks given bundle is out of stock.
	 * @param bundleCode bundle code
	 */
	@Then("^bundle (\\w+) is out of stock$")
	public void checkoutBundleIsOutOfStock(final String bundleCode) {
		Product product = productLookup.findByGuid(bundleCode);
		Store store = storeHolder.get();
		StoreProduct storeProduct = productViewService.getProduct(product.getCode(), store, false);
		assertEquals(InventoryMessage.OUT_OF_STOCK, storeProduct.getInventoryDetails(product.getDefaultSku().getSkuCode()).getMessageCode());
	}

	private AvailabilityCriteria getAvailability(final Map<String, String> properties) {
		String availability = properties.get("availability");
		if (availability == null) {
			return null;
		}
		if ("when in stock".equals(availability)) {
			return AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK;
		} else if ("for pre-order".equals(availability)) {
			return AvailabilityCriteria.AVAILABLE_FOR_PRE_ORDER;
		} else {
			throw new IllegalStateException("unrecognized availability criteria " + availability);
		}
	}

	private boolean isInStock(final Map<String, String> properties) {
		String inStock = properties.get("in stock");
		if (inStock != null) {
			if ("yes".equals(inStock)) {
				return true;
			} else if ("no".equals(inStock)) {
				return false;
			} else {
				throw new IllegalStateException("unrecognized in stock value " + inStock);
			}
		}
		return false;
	}
}

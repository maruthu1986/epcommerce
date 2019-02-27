/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.test.persister.testscenarios;

import java.util.List;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;

/**
 * This simple scenario populates database with a common test data such as store, catalog, warehouse, products and others to make common shopping
 * possible.
 */
public class ShoppingCartSimpleStoreScenario extends AbstractScenario {

	private SimpleStoreScenario simpleStoreScenario;
	private ProductsScenario productsScenario;
	private CustomerAndCustomerSessionsScenario customerAndCustomerSessionsScenario;

	/**
	 * Populate database with default test data using test data persisters.
	 */
	@Override
	public void initialize() {
		getSimpleStoreScenario().initialize();
		getProductsScenario().initialize();
		getCustomerAndCustomerSessionsScenario().initialize();

		// Tax jurisdictions
		getDataPersisterFactory().getTaxTestPersister().persistDefaultTaxJurisdictions();
		// Create shipping service levels
		getDataPersisterFactory().getStoreTestPersister().persistDefaultShippingServiceLevels(getStore());
	}

	public Store getStore() {
		return getSimpleStoreScenario().getStore();
	}

	public Catalog getCatalog() {
		return getSimpleStoreScenario().getCatalog();
	}

	public Category getCategory() {
		return getSimpleStoreScenario().getCategory();
	}

	public Warehouse getWarehouse() {
		return getSimpleStoreScenario().getWarehouse();
	}

	public List<Product> getShippableProducts() {
		return getProductsScenario().getShippableProducts();
	}
	
	public List<Product> getNonShippableProducts() {
		return getProductsScenario().getNonShippableProducts();
	}

	/*
	 * Spring Accessors
	 */
	protected SimpleStoreScenario getSimpleStoreScenario() {
		return simpleStoreScenario;
	}

	public void setSimpleStoreScenario(final SimpleStoreScenario simpleStoreScenario) {
		this.simpleStoreScenario = simpleStoreScenario;
	}

	protected ProductsScenario getProductsScenario() {
		return productsScenario;
	}

	public void setProductsScenario(final ProductsScenario productsScenario) {
		this.productsScenario = productsScenario;
	}

	public CustomerAndCustomerSessionsScenario getCustomerAndCustomerSessionsScenario() {
		return customerAndCustomerSessionsScenario;
	}

	public void setCustomerAndCustomerSessionsScenario(final CustomerAndCustomerSessionsScenario customerAndCustomerSessionsScenario) {
		this.customerAndCustomerSessionsScenario = customerAndCustomerSessionsScenario;
	}
}
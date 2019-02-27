/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.test.persister.testscenarios;

import java.util.List;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.store.Warehouse;

/**
 * A Scenario which creates products.  It has a dependency on SimpleStoreScenario, which must be run before this one.
 * This scenario is really designed to be used as part of a composite, although you can use it directly by
 * including it in a list of scenarios behind the SimpleStoreScenario.
 */
public class ProductsScenario extends AbstractScenario {
	private SimpleStoreScenario simpleStoreScenario;
	private List<Product> shippableProducts;
	private List<Product> nonShippableProducts;

	@Override
	public void initialize() {
		// Create shippable and non-shippable products
		shippableProducts = getDataPersisterFactory().getCatalogTestPersister().persistDefaultShippableProducts(
				getCatalog(), getCategory(), getWarehouse());
		nonShippableProducts = getDataPersisterFactory().getCatalogTestPersister().persistDefaultNonShippableProducts(
				getCatalog(), getCategory(), getWarehouse());
	}

	public List<Product> getShippableProducts() {
		return shippableProducts;
	}

	public List<Product> getNonShippableProducts() {
		return nonShippableProducts;
	}

	protected Catalog getCatalog() {
		return getSimpleStoreScenario().getCatalog();
	}

	protected Category getCategory() {
		return getSimpleStoreScenario().getCategory();
	}

	protected Warehouse getWarehouse() {
		return getSimpleStoreScenario().getWarehouse();
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
}

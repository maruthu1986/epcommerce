/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.test.persister.testscenarios;

import static com.elasticpath.commons.constants.EpShippingContextIdNames.SHIPPING_OPTION_TRANSFORMER;

import java.util.Locale;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.shipping.ShippingRegion;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.service.shipping.ShippingOptionTransformer;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;
import com.elasticpath.test.persister.StoreTestPersister;

/**
 * This simple scenario populates database with a common test data such as store, catalog, warehouse etc.
 */
public class SimpleStoreScenario extends AbstractScenario {
	/** The reference to the single store in this scenario. */
	protected Store store;

	/** The warehouse used by the store. */
	protected Warehouse warehouse;

	/** The catalog used by the store. */
	protected Catalog catalog;

	/** A single category within the store's catalog. */
	protected Category category;

	/** The only shipping region supported by this store. */
	protected ShippingRegion shippingRegion;

	/**
	 * The only shipping service level supported by this store.
	 */
	protected ShippingServiceLevel shippingServiceLevel;

	protected ShippingOptionTransformer shippingOptionTransformer;

	public SimpleStoreScenario() {
		super();
	}

	/**
	 * Populate database with default common data.
	 */
	@Override
	public void initialize() {
		shippingOptionTransformer = getBeanFactory().getBean(SHIPPING_OPTION_TRANSFORMER);
		catalog = getDataPersisterFactory().getCatalogTestPersister().persistDefaultMasterCatalog();
		warehouse = getDataPersisterFactory().getStoreTestPersister().persistDefaultWarehouse();
		store = getDataPersisterFactory().getStoreTestPersister().persistDefaultStore(catalog, warehouse);
		category = getDataPersisterFactory().getCatalogTestPersister().persistDefaultCategories(catalog);
		shippingRegion = getDataPersisterFactory().getStoreTestPersister().getShippingRegion(StoreTestPersister.DEFAULT_SHIPPING_REGION_NAME);
		shippingServiceLevel = getDataPersisterFactory().getStoreTestPersister().persistDefaultShippingServiceLevel(store);
		store.setCatalog(catalog);

	}

	/**
	 * Get the populated store.
	 *
	 * @return store
	 */
	public Store getStore() {
		return store;
	}

	/**
	 * Get the populated catalog.
	 *
	 * @return catalog
	 */
	public Catalog getCatalog() {
		return catalog;
	}

	/**
	 * Get the populated warehouse.
	 *
	 * @return warehouse
	 */
	public Warehouse getWarehouse() {
		return warehouse;
	}

	/**
	 * Get the populated category.
	 *
	 * @return catagory
	 */
	public Category getCategory() {
		return category;
	}

	/**
	 * @return the shippingRegion
	 */
	public ShippingRegion getShippingRegion() {
		return shippingRegion;
	}

	/**
	 * @return the shipping option.
	 */
	public ShippingOption getShippingOption() {
		return getShippingOption(store.getDefaultLocale());
	}

	/**
	 * @return the shipping option.
	 */
	public ShippingOption getShippingOption(final Locale locale) {
		return shippingOptionTransformer.transform(shippingServiceLevel, () -> null, locale);
	}
}

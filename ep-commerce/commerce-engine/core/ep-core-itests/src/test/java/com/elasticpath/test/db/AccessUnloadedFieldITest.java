/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.test.db;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.persistence.support.impl.FetchGroupLoadTunerImpl;
import com.elasticpath.service.catalog.ProductService;

/**
 * Test class to verify that core domain classes trigger an exception when a field not loaded by OpenJPA is accessed.
 */
public class AccessUnloadedFieldITest extends DbTestCase {

	@Autowired
	private ProductService productService;

	private FetchGroupLoadTuner loadTuner;

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Before
	public void setUp() {
		loadTuner = new FetchGroupLoadTunerImpl();
		loadTuner.addFetchGroup(FetchGroupConstants.PRODUCT_HASH_MINIMAL);
	}

	@Test
	public void verifyExceptionThrownWhenUnloadedFieldAccessed() throws Exception {
		final Product product = createPersistedTestProduct();

		final Product tunedProduct = productService.getTuned(product.getUidPk(), loadTuner);

		exception.expect(IllegalStateException.class);
		exception.expectMessage(containsString("unloaded field"));

		// End Date is not a member of the minimal fetch plan.
		tunedProduct.getEndDate();
	}

	@Test
	public void verifyExceptionNotThrownWhenLoadedFieldAccessed() throws Exception {
		final Product product = createPersistedTestProduct();

		final Product tunedProduct = productService.getTuned(product.getUidPk(), loadTuner);

		// Code is not a member of the minimal fetch plan.
		assertNotNull("Expected non-null code populated by Fetch Group Load Tuner", tunedProduct.getCode());
	}

	private Product createPersistedTestProduct() {
		return getTac().getPersistersFactory().getCatalogTestPersister()
				.createDefaultProductWithSkuAndInventory(scenario.getCatalog(), scenario.getCategory(), scenario.getWarehouse());
	}

}
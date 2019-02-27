/*
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.tools.sync.target.impl;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.tools.sync.merge.configuration.EntityLocator;

//import com.elasticpath.test.BeanFactoryExpectationsFactory;

public class ProductSkuDaoAdapterImplTest {

	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ProductSkuDaoAdapterImpl productSkuDaoAdapterImpl;

	EntityLocator entityLocator;

	private ProductSkuService productSkuService;

	/**
	 * Sets up the test.
	 */
	@Before
	public void setUp() {
		productSkuService = context.mock(ProductSkuService.class);
		entityLocator = context.mock(EntityLocator.class);

		productSkuDaoAdapterImpl = new ProductSkuDaoAdapterImpl();
		productSkuDaoAdapterImpl.setProductSkuService(productSkuService);
		productSkuDaoAdapterImpl.setEntityLocator(entityLocator);
	}

	/**
	 * Verify removing a sku uses the same process as finding a sku.
	 */
	@Test
	public void ensureEntityLocatorGetsTheSkuBeforeRemove() {
		final String guid = "guid";
		final Long uid = 1L;
		final ProductSku sku = context.mock(ProductSku.class);

		context.checking(new Expectations() {
			{
				oneOf(entityLocator).locatePersistence(guid, ProductSku.class);
				will(returnValue(sku));

				oneOf(sku).getUidPk();
				will(returnValue(uid));

				oneOf(productSkuService).removeProductSkuTree(uid);
			}
		});
		productSkuDaoAdapterImpl.remove(guid);
	}
}

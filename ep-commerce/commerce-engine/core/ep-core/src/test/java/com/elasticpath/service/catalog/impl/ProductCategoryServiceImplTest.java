/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.catalog.impl;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Collections;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.ProductCategory;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.ProductCategoryImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.persistence.api.PersistenceEngine;

public class ProductCategoryServiceImplTest {
	@Rule public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final PersistenceEngine persistenceEngine = context.mock(PersistenceEngine.class);
	private ProductCategoryServiceImpl service;
	private CategoryImpl category;
	private ProductImpl product;
	private ProductCategoryImpl productCategory;

	@Before
	public void setUp() throws Exception {
		category = new CategoryImpl();
		category.initialize();
		category.setCode("category");

		product = new ProductImpl();
		product.setCode("p1");

		productCategory = new ProductCategoryImpl();
		productCategory.setCategory(category);
		productCategory.setProduct(product);

		service = new ProductCategoryServiceImpl();
		service.setPersistenceEngine(persistenceEngine);
	}

	@Test
	public void testFindByCategoryGuid() throws Exception {
		context.checking(new Expectations() {
			{
				allowing(persistenceEngine).retrieveByNamedQuery(
						with(any(String.class)), with(new Object[] {category.getGuid()}));
				will(returnValue(Collections.singletonList(productCategory)));
			}
		});

		Collection<ProductCategory> found = service.findByCategoryGuid(category.getGuid());

		assertEquals("Finder should query the db and return the result",
				Collections.singletonList(productCategory), found);
	}

	@Test
	public void testFindByCategoryAndProduct() throws Exception {
		context.checking(new Expectations() {
			{
				allowing(persistenceEngine).retrieveByNamedQuery(
						with(any(String.class)), with(new Object[] {category.getGuid(), product.getCode()}));
				will(returnValue(Collections.singletonList(productCategory)));
			}
		});

		ProductCategory found = service.findByCategoryAndProduct(category.getGuid(), product.getCode());

		assertEquals("Finder should query the db and return the result", productCategory, found);
	}
}

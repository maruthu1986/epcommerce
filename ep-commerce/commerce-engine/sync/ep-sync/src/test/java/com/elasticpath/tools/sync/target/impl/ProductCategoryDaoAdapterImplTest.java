/*
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.tools.sync.target.impl;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.ProductCategoryImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.service.catalog.ProductCategoryService;

public class ProductCategoryDaoAdapterImplTest {
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ProductCategoryDaoAdapterImpl adapter;
	private final ProductCategoryService productCategoryService = context.mock(ProductCategoryService.class);
	private CategoryImpl category;
	private ProductImpl product;
	private ProductCategoryImpl productCategory;

	@Before
	public void setUp() throws Exception {
		category = new CategoryImpl();
		category.initialize();

		product = new ProductImpl();
		product.setCode("product");

		productCategory = new ProductCategoryImpl();
		productCategory.setProduct(product);
		productCategory.setCategory(category);

		adapter = new ProductCategoryDaoAdapterImpl();
		adapter.setProductCategoryService(productCategoryService);
	}

	@Test
	public void testGetAssociatedGuidsWithCategoryGuid() {
		context.checking(new Expectations() {
			{
				allowing(productCategoryService).findByCategoryGuid(category.getGuid());
				will(returnValue(Collections.singletonList(productCategory)));
			}
		});

		List<String> found = adapter.getAssociatedGuids(Category.class, category.getGuid());
		assertEquals("Synthetic product/category guids should be returned",
				Collections.singletonList(category.getGuid() + "|" + product.getCode()),
				found);
	}
}

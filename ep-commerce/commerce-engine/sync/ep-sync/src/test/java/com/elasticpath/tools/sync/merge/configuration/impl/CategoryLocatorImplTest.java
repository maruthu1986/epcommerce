/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.tools.sync.merge.configuration.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.Category;
import com.elasticpath.service.catalog.CategoryService;

public class CategoryLocatorImplTest {
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final CategoryService categoryService = context.mock(CategoryService.class);
	private CategoryLocatorImpl categoryLocator;

	@Before
	public void setUp() {
		categoryLocator = new CategoryLocatorImpl();
		categoryLocator.setCategoryService(categoryService);
	}

	@Test
	public void testEntityExistsDelegatesToService() throws Exception {
		context.checking(new Expectations() {
			{
				allowing(categoryService).isGuidInUse("foo"); will(returnValue(true));
				allowing(categoryService).isGuidInUse("bar"); will(returnValue(false));
			}
		});

		assertTrue(categoryLocator.entityExists("foo", Category.class));
		assertFalse(categoryLocator.entityExists("bar", Category.class));
	}
}

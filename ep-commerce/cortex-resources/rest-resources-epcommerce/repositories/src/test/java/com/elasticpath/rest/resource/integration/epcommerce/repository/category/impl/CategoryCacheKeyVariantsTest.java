/**
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.category.impl;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;

/**
 * Tests {@link CategoryRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CategoryCacheKeyVariantsTest {

	private static final String GUID = "guid";
	private static final String CODE = "code";

	@Mock
	private Category category;

	@Mock
	private Catalog catalog;

	@InjectMocks
	private CategoryCacheKeyVariants categoryCacheKeyVariants;

	@Test
	public void get() {
		// Given
		when(category.getGuid()).thenReturn(GUID);
		when(category.getCode()).thenReturn(CODE);
		when(category.getCatalog()).thenReturn(catalog);

		// When
		Collection<Object[]> results = categoryCacheKeyVariants.get(category);

		// Then
		Iterator<Object[]> iterator = results.iterator();
		Object[] resultGuid = iterator.next();
		Object[] resultCodeAndCatalog = iterator.next();
		assertThat(resultGuid[0]).isEqualTo(GUID);
		assertThat(resultCodeAndCatalog).isEqualTo(new Object[] {CODE, catalog});
	}

	@Test
	public void getType() {
		assertThat(categoryCacheKeyVariants.getType()).isEqualTo(Category.class);
	}
}
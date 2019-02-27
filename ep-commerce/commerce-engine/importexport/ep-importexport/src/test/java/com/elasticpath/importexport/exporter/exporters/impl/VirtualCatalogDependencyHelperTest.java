/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.importexport.exporter.exporters.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;

/**
 * Tests building dependencies process performed by <code>VirtualCatalogDependencyHelper</code>.
 */
@RunWith(MockitoJUnitRunner.class)
public class VirtualCatalogDependencyHelperTest {

	private VirtualCatalogDependencyHelper dependencyHelper;

	@Mock
	private Catalog catalog;
	@Mock
	private Category category;
	@Mock
	private CategoryType categoryType;

	private DependencyRegistry dependencyRegistry;

	/**
	 * Prepares meta objects.
	 */
	@Before
	public void setUp() {
		dependencyHelper = new VirtualCatalogDependencyHelper();

		dependencyRegistry = new DependencyRegistry(Arrays.asList(Catalog.class, Category.class, CategoryType.class, Object.class));

		when(category.getCatalog()).thenReturn(catalog);
		when(catalog.isMaster()).thenReturn(true);
	}

	/**
	 * Checks that guard prevents further execution in case Master Category is contained in master catalog.
	 */
	@Test
	public void testAddInfluencingWhenMasterCatalog() {
		dependencyHelper.addInfluencingCatalogs(category, null);
		verify(catalog, times(1)).isMaster();
	}

	/**
	 * Check that master catalog containing category type is added into dependency registry when category is contained in virtual catalog.
	 */
	@Test
	public void testAddInfluencingWhenVirtualCatalog() {

		when(catalog.isMaster()).thenReturn(false);
		when(category.getCategoryType()).thenReturn(categoryType);

		final String masterCatalogGuid = "22ca91b5-9ca8-43e5-8850-0963a4ee92f1";
		final Catalog mockMasterCatalog = mock(Catalog.class, "master catalog");

		when(categoryType.getCatalog()).thenReturn(mockMasterCatalog);
		when(mockMasterCatalog.getGuid()).thenReturn(masterCatalogGuid);

		dependencyHelper.addInfluencingCatalogs(category, dependencyRegistry);
		final Set<String> influencingCatalogs = dependencyRegistry.getDependentGuids(Catalog.class);

		assertThat(influencingCatalogs)
				.size()
				.isEqualTo(1);

		assertThat(influencingCatalogs).contains(masterCatalogGuid);

		verify(catalog, times(1)).isMaster();
		verify(category, times(1)).getCategoryType();
		verify(categoryType, times(1)).getCatalog();
		verify(mockMasterCatalog, times(1)).getGuid();
	}
}

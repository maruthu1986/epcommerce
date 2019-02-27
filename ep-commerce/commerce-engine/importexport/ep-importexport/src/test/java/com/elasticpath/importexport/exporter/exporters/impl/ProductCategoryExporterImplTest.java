/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.importexport.exporter.exporters.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.importexport.common.adapters.productcategories.ProductCategoryAdapter;
import com.elasticpath.importexport.common.dto.productcategory.ProductCategoriesDTO;
import com.elasticpath.importexport.common.summary.Summary;
import com.elasticpath.importexport.common.summary.impl.SummaryImpl;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.service.query.QueryCriteria;
import com.elasticpath.service.query.QueryResult;
import com.elasticpath.service.query.QueryService;

/**
 * Product category exporter test.
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("PMD.NonStaticInitializer")
public class ProductCategoryExporterImplTest {

	private static final String PRODUCT_GUID = "1f0dde98-93d8-4720-9a62-c524c2d3bb97";
	private static final String CATEGORY_GUID = "c0f8b6ee-82be-4129-899c-48f4011dd101";

	private ProductCategoryExporterImpl productCategoryExporter;
	private ExportContext exportContext;
	@Mock
	private QueryService<Product> productQueryService;

	/**
	 * Prepare for tests.
	 *
	 * @throws Exception in case of error happens
	 */
	@Before
	@SuppressWarnings("unchecked")
	public void setUp() throws Exception {
		QueryResult<Product> productQueryResult = mock(QueryResult.class);

		final Catalog catalog = new CatalogImpl();
		catalog.setCode("CATALOG");

		final Category category = new CategoryImpl();
		category.setCatalog(catalog);
		category.setGuid(CATEGORY_GUID);
		final Product product = new ProductImpl();
		product.setGuid(PRODUCT_GUID);
		product.addCategory(category);

		when(productQueryService.query(any(QueryCriteria.class))).thenReturn((QueryResult) productQueryResult);
		when(productQueryResult.getResults()).thenReturn(Collections.singletonList(product));

		productCategoryExporter = new ProductCategoryExporterImpl();
		productCategoryExporter.setProductQueryService(productQueryService);
		productCategoryExporter.setProductCategoryAdapter(new MockProductCategoryAdapter());
	}

	/**
	 * Check that during initialization exporter prepares the list of UidPk for product categories to be exported.
	 */
	@Test
	public void testExporterInitialization() throws Exception {
		final List<String> productGuidList = new ArrayList<>();
		productGuidList.add(PRODUCT_GUID);

		ExportConfiguration exportConfiguration = new ExportConfiguration();
		SearchConfiguration searchConfiguration = new SearchConfiguration();

		exportContext = new ExportContext(exportConfiguration, searchConfiguration);
		exportContext.setSummary(new SummaryImpl());

		List<Class<?>> dependentClasses = new ArrayList<>();
		dependentClasses.add(Product.class);
		dependentClasses.add(Category.class);
		DependencyRegistry dependencyRegistry = new DependencyRegistry(dependentClasses);

		dependencyRegistry.addGuidDependencies(Product.class, new TreeSet<>(productGuidList));
		exportContext.setDependencyRegistry(dependencyRegistry);

		productCategoryExporter.initialize(exportContext);
	}

	/**
	 * Check an export of product categories.
	 */
	@Test
	public void testProcessExport() throws Exception {
		testExporterInitialization();
		productCategoryExporter.processExport(System.out);
		Summary summary = productCategoryExporter.getContext().getSummary();

		assertThat(summary.getCounters())
				.size()
				.isEqualTo(1);
		assertThat(summary.getCounters()).containsKey(JobType.PRODUCTCATEGORYASSOCIATION);
		assertThat(summary.getCounters().get(JobType.PRODUCTCATEGORYASSOCIATION))
				.isEqualTo(1);
		assertThat(summary.getFailures())
				.size()
				.isEqualTo(0);
		assertThat(summary.getStartDate()).isNotNull();
		assertThat(summary.getElapsedTime()).isNotNull();
		assertThat(summary.getElapsedTime().toString()).isNotNull();

		Set<String> dependentGuids = exportContext.getDependencyRegistry().getDependentGuids(Category.class);
		assertThat(dependentGuids).isEqualTo(Collections.singleton(CATEGORY_GUID));
	}

	/**
	 * Mock product category adapter.
	 */
	private class MockProductCategoryAdapter extends ProductCategoryAdapter {

		@Override
		public void populateDomain(final ProductCategoriesDTO productCategoriesDTO, final Product product) {
			// do nothing
		}

		@Override
		public void populateDTO(final Product product, final ProductCategoriesDTO productCategoriesDTO) {
			// do nothing
		}

	}

}

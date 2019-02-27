/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.importexport.exporter.exporters.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.catalog.impl.BrandImpl;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.ProductAssociationImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.service.catalog.ProductAssociationService;
import com.elasticpath.service.search.query.ProductAssociationSearchCriteria;

/**
 * Tests of the ProductDependencyHelper.
 */
@RunWith(MockitoJUnitRunner.class)
public class ProductDependencyHelperTest {

	private ProductDependencyHelper helper;
	@Mock
	private ProductAssociationService mockProductAssociationService;

	private List<Long> uidList;

	private DependencyRegistry registry;

	private BrandImpl fakeBrand;

	private BrandImpl fakeBrand2;

	private CategoryImpl fakeCategory;

	@Before
	public void setUp() throws Exception {
		List<Class<?>> dependentTypes = new ArrayList<>();
		dependentTypes.addAll(Arrays.asList(new ProductExporterImpl().getDependentClasses()));
		dependentTypes.addAll(Arrays.asList(new CatalogExporterImpl().getDependentClasses()));
		dependentTypes.addAll(Arrays.asList(new ProductAssociationExporterImpl().getDependentClasses()));

		registry = new DependencyRegistry(dependentTypes);
		uidList = new ArrayList<>();
		helper = new ProductDependencyHelper(registry, uidList, null, mockProductAssociationService);

		CatalogImpl fakeCatalog = new CatalogImpl();
		fakeCatalog.setUidPk(1);

		fakeBrand = new BrandImpl();
		fakeBrand.setGuid("e263d173-c8c8-4ad1-8c00-ebafae6442e0");
		fakeBrand.setCatalog(fakeCatalog);

		fakeBrand2 = new BrandImpl();
		fakeBrand2.setGuid("7300c4f6-8aaf-4054-936d-0ccd2b0bf2a3");
		fakeBrand2.setCatalog(fakeCatalog);

		fakeCategory = new CategoryImpl();
		fakeCategory.setUidPk(1);
		fakeCategory.setCatalog(fakeCatalog);

		when(mockProductAssociationService.findByCriteria(any(ProductAssociationSearchCriteria.class))).thenReturn(new ArrayList<>());
	}

	/**
	 * Test registering dependencies for single unassociated product.
	 *
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testSingleProductDependencies() throws Exception {
		Product product1 = createProduct(1, fakeBrand, fakeCategory);
		uidList.add(product1.getUidPk());
		List<Product> products = new ArrayList<>();
		products.add(product1);

		helper.addDependencies(products);
		assertThat(registry.getDependentGuids(Brand.class)).contains(fakeBrand.getGuid());
	}

	/**
	 * Test registering dependencies for multiple unassociated products.
	 *
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testMultipleProductCatalogDependencies() throws Exception {
		Product product1 = createProduct(1, fakeBrand, fakeCategory);
		uidList.add(product1.getUidPk());
		Product product2 = createProduct(2, fakeBrand2, fakeCategory);
		uidList.add(product2.getUidPk());
		List<Product> products = new ArrayList<>();
		products.add(product1);
		products.add(product2);

		helper.addDependencies(products);
		assertThat(registry.getDependentGuids(Brand.class)).contains(fakeBrand.getGuid());
		assertThat(registry.getDependentGuids(Brand.class)).contains(fakeBrand2.getGuid());
	}

	/**
	 * Test registering dependencies for associated products.
	 *
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testAssociatedProductDependencies() throws Exception {
		Product product1 = createProduct(1, fakeBrand, fakeCategory);
		uidList.add(product1.getUidPk());
		Product product2 = createProduct(2, fakeBrand2, fakeCategory);
		List<Product> products = new ArrayList<>();
		products.add(product1);

		final ProductAssociationImpl association = new ProductAssociationImpl();
		association.setGuid("3e311b5e-d206-4fd3-8c65-cfd300b321f7");
		association.setSourceProduct(product1);
		association.setTargetProduct(product2);
		final List<ProductAssociation> associations = new ArrayList<>();
		associations.add(association);

		when(mockProductAssociationService.findByCriteria(any(ProductAssociationSearchCriteria.class))).thenReturn(associations);

		helper.addDependencies(products);

		assertThat(registry.getDependentGuids(ProductAssociation.class)).contains(association.getGuid());
		assertThat(uidList).contains(product2.getUidPk());
	}

	private Product createProduct(final long uidPk, final Brand brand, final Category category) {
		Product product1 = new ProductImpl();
		product1.setUidPk(uidPk);
		product1.setBrand(brand);
		Set<Category> categories = new HashSet<>();
		categories.add(category);
		product1.setCategories(categories);
		return product1;
	}
}

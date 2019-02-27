/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.service.catalog.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.exception.EpProductInUseException;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.persistence.dao.ProductDao;
import com.elasticpath.service.catalog.ProductLookup;
import com.elasticpath.service.search.IndexNotificationService;
import com.elasticpath.service.search.IndexType;

/**
 * Tests for the ProductServiceImpl class.
 */
public class ProductServiceImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private final ProductDao productDao = context.mock(ProductDao.class);
	private final ProductLookup productLookup = context.mock(ProductLookup.class);
	private final IndexNotificationService indexNotificationService = context.mock(IndexNotificationService.class);
	
	/**
	 * Tests if #removeProductTree throws exception when product is in a bundle.
	 */
	@Test(expected = EpProductInUseException.class)
	public void testRemoveProductTreeThrowsExceptionWhenProductIsInBundle() {
		final long aProductUid = 1L;
		final Product product = new ProductImpl();
		
		final ArrayList<Product> productsInBundleList = new ArrayList<>();
		productsInBundleList.add(product);
		
		ProductServiceImpl service = new ProductServiceImpl() {
			@Override
			public boolean canDelete(final Product product) {
				return false;
			}
		};
		
		service.setProductLookup(productLookup);
		
		context.checking(new Expectations() { {
			oneOf(productLookup).findByUid(aProductUid); will(returnValue(product));
		} });
		
		service.removeProductTree(aProductUid);
	}

	@Test
	public void testFindProductUidsByCategoryUids() {

		final List<Long> categoryUids = Arrays.asList(1L, 2L, 3L);
		final List<Long> expectedProductUids = Arrays.asList(4L, 5L, 6L);

		ProductServiceImpl service = new ProductServiceImpl();
		service.setProductDao(productDao);

		context.checking(new Expectations() { {
			oneOf(productDao).findUidsByCategoryUids(categoryUids); will(returnValue(expectedProductUids));
		} });

		final List<Long> actualProductUids = service.findUidsByCategoryUids(categoryUids);

		assertEquals(actualProductUids, expectedProductUids);
	}
	
	/**
	 * Tests that index notification update is posted when a product is added or updated.
	 */
	@Ignore("test does not work")
	@Test
	public void testAddOrUpdateProductAddsIndexNotification() {
		final Product product = new ProductImpl();
		final ProductServiceImpl productService = new ProductServiceImpl();
		productService.setIndexNotificationService(indexNotificationService);
		context.checking(new Expectations() {
			{
				oneOf(productDao).saveOrUpdate(product); will(returnValue(product));
				oneOf(indexNotificationService).addNotificationForEntityIndexUpdate(IndexType.PRODUCT, product.getUidPk());
			}
		});
		
		productService.saveOrUpdate(product);
	}
	
}

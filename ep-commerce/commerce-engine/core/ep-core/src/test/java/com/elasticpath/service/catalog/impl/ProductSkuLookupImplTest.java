/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.catalog.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.dao.ProductDao;
import com.elasticpath.service.catalog.ProductLookup;

public class ProductSkuLookupImplTest {
	private static final long PRODUCT_UID = 1L, PRODUCT2_UID = 2L;
	private static final String PRODUCT_CODE = "PRODUCT", PRODUCT2_CODE = "PRODUCT-2";
	private static final long SKU_UID = 11L, SKU2_UID = 21L;
	private static final String SKU_CODE = "SKU", SKU2_CODE = "SKU-2";
	private static final String SKU_GUID = "SKU-GUID", SKU2_GUID = "SKU2-GUID";
	private static final String IS_PRODUCT_SKU_EXISTS_NAMED_QUERY = "PRODUCT_SKU_GUID_SELECT_BY_GUID";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	@Mock private ProductDao productDao;
	@Mock private ProductLookup productLookup;
	@Mock private PersistenceEngine persistenceEngine;

	private Product product1, product2;
	private ProductSku sku1, sku2;
	private ProductSkuLookupImpl skuLookup;

	@Before
	public void setUp() {
		product1 = new ProductImpl();
		product1.setUidPk(PRODUCT_UID);
		product1.setCode(PRODUCT_CODE);

		product2 = new ProductImpl();
		product2.setUidPk(PRODUCT2_UID);
		product2.setCode(PRODUCT2_CODE);

		sku1 = new ProductSkuImpl();
		sku1.setUidPk(SKU_UID);
		sku1.setSkuCode(SKU_CODE);
		sku1.setGuid(SKU_GUID);

		sku2 = new ProductSkuImpl();
		sku2.setUidPk(SKU2_UID);
		sku2.setSkuCode(SKU2_CODE);
		sku2.setGuid(SKU2_GUID);

		product1.addOrUpdateSku(sku1);
		product2.addOrUpdateSku(sku2);

		skuLookup = new ProductSkuLookupImpl();
		skuLookup.setProductDao(productDao);
		skuLookup.setProductLookup(productLookup);
		skuLookup.setPersistenceEngine(persistenceEngine);
	}

	@Test
	public void testFindByUidWhenUidIsFound() {
		//  Given
		context.checking(new Expectations() {
			{
				allowing(productDao).findUidBySkuUid(SKU_UID);
				will(returnValue(PRODUCT_UID));

				allowing(productLookup).findByUid(PRODUCT_UID);
				will(returnValue(product1));
			}
		});

		// When
		ProductSku found = skuLookup.findByUid(SKU_UID);

		// Then
		assertSame("Sku should have been found and returned from the sku lookup", sku1, found);
	}

	@Test
	public void testFindByUidWhenUidIsNotFound() {
		//  Given
		context.checking(new Expectations() {
			{
				allowing(productDao).findUidBySkuUid(SKU_UID);
				will(returnValue(null));
			}
		});

		// When
		ProductSku found = skuLookup.findByUid(SKU_UID);

		// Then
		assertNull("Sku should not have been found because no match for the uid existed", found);
	}

	@Test
	public void testFindByUidWhenProductIsNotFound() {
		//  Given
		context.checking(new Expectations() {
			{
				allowing(productDao).findUidBySkuUid(SKU_UID);
				will(returnValue(PRODUCT_UID));

				allowing(productLookup).findByUid(PRODUCT_UID);
				will(returnValue(null));
			}
		});

		// When
		ProductSku found = skuLookup.findByUid(SKU_UID);

		// Then
		assertNull("Sku should not have been found because no match for the product existed", found);
	}

	@Test
	public void testFindByUids() {
		//  Given
		context.checking(new Expectations() {
			{
				allowing(productDao).findUidsBySkuUids(Arrays.asList(SKU_UID, SKU2_UID));
				will(returnValue(Arrays.asList(PRODUCT_UID, PRODUCT2_UID)));

				allowing(productLookup).findByUids(Arrays.asList(PRODUCT_UID, PRODUCT2_UID));
				will(returnValue(Arrays.asList(product1, product2)));
			}
		});

		// When
		List<ProductSku> found = skuLookup.findByUids(Arrays.asList(SKU_UID, SKU2_UID));

		// Then
		assertEquals("Skus should have been found and returned from the sku lookup",
				Arrays.asList(sku1, sku2), found);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testFindByGuidHappyPath() {
		//  Given
		context.checking(new Expectations() {
			{
				allowing(productDao).findUidBySkuGuid(SKU_GUID);
				will(returnValue(PRODUCT_UID));

				allowing(productLookup).findByUid(PRODUCT_UID);
				will(returnValue(product1));
			}
		});

		// When
		ProductSku found = skuLookup.findByGuid(SKU_GUID);

		// Then
		assertSame("Sku should have been found and returned from the sku lookup", sku1, found);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testFindByGuidWhenSkuCodeNotFound() {
		//  Given
		context.checking(new Expectations() {
			{
				allowing(productDao).findUidBySkuGuid(SKU_GUID);
				will(returnValue(null));
			}
		});

		// When
		ProductSku found = skuLookup.findByGuid(SKU_GUID);

		// Then
		assertNull("Sku should not have been found", found);
	}

	@Test
	public void testFindBySkuGuidsHappyPath() {
		//  Given
		context.checking(new Expectations() {
			{
				allowing(productDao).findUidsBySkuGuids(Arrays.asList(SKU_GUID, SKU2_GUID));
				will(returnValue(Arrays.asList(PRODUCT_UID, PRODUCT2_UID)));

				allowing(productLookup).findByUids(Arrays.asList(PRODUCT_UID, PRODUCT2_UID));
				will(returnValue(Arrays.asList(product1, product2)));
			}
		});

		// When
		List<ProductSku> found = skuLookup.findByGuids(Arrays.asList(SKU_GUID, SKU2_GUID));

		// Then
		assertEquals("Skus should have been found and returned from the sku lookup",
				Arrays.asList(sku1, sku2), found);
	}

	@Test
	public void testFindBySkuGuidsWhenGuidsNotFound() {
		//  Given
		context.checking(new Expectations() {
			{
				allowing(productDao).findUidsBySkuGuids(Arrays.asList(SKU_GUID, SKU2_GUID));
				will(returnValue(Arrays.asList()));
			}
		});

		// When
		List<ProductSku> found = skuLookup.findByGuids(Arrays.asList(SKU_GUID, SKU2_GUID));

		// Then
		assertEquals("Skus should not have been found", Collections.<ProductSku>emptyList(), found);
	}

	@Test
	public void testFindBySkuCodeHappyPath() {
		//  Given
		context.checking(new Expectations() {
			{
				allowing(productDao).findUidBySkuCode(SKU_CODE);
				will(returnValue(PRODUCT_UID));

				allowing(productLookup).findByUid(PRODUCT_UID);
				will(returnValue(product1));
			}
		});

		// When
		ProductSku found = skuLookup.findBySkuCode(SKU_CODE);

		// Then
		assertSame("Sku should have been found and returned from the sku lookup", sku1, found);
	}

	@Test
	public void testFindBySkuCodeWhenSkuCodeNotFound() {
		//  Given
		context.checking(new Expectations() {
			{
				allowing(productDao).findUidBySkuCode(SKU_CODE);
				will(returnValue(null));
			}
		});

		// When
		ProductSku found = skuLookup.findBySkuCode(SKU_CODE);

		// Then
		assertNull("Sku should not have been found", found);
	}

	@Test
	public void testFindBySkuCodesHappyPath() {
		//  Given
		context.checking(new Expectations() {
			{
				allowing(productDao).findUidsBySkuCodes(Arrays.asList(SKU_CODE, SKU2_CODE));
				will(returnValue(Arrays.asList(PRODUCT_UID, PRODUCT2_UID)));

				allowing(productLookup).findByUids(Arrays.asList(PRODUCT_UID, PRODUCT2_UID));
				will(returnValue(Arrays.asList(product1, product2)));
			}
		});

		// When
		List<ProductSku> found = skuLookup.findBySkuCodes(Arrays.asList(SKU_CODE, SKU2_CODE));

		// Then
		assertEquals("Skus should have been found and returned from the sku lookup",
				Arrays.asList(sku1, sku2), found);
	}

	@Test
	public void testFindBySkuCodesWhenSkuCodesNotFound() {
		//  Given
		context.checking(new Expectations() {
			{
				allowing(productDao).findUidsBySkuCodes(Arrays.asList(SKU_CODE, SKU2_CODE));
				will(returnValue(Arrays.asList()));
			}
		});

		// When
		List<ProductSku> found = skuLookup.findBySkuCodes(Arrays.asList(SKU_CODE, SKU2_CODE));

		// Then
		assertEquals("Skus should not have been found", Collections.<ProductSku>emptyList(), found);
	}

	@Test
	public void shouldReturnTrueWhenProductSkuExists() {
		context.checking(new Expectations() {
			{
				allowing(persistenceEngine).retrieveByNamedQuery(IS_PRODUCT_SKU_EXISTS_NAMED_QUERY, SKU_CODE);
				will(returnValue(Arrays.asList("SKU1")));
			}
		});

		Boolean result = skuLookup.isProductSkuExist(SKU_CODE);

		assertTrue("Return value must be true when product sku exists", result);
	}

	@Test
	public void shouldReturnFalseWhenProductSkuDoesNotExist() {
		context.checking(new Expectations() {
			{
				allowing(persistenceEngine).retrieveByNamedQuery(IS_PRODUCT_SKU_EXISTS_NAMED_QUERY, SKU_CODE);
				will(returnValue(Arrays.asList()));
			}
		});

		Boolean result = skuLookup.isProductSkuExist(SKU_CODE);

		assertFalse("Return value must be false when product sku does not exist", result);
	}

	@Test (expected = EpServiceException.class)
	public void shouldThrowExceptionOnDuplicateSKUsWhenCheckingProductSkuExistence() {
		context.checking(new Expectations() {
			{
				allowing(persistenceEngine).retrieveByNamedQuery(IS_PRODUCT_SKU_EXISTS_NAMED_QUERY, SKU_CODE);
				will(returnValue(Arrays.asList("SKU1", "SKU2")));
			}
		});

		skuLookup.isProductSkuExist(SKU_CODE);
	}

	@Test (expected = Exception.class)
	public void shouldThrowExceptionOnDbCallFailureSKUsWhenCheckingProductSkuExistence() {
		context.checking(new Expectations() {
			{
				allowing(persistenceEngine).retrieveByNamedQuery(IS_PRODUCT_SKU_EXISTS_NAMED_QUERY, SKU_CODE);
				will(throwException(new Exception("DB failure")));
			}
		});

		skuLookup.isProductSkuExist(SKU_CODE);
	}
}

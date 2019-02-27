/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.caching.core.catalog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.cache.Cache;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.service.catalog.ProductLookup;
import com.elasticpath.service.catalog.ProductSkuLookup;

public class CachingProductSkuLookupImplTest {
	private static final long PRODUCT_UID = 3L;
	private static final String SKU_CODE = "sku-code";

	@Mock private Cache<Long, Long> uidToProductCache;
	@Mock private Cache<String, Long> guidToProductCache;
	@Mock private Cache<String, Long> skuCodeToProductCache;
	@Mock private Cache<String, Boolean> skuCodeToExistenceStatusCache;
	@Mock private ProductLookup productLookup;
	@Mock private ProductSkuLookup fallbackSkuLookup;
	private CachingProductSkuLookupImpl productSkuLookup;
	private ProductSkuImpl sku;
	private ProductSkuImpl sku2;
	private ProductImpl product;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		productSkuLookup = new CachingProductSkuLookupImpl();
		productSkuLookup.setFallbackProductSkuLookup(fallbackSkuLookup);
		productSkuLookup.setProductLookup(productLookup);
		productSkuLookup.setGuidToProductCache(guidToProductCache);
		productSkuLookup.setSkuCodeToProductCache(skuCodeToProductCache);
		productSkuLookup.setUidToProductCache(uidToProductCache);
		productSkuLookup.setSkuCodeToExistenceStatusCache(skuCodeToExistenceStatusCache);

		product = new ProductImpl();
		product.setUidPk(PRODUCT_UID);

		sku = new ProductSkuImpl();
		sku.setUidPk(1L);
		sku.setSkuCode(SKU_CODE);
		sku.setGuid("sku-guid");
		sku.setProduct(product);

		sku2 = new ProductSkuImpl();
		sku2.setUidPk(2L);
		sku2.setSkuCode("sku2-code");
		sku2.setGuid("sku2-guid");
		sku2.setProduct(product);
	}

	@Test
	public void testFindByUidLoadsFromFallbackOnCacheMiss() throws Exception {
		// Given
		when(uidToProductCache.get(sku.getUidPk())).thenReturn(null);
		when(fallbackSkuLookup.findByUid(sku.getUidPk())).thenReturn(sku);

		// When
		ProductSku found = productSkuLookup.findByUid(sku.getUidPk());

		// Then
		assertSame("Sku should have been found from the fallback lookup", sku, found);
		verifySkuWasCached();
	}

	@Test
	public void testFindByUidLoadsFromCacheOnCacheHit() throws Exception {
		// Given
		when(uidToProductCache.get(sku.getUidPk())).thenReturn(product.getUidPk());
		when(productLookup.findByUid(product.getUidPk())).thenReturn(product);

		// When
		ProductSku found = productSkuLookup.findByUid(sku.getUidPk());

		// Then
		assertSame("Sku should have been found by looking up the cached product uid and then retrieve the product from the product lookup",
				sku, found);
		verifyThatSkuWasNotCached();
	}

	@Test
	public void testFindByUidReturnsNullOnCacheHitButProductLookupFailure() throws Exception {
		// Given
		when(uidToProductCache.get(sku.getUidPk())).thenReturn(product.getUidPk());
		when(productLookup.findByUid(product.getUidPk())).thenReturn(null);

		// When
		ProductSku found = productSkuLookup.findByUid(sku.getUidPk());

		// Then
		assertNull("Sku was not found because productLookup didn't return a product", found);
		verifyThatSkuWasNotCached();
	}


	@Test
	public void testFindByUidReturnsNullOnCacheMissAndFallbackReaderFailure() throws Exception {
		// Given
		when(uidToProductCache.get(sku.getUidPk())).thenReturn(null);
		when(fallbackSkuLookup.findByUid(sku.getUidPk())).thenReturn(null);

		// When
		ProductSku found = productSkuLookup.findByUid(sku.getUidPk());

		// Then
		assertNull("Sku was not found because neither the cache nor the fallback lookup returned anything useful", found);
		verifyThatSkuWasNotCached();
	}

	@Test
	public void testFindByUidsLoadsFromFallbackOnCacheMissAndFromProductReadOnCacheHit() throws Exception {
		// Given
		when(uidToProductCache.get(sku.getUidPk())).thenReturn(null);
		when(uidToProductCache.get(sku2.getUidPk())).thenReturn(product.getUidPk());
		when(fallbackSkuLookup.findByUid(sku.getUidPk())).thenReturn(sku);
		when(productLookup.findByUid(product.getUidPk())).thenReturn(product);

		// When
		List<ProductSku> found = productSkuLookup.findByUids(Arrays.asList(sku.getUidPk(), sku2.getUidPk()));

		// Then
		assertSame("Sku1 should have been found from the fallback lookup", sku, found.get(0));
		assertSame("Sku2 should have been found from cache", sku2, found.get(1));
		verifySkuWasCached();
	}

	@Test
	public void testFindByUidsReturnsEmptyListIfFindFails() throws Exception {
		// Given
		when(uidToProductCache.get(sku.getUidPk())).thenReturn(null);
		when(uidToProductCache.get(sku2.getUidPk())).thenReturn(null);
		when(fallbackSkuLookup.findByUid(sku.getUidPk())).thenReturn(null);
		when(fallbackSkuLookup.findByUid(sku2.getUidPk())).thenReturn(null);

		// When
		List<ProductSku> found = productSkuLookup.findByUids(Arrays.asList(sku.getUidPk(), sku2.getUidPk()));

		// Then
		assertEquals("Nothing was found, so we should return an empty list", Collections.emptyList(), found);
	}

	@Test
	public void testFindByGuidLoadsFromFallbackOnCacheMiss() throws Exception {
		// Given
		when(guidToProductCache.get(sku.getGuid())).thenReturn(null);
		when(fallbackSkuLookup.findByGuid(sku.getGuid())).thenReturn(sku);

		// When
		ProductSku found = productSkuLookup.findByGuid(sku.getGuid());

		// Then
		assertSame("Sku should have been found from the fallback lookup", sku, found);
		verifySkuWasCached();
	}

	@Test
	public void testFindByGuidLoadsFromCacheOnCacheHit() throws Exception {
		// Given
		when(guidToProductCache.get(sku.getGuid())).thenReturn(product.getUidPk());
		when(productLookup.findByUid(product.getUidPk())).thenReturn(product);

		// When
		ProductSku found = productSkuLookup.findByGuid(sku.getGuid());

		// Then
		assertSame("Sku should have been found by looking up the cached product uid and then retrieve the product from the product lookup",
				sku, found);
		verifyThatSkuWasNotCached();
	}

	@Test
	public void testFindByGuidReturnsNullOnCacheHitButProductLookupFailure() throws Exception {
		// Given
		when(guidToProductCache.get(sku.getGuid())).thenReturn(product.getUidPk());
		when(productLookup.findByUid(product.getUidPk())).thenReturn(null);

		// When
		ProductSku found = productSkuLookup.findByGuid(sku.getGuid());

		// Then
		assertNull("Sku was not found because productLookup didn't return a product", found);
		verifyThatSkuWasNotCached();
	}

	@Test
	public void testFindByGuidReturnsNullOnCacheMissAndFallbackReaderFailure() throws Exception {
		// Given
		when(guidToProductCache.get(sku.getGuid())).thenReturn(null);
		when(fallbackSkuLookup.findByGuid(sku.getGuid())).thenReturn(null);

		// When
		ProductSku found = productSkuLookup.findByGuid(sku.getGuid());

		// Then
		assertNull("Sku was not found because neither the cache nor the fallback lookup returned anything useful", found);
		verifyThatSkuWasNotCached();
	}

	@Test
	public void testFindBySkuCodeLoadsFromFallbackOnCacheMiss() throws Exception {
		// Given
		when(skuCodeToProductCache.get(sku.getSkuCode())).thenReturn(null);
		when(fallbackSkuLookup.findBySkuCode(sku.getSkuCode())).thenReturn(sku);

		// When
		ProductSku found = productSkuLookup.findBySkuCode(sku.getSkuCode());

		// Then
		assertSame("Sku should have been found from the fallback lookup", sku, found);
		verifySkuWasCached();
	}

	@Test
	public void testFindBySkuCodeLoadsFromCacheOnCacheHit() throws Exception {
		// Given
		when(skuCodeToProductCache.get(sku.getSkuCode())).thenReturn(product.getUidPk());
		when(productLookup.findByUid(product.getUidPk())).thenReturn(product);

		// When
		ProductSku found = productSkuLookup.findBySkuCode(sku.getSkuCode());

		// Then
		assertSame("Sku should have been found by looking up the cached product uid and then retrieve the product from the product lookup",
				sku, found);
		verifyThatSkuWasNotCached();
	}

	@Test
	public void testFindBySkuCodeReturnsNullOnCacheHitButProductLookupFailure() throws Exception {
		// Given
		when(skuCodeToProductCache.get(sku.getSkuCode())).thenReturn(product.getUidPk());
		when(productLookup.findByUid(product.getUidPk())).thenReturn(null);

		// When
		ProductSku found = productSkuLookup.findBySkuCode(sku.getSkuCode());

		// Then
		assertNull("Sku was not found because productLookup didn't return a product", found);
		verifyThatSkuWasNotCached();
	}

	@Test
	public void testFindBySkuCodeReturnsNullOnCacheMissAndFallbackReaderFailure() throws Exception {
		// Given
		when(skuCodeToProductCache.get(sku.getSkuCode())).thenReturn(null);
		when(fallbackSkuLookup.findByUid(sku.getUidPk())).thenReturn(null);

		// When
		ProductSku found = productSkuLookup.findBySkuCode(sku.getSkuCode());

		// Then
		assertNull("Sku was not found because neither the cache nor the fallback lookup returned anything useful", found);
		verifyThatSkuWasNotCached();
	}

	@Test
	public void testFindBySkuCodesLoadsFromFallbackOnCacheMissAndFromProductReadOnCacheHit() throws Exception {
		// Given
		when(skuCodeToProductCache.get(sku.getSkuCode())).thenReturn(null);
		when(skuCodeToProductCache.get(sku2.getSkuCode())).thenReturn(product.getUidPk());
		when(fallbackSkuLookup.findBySkuCode(sku.getSkuCode())).thenReturn(sku);
		when(productLookup.findByUid(product.getUidPk())).thenReturn(product);

		// When
		List<ProductSku> found = productSkuLookup.findBySkuCodes(Arrays.asList(sku.getSkuCode(), sku2.getSkuCode()));

		// Then
		assertSame("Sku1 should have been found from the fallback lookup", sku, found.get(0));
		assertSame("Sku2 should have been found from cache", sku2, found.get(1));
		verifySkuWasCached();
	}

	@Test
	public void testFindBySkuCodesReturnsEmptyListIfFindFails() throws Exception {
		// Given
		when(skuCodeToProductCache.get(sku.getSkuCode())).thenReturn(null);
		when(skuCodeToProductCache.get(sku2.getSkuCode())).thenReturn(null);
		when(fallbackSkuLookup.findByUid(sku.getUidPk())).thenReturn(null);
		when(fallbackSkuLookup.findByUid(sku2.getUidPk())).thenReturn(null);

		// When
		List<ProductSku> found = productSkuLookup.findBySkuCodes(Arrays.asList(sku.getSkuCode(), sku2.getSkuCode()));

		// Then
		assertEquals("Nothing was found, so we should return an empty list", Collections.emptyList(), found);
	}

	@Test
	public void shouldReturnTrueOnCacheHitWhenGettingSkuExistenceStatusHitAndSkuCodeExists() {
		when(skuCodeToExistenceStatusCache.get(SKU_CODE)).thenReturn(Boolean.TRUE);

		Boolean actualResult = productSkuLookup.isProductSkuExist(SKU_CODE);

		assertTrue("Must be true for cache hit when sku code exists", actualResult);
		verifyZeroInteractions(fallbackSkuLookup);
	}

	@Test
	public void shouldReturnFalseOnCacheHitWhenGettingSkuExistenceStatusAndSkuCodeDoesNotExist() {
		when(skuCodeToExistenceStatusCache.get(SKU_CODE)).thenReturn(Boolean.FALSE);

		Boolean actualResult = productSkuLookup.isProductSkuExist(SKU_CODE);

		assertFalse("Must be false for cache hit when sku code does not exist", actualResult);
		verifyZeroInteractions(fallbackSkuLookup);
	}

	@Test
	public void shouldLoadFromFallbackOnCacheMissWhenGettingSkuExistenceStatus() {
		when(skuCodeToExistenceStatusCache.get(SKU_CODE)).thenReturn(null);
		when(fallbackSkuLookup.isProductSkuExist(SKU_CODE)).thenReturn(Boolean.TRUE);

		Boolean actualResult = productSkuLookup.isProductSkuExist(SKU_CODE);

		assertTrue("Must be true for cache hit when sku code exists", actualResult);
		verify(fallbackSkuLookup).isProductSkuExist(SKU_CODE);
		verify(skuCodeToExistenceStatusCache).put(SKU_CODE, actualResult);
	}

	@Test (expected = EpServiceException.class)
	public void shouldThrowExceptionOnCacheMissWhenGettingSkuExistenceStatusAndProductSkuLookupFails() {
		when(skuCodeToExistenceStatusCache.get(SKU_CODE)).thenReturn(null);
		when(fallbackSkuLookup.isProductSkuExist(SKU_CODE)).thenThrow(new EpServiceException("Exception"));

		productSkuLookup.isProductSkuExist(SKU_CODE);

		verifyNoMoreInteractions(fallbackSkuLookup);
	}



	protected void verifySkuWasCached() {
		verify(uidToProductCache).put(sku.getUidPk(), product.getUidPk());
		verify(uidToProductCache).put(sku2.getUidPk(), product.getUidPk());
		verify(guidToProductCache).put(sku.getGuid(), product.getUidPk());
		verify(guidToProductCache).put(sku2.getGuid(), product.getUidPk());
		verify(skuCodeToProductCache).put(sku.getSkuCode(), product.getUidPk());
		verify(skuCodeToProductCache).put(sku2.getSkuCode(), product.getUidPk());
	}

	protected void verifyThatSkuWasNotCached() {
		verify(uidToProductCache, never()).put(anyLong(), anyLong());
		verify(guidToProductCache, never()).put(anyString(), anyLong());
		verify(skuCodeToProductCache, never()).put(anyString(), anyLong());
	}
}

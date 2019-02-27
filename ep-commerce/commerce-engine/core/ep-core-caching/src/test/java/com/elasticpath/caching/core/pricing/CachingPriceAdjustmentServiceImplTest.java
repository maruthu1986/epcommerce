/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.caching.core.pricing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyMapOf;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.cache.Cache;
import com.elasticpath.domain.pricing.PriceAdjustment;
import com.elasticpath.service.pricing.PriceAdjustmentService;

@RunWith(MockitoJUnitRunner.class)
public class CachingPriceAdjustmentServiceImplTest {

	private static final String PRICE_LIST_GUID = "plGuid";
	private static final Collection<String> BUNDLE_CONSTITUENTS_IDS	= Arrays.asList("1", "2", "3", "4", "5");

	@Mock
	private Cache<PriceListGuidAndBundleConstituentsKey, Map<String, PriceAdjustment>> cache;
	@Mock
	private PriceAdjustmentService fallbackService;
	@Mock
	private PriceAdjustment mockPriceAdjustment;


	@InjectMocks
	private CachingPriceAdjustmentServiceImpl cachingPriceAdjustmentService;

	private static final PriceListGuidAndBundleConstituentsKey CACHE_KEY =
		new PriceListGuidAndBundleConstituentsKey(PRICE_LIST_GUID, BUNDLE_CONSTITUENTS_IDS);

	private final Map<String, PriceAdjustment> result = new HashMap<>();

	@Before
	public void setUp() throws Exception {

		result.put("GuidAndAdjustment", mockPriceAdjustment);
	}

	@Test
	public void testFindByPlGUIDAndBCIDSLoadsFromFallbackOnCacheMiss() throws Exception {
		// Given
		when(cache.get(CACHE_KEY)).thenReturn(null);
		when(fallbackService.findByPriceListAndBundleConstituentsAsMap(PRICE_LIST_GUID, BUNDLE_CONSTITUENTS_IDS)).thenReturn(result);

		// When
		Map<String, PriceAdjustment> found = cachingPriceAdjustmentService
			.findByPriceListAndBundleConstituentsAsMap(PRICE_LIST_GUID, BUNDLE_CONSTITUENTS_IDS);

		// Then
		assertThat(result).isSameAs(found);

		verify(cache).put(CACHE_KEY, result);
	}

	@Test
	public void testFindByPlGUIDAndBCIDSLoadsFromCacheOnCacheHit() throws Exception {
		// Given
		when(cache.get(CACHE_KEY)).thenReturn(result);

		// When
		Map<String, PriceAdjustment> found = cachingPriceAdjustmentService
			.findByPriceListAndBundleConstituentsAsMap(PRICE_LIST_GUID, BUNDLE_CONSTITUENTS_IDS);

		// Then
		assertThat(result).isSameAs(found);

		verify(cache, never()).put(eq(CACHE_KEY), anyMapOf(String.class, PriceAdjustment.class));
		verify(fallbackService, never()).findByPriceListAndBundleConstituentsAsMap(PRICE_LIST_GUID, BUNDLE_CONSTITUENTS_IDS);
	}

	@Test
	public void testFindByPlGUIDAndBCIDSThrowsExceptionOnCacheMissAndDbLookupFails() throws Exception {
		// Given
		final String dbErrorMessage = "Db failure";

		when(cache.get(CACHE_KEY)).thenReturn(null);
		when(fallbackService.findByPriceListAndBundleConstituentsAsMap(PRICE_LIST_GUID, BUNDLE_CONSTITUENTS_IDS))
			.thenThrow(new EpServiceException(dbErrorMessage));

		assertThatThrownBy(() -> cachingPriceAdjustmentService.findByPriceListAndBundleConstituentsAsMap(PRICE_LIST_GUID, BUNDLE_CONSTITUENTS_IDS))
			.isInstanceOf(EpServiceException.class)
			.hasMessage(dbErrorMessage);

		verify(cache, never()).put(eq(CACHE_KEY), anyMapOf(String.class, PriceAdjustment.class));
	}

}

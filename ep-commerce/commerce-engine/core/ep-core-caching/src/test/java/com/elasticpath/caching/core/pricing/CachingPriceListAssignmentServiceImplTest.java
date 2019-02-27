/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.caching.core.pricing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.cache.Cache;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.pricing.PriceListAssignment;
import com.elasticpath.service.pricing.PriceListAssignmentService;

/**
 * Unit test for {@link CachingPriceListAssignmentServiceImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CachingPriceListAssignmentServiceImplTest {

	private static final String PLA_ID = "plaId";

	private static final String PLA_NAME = "plaName";

	private static final String CURRENCY_CODE = "currencyCode";

	private static final String CATALOG_CODE = "catalogCode";

	private static final String PRICE_LIST_NAME = "priceListName";

	private static final boolean INCLUDE_HIDDEN = true;

	private static final CatalogAndCurrencyCodeAndHiddenCompositeKey CATALOG_CURRENCY_FALSE_KEY = new CatalogAndCurrencyCodeAndHiddenCompositeKey(
			CATALOG_CODE, CURRENCY_CODE, false);

	private static final CatalogAndCurrencyCodeAndHiddenCompositeKey CATALOG_CURRENCY_TRUE_KEY = new CatalogAndCurrencyCodeAndHiddenCompositeKey(
			CATALOG_CODE, CURRENCY_CODE, INCLUDE_HIDDEN);

	@Mock
	private Catalog catalog;

	@Mock
	private List<String> notCachedCatalogIds;

	@Mock
	private List<PriceListAssignment> notCachedPriceListAssignments;

	@Mock
	private List<PriceListAssignment> cachedPriceListAssignments;

	@Mock
	private PriceListAssignment notCachedPriceListAssignment;

	@InjectMocks
	private CachingPriceListAssignmentServiceImpl cachingPriceListAssignmentService;

	@Mock
	private PriceListAssignmentService fallbackService;

	@Mock
	private Cache<CatalogAndCurrencyCodeAndHiddenCompositeKey, List<PriceListAssignment>> priceListAssignmentsByCatalogAndCurrencyCodeCache;

	@Test
	public void verifySaveOrUpdateCallsFallbackService() {
		when(fallbackService.saveOrUpdate(notCachedPriceListAssignment)).thenReturn(notCachedPriceListAssignment);

		assertThat(cachingPriceListAssignmentService.saveOrUpdate(notCachedPriceListAssignment)).isEqualTo(notCachedPriceListAssignment);

		verify(fallbackService).saveOrUpdate(notCachedPriceListAssignment);
	}

	@Test
	public void verifyFindByGuidCallsFallbackService() {
		when(fallbackService.findByGuid(PLA_ID)).thenReturn(notCachedPriceListAssignment);

		assertThat(cachingPriceListAssignmentService.findByGuid(PLA_ID)).isEqualTo(notCachedPriceListAssignment);

		verify(fallbackService).findByGuid(PLA_ID);
	}

	@Test
	public void verifyFindByNameCallsFallbackService() {
		when(fallbackService.findByName(PLA_NAME)).thenReturn(notCachedPriceListAssignment);

		assertThat(cachingPriceListAssignmentService.findByName(PLA_NAME)).isEqualTo(notCachedPriceListAssignment);

		verify(fallbackService).findByName(PLA_NAME);
	}

	@Test
	public void verifyListCallsFallbackService() {
		when(fallbackService.list()).thenReturn(notCachedPriceListAssignments);

		assertThat(cachingPriceListAssignmentService.list()).isEqualTo(notCachedPriceListAssignments);

		verify(fallbackService).list();
	}

	@Test
	public void verifyListWithBoolCallsFallbackService() {
		when(fallbackService.list(INCLUDE_HIDDEN)).thenReturn(notCachedPriceListAssignments);

		assertThat(cachingPriceListAssignmentService.list(INCLUDE_HIDDEN)).isEqualTo(notCachedPriceListAssignments);

		verify(fallbackService).list(INCLUDE_HIDDEN);
	}

	@Test
	public void verifyListByCatalogAndCurrencyCodeReturnsPLAListFromDaoOnCacheMiss() {
		when(fallbackService.listByCatalogAndCurrencyCode(CATALOG_CODE, CURRENCY_CODE, false)).thenReturn(notCachedPriceListAssignments);
		when(priceListAssignmentsByCatalogAndCurrencyCodeCache.get(CATALOG_CURRENCY_FALSE_KEY)).thenReturn(null);

		assertThat(cachingPriceListAssignmentService.listByCatalogAndCurrencyCode(CATALOG_CODE, CURRENCY_CODE, false))
				.isEqualTo(notCachedPriceListAssignments);

		verify(priceListAssignmentsByCatalogAndCurrencyCodeCache).put(CATALOG_CURRENCY_FALSE_KEY, notCachedPriceListAssignments);
	}

	@Test
	public void verifyListByCatalogAndCurrencyCodeReturnsPLAListFromCacheOnCacheHit() {
		when(priceListAssignmentsByCatalogAndCurrencyCodeCache.get(CATALOG_CURRENCY_TRUE_KEY)).thenReturn(cachedPriceListAssignments);

		assertThat(cachingPriceListAssignmentService.listByCatalogAndCurrencyCode(CATALOG_CODE, CURRENCY_CODE, INCLUDE_HIDDEN))
				.isEqualTo(cachedPriceListAssignments);

		verify(priceListAssignmentsByCatalogAndCurrencyCodeCache, never()).put(CATALOG_CURRENCY_TRUE_KEY, notCachedPriceListAssignments);
	}

	@Test
	public void verifyListByCatalogAndPriceListNamesCallsFallbackService() {
		when(fallbackService.listByCatalogAndPriceListNames(CATALOG_CODE, PRICE_LIST_NAME)).thenReturn(notCachedPriceListAssignments);

		assertThat(cachingPriceListAssignmentService.listByCatalogAndPriceListNames(CATALOG_CODE, PRICE_LIST_NAME))
				.isEqualTo(notCachedPriceListAssignments);

		verify(fallbackService).listByCatalogAndPriceListNames(CATALOG_CODE, PRICE_LIST_NAME);
	}

	@Test
	public void verifyListByCatalogCallsFallbackService() {
		when(fallbackService.listByCatalog(catalog)).thenReturn(notCachedPriceListAssignments);

		assertThat(cachingPriceListAssignmentService.listByCatalog(catalog)).isEqualTo(notCachedPriceListAssignments);

		verify(fallbackService).listByCatalog(catalog);
	}

	@Test
	public void verifyListByCatalogWithBoolCallsFallbackService() {
		when(fallbackService.listByCatalog(catalog, INCLUDE_HIDDEN)).thenReturn(notCachedPriceListAssignments);

		assertThat(cachingPriceListAssignmentService.listByCatalog(catalog, INCLUDE_HIDDEN)).isEqualTo(notCachedPriceListAssignments);

		verify(fallbackService).listByCatalog(catalog, INCLUDE_HIDDEN);
	}

	@Test
	public void verifyListByCatalogCodeCallsFallbackService() {
		when(fallbackService.listByCatalog(CATALOG_CODE)).thenReturn(notCachedPriceListAssignments);

		assertThat(cachingPriceListAssignmentService.listByCatalog(CATALOG_CODE)).isEqualTo(notCachedPriceListAssignments);

		verify(fallbackService).listByCatalog(CATALOG_CODE);
	}

	@Test
	public void verifyListByCatalogCodeWithBoolCallsFallbackService() {
		when(fallbackService.listByCatalog(CATALOG_CODE, INCLUDE_HIDDEN)).thenReturn(notCachedPriceListAssignments);

		assertThat(cachingPriceListAssignmentService.listByCatalog(CATALOG_CODE, INCLUDE_HIDDEN)).isEqualTo(notCachedPriceListAssignments);

		verify(fallbackService).listByCatalog(CATALOG_CODE, INCLUDE_HIDDEN);
	}

	@Test
	public void verifyListByPriceListCallsFallbackService() {
		when(fallbackService.listByPriceList(PRICE_LIST_NAME)).thenReturn(notCachedPriceListAssignments);

		assertThat(cachingPriceListAssignmentService.listByPriceList(PRICE_LIST_NAME)).isEqualTo(notCachedPriceListAssignments);

		verify(fallbackService).listByPriceList(PRICE_LIST_NAME);
	}

	@Test
	public void verifyListAssignedCatalogsCodesCallsFallbackService() {
		when(fallbackService.listAssignedCatalogsCodes()).thenReturn(notCachedCatalogIds);

		assertThat(cachingPriceListAssignmentService.listAssignedCatalogsCodes()).isEqualTo(notCachedCatalogIds);

		verify(fallbackService).listAssignedCatalogsCodes();
	}

	@Test
	public void verifyDeleteCallsFallbackService() {
		cachingPriceListAssignmentService.delete(notCachedPriceListAssignment);

		verify(fallbackService).delete(notCachedPriceListAssignment);
	}
}

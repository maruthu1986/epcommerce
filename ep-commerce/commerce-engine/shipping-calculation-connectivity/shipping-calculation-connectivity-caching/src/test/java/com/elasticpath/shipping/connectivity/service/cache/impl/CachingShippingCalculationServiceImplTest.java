/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.service.cache.impl;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.verification.VerificationMode;

import com.elasticpath.cache.Cache;
import com.elasticpath.money.Money;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItem;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItemContainer;
import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.ShippableItemContainer;
import com.elasticpath.shipping.connectivity.dto.ShippingAddress;
import com.elasticpath.shipping.connectivity.dto.ShippingCalculationResult;
import com.elasticpath.shipping.connectivity.service.ShippingCalculationService;
import com.elasticpath.shipping.connectivity.service.cache.ShippingCalculationResultCacheKey;
import com.elasticpath.shipping.connectivity.service.cache.ShippingCalculationResultCacheKeyBuilder;

/**
 * Tests {@link CachingShippingCalculationServiceImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings({"PMD.TooManyFields", "PMD.TooManyMethods"})
public class CachingShippingCalculationServiceImplTest {

	private static final String STORE_CODE_1 = "storeCode1";
	private static final String STORE_CODE_2 = "storeCode2";
	private static final String SKU_GUID_1 = "skuGuid1";
	private static final String SKU_GUID_2 = "skuGuid2";
	private static final Locale LOCALE_1 = Locale.US;
	private static final Locale LOCALE_2 = Locale.CANADA;
	private static final Currency CURRENCY_1 = Currency.getInstance("CAD");
	private static final Currency CURRENCY_2 = Currency.getInstance("USD");

	@InjectMocks
	private CachingShippingCalculationServiceImpl cachingShippingCalculationService;

	@Mock
	private ShippingCalculationService mockFallBackShippingCalculationService;

	@Mock
	private ShippableItem mockShippableItem;
	@Mock
	private PricedShippableItem mockPricedShippableItem;

	private List<? extends ShippableItem> mockShippableItems;

	private List<PricedShippableItem> mockPricedShippableItems;

	@Mock
	private ShippingAddress mockDestination1;
	@Mock
	private ShippingAddress mockDestination2;
	@Mock
	private ShippableItemContainer<ShippableItem> mockShippableItemContainer1;
	@Mock
	private ShippableItemContainer<ShippableItem> mockShippableItemContainer2;
	@Mock
	private PricedShippableItemContainer<PricedShippableItem> mockPricedShippableItemContainer;
	@Mock
	private ShippingCalculationResult mockShippingCalculationResult1;
	@Mock
	private ShippingCalculationResult mockShippingCalculationResult2;
	private MockShippingCalculationResultCache mockShippingCalculationResultCache;

	@Before
	public void setUp() {
		mockShippingCalculationResultCache = new MockShippingCalculationResultCache();
		cachingShippingCalculationService.setCache(mockShippingCalculationResultCache);
		cachingShippingCalculationService.setCacheKeyBuilderSupplier(this::createCacheKeyBuilder);
		cachingShippingCalculationService.setShippingCalculationService(mockFallBackShippingCalculationService);

		mockShippableItems = singletonList(mockShippableItem);

		mockPricedShippableItems = singletonList(mockPricedShippableItem);

		mockForShippableItem(mockShippableItem, SKU_GUID_1, 1);

		mockForPricedShippableItem(mockPricedShippableItem, SKU_GUID_1, 1, Money.valueOf(BigDecimal.ZERO, CURRENCY_1));

		doReturn(mockShippableItems).when(mockShippableItemContainer1).getShippableItems();
		when(mockShippableItemContainer1.getDestinationAddress()).thenReturn(mockDestination1);
		when(mockShippableItemContainer1.getStoreCode()).thenReturn(STORE_CODE_1);
		when(mockShippableItemContainer1.getLocale()).thenReturn(LOCALE_1);

		doReturn(mockShippableItems).when(mockShippableItemContainer2).getShippableItems();
		when(mockShippableItemContainer2.getDestinationAddress()).thenReturn(mockDestination1);
		when(mockShippableItemContainer2.getStoreCode()).thenReturn(STORE_CODE_1);
		when(mockShippableItemContainer2.getLocale()).thenReturn(LOCALE_1);

		when(mockFallBackShippingCalculationService.getPricedShippingOptions(mockPricedShippableItemContainer))
				.thenReturn(mockShippingCalculationResult1);
		when(mockFallBackShippingCalculationService.getUnpricedShippingOptions(mockShippableItemContainer1)).thenReturn(
				mockShippingCalculationResult1);
	}

	@Test
	public void testGetUnpricedShippingOptionsFromCache() {
		addToCache(mockShippableItems, mockDestination1, STORE_CODE_1, LOCALE_1, mockShippingCalculationResult1);

		testGetUnpricedShippingOptionsUsesCache(mockShippableItemContainer1, mockShippingCalculationResult1);
	}

	@Test
	public void testGetUnpricedShippingOptionsFromCacheWhenDifferentCartSameItemsAndDestination() {
		addToCache(mockShippableItems, mockDestination1, STORE_CODE_1, LOCALE_1, mockShippingCalculationResult1);

		testGetUnpricedShippingOptionsUsesCache(mockShippableItemContainer2, mockShippingCalculationResult1);
	}

	@Test
	public void testGetUnpricedShippingOptionsFromProviderWhenEmptyCache() {
		testGetUnpricedShippingOptionsUsesProvider(mockShippableItemContainer1, mockShippingCalculationResult1);
	}

	@Test
	public void testGetUnpricedShippingOptionsFromProviderWhenDifferentItemQuantities() {
		addToCache(mockShippableItems, mockDestination1, STORE_CODE_1, LOCALE_1, mockShippingCalculationResult1);

		when(mockShippableItem.getQuantity()).thenReturn(2);

		testGetUnpricedShippingOptionsUsesProvider(mockShippableItemContainer1, mockShippingCalculationResult2);
	}

	@Test
	public void testGetUnpricedShippingOptionsFromProviderWhenDifferentItems() {
		addToCache(mockShippableItems, mockDestination1, STORE_CODE_1, LOCALE_1, mockShippingCalculationResult1);

		when(mockShippableItem.getSkuGuid()).thenReturn(SKU_GUID_2);

		testGetUnpricedShippingOptionsUsesProvider(mockShippableItemContainer1, mockShippingCalculationResult2);
	}

	@Test
	public void testGetUnpricedShippingOptionsFromProviderWhenDifferentDestination() {
		addToCache(mockShippableItems, mockDestination1, STORE_CODE_1, LOCALE_1, mockShippingCalculationResult1);

		when(mockShippableItemContainer1.getDestinationAddress()).thenReturn(mockDestination2);

		testGetUnpricedShippingOptionsUsesProvider(mockShippableItemContainer1, mockShippingCalculationResult2);
	}

	@Test
	public void testGetUnpricedShippingOptionsFromProviderWhenDifferentStoreCode() {
		addToCache(mockShippableItems, mockDestination1, STORE_CODE_1, LOCALE_1, mockShippingCalculationResult1);

		when(mockShippableItemContainer1.getStoreCode()).thenReturn(STORE_CODE_2);

		testGetUnpricedShippingOptionsUsesProvider(mockShippableItemContainer1, mockShippingCalculationResult2);
	}

	@Test
	public void testGetUnpricedShippingOptionsFromProviderWhenDifferentLocale() {
		addToCache(mockShippableItems, mockDestination1, STORE_CODE_1, LOCALE_1, mockShippingCalculationResult1);

		when(mockShippableItemContainer1.getLocale()).thenReturn(LOCALE_2);

		testGetUnpricedShippingOptionsUsesProvider(mockShippableItemContainer1, mockShippingCalculationResult2);
	}

	@Test
	public void testGetUnpricedShippingOptionsDoesNotCacheForPricedWhenShippingCostMissing() {
		testGetUnpricedShippingOptionsUsesProvider(mockShippableItemContainer1, mockShippingCalculationResult2);

		testGetPricedShippingOptionsUsesProvider(mockPricedShippableItems, mockDestination1, STORE_CODE_1, LOCALE_1, CURRENCY_1,
				mockShippingCalculationResult2);
	}

	@Test
	public void testGetUnpricedShippingOptionsFromProviderWhenAddItems() {
		addToCache(mockPricedShippableItems, mockDestination1, STORE_CODE_1, LOCALE_1, CURRENCY_1, mockShippingCalculationResult1);

		final ShippableItem newMockShippableItem = mockForShippableItem(mock(ShippableItem.class), SKU_GUID_2, 2);
		doReturn(asList(mockShippableItem, newMockShippableItem)).when(mockShippableItemContainer1).getShippableItems();
		testGetUnpricedShippingOptionsUsesProvider(mockShippableItemContainer1, mockShippingCalculationResult2);
	}

	@Test
	public void testGetPricedShippingOptionsFromCache() {
		addToCache(mockPricedShippableItems, mockDestination1, STORE_CODE_1, LOCALE_1, CURRENCY_1, mockShippingCalculationResult1);

		testGetPricedShippingOptionsUsesCache(mockPricedShippableItems, mockDestination1, STORE_CODE_1, LOCALE_1, CURRENCY_1,
				mockShippingCalculationResult1);
	}

	@Test
	public void testGetPricedShippingOptionsFromProviderWhenEmptyCache() {
		testGetPricedShippingOptionsUsesProvider(mockPricedShippableItems, mockDestination1, STORE_CODE_1, LOCALE_1, CURRENCY_1,
				mockShippingCalculationResult2);
	}

	@Test
	public void testGetPricedShippingOptionsFromProviderWhenDifferentItemQuantities() {
		addToCache(mockPricedShippableItems, mockDestination1, STORE_CODE_1, LOCALE_1, CURRENCY_1, mockShippingCalculationResult1);

		when(mockPricedShippableItem.getQuantity()).thenReturn(2);

		testGetPricedShippingOptionsUsesProvider(mockPricedShippableItems, mockDestination1, STORE_CODE_1, LOCALE_1, CURRENCY_1,
				mockShippingCalculationResult2);
	}

	@Test
	public void testGetPricedShippingOptionsFromProviderWhenDifferentItems() {
		addToCache(mockPricedShippableItems, mockDestination1, STORE_CODE_1, LOCALE_1, CURRENCY_1, mockShippingCalculationResult1);

		when(mockPricedShippableItem.getSkuGuid()).thenReturn(SKU_GUID_2);

		testGetPricedShippingOptionsUsesProvider(mockPricedShippableItems, mockDestination1, STORE_CODE_1, LOCALE_1, CURRENCY_1,
				mockShippingCalculationResult2);
	}

	@Test
	public void testGetPricedShippingOptionsFromProviderWhenAddItems() {
		addToCache(mockPricedShippableItems, mockDestination1, STORE_CODE_1, LOCALE_1, CURRENCY_1, mockShippingCalculationResult1);

		final PricedShippableItem newMockPricedShippableItem = mockForPricedShippableItem(
				mock(PricedShippableItem.class), SKU_GUID_2, 2, Money.valueOf(BigDecimal.ONE, CURRENCY_1));

		testGetPricedShippingOptionsUsesProvider(asList(mockPricedShippableItem, newMockPricedShippableItem),
				mockDestination1, STORE_CODE_1, LOCALE_1, CURRENCY_1,
				mockShippingCalculationResult2);
	}

	@Test
	public void testGetPricedShippingOptionsFromProviderWhenDifferentDestination() {
		addToCache(mockPricedShippableItems, mockDestination1, STORE_CODE_1, LOCALE_1, CURRENCY_1, mockShippingCalculationResult1);

		testGetPricedShippingOptionsUsesProvider(mockPricedShippableItems, mockDestination2, STORE_CODE_1, LOCALE_1, CURRENCY_1,
				mockShippingCalculationResult2);
	}

	@Test
	public void testGetPricedShippingOptionsFromProviderWhenDifferentStoreCode() {
		addToCache(mockPricedShippableItems, mockDestination1, STORE_CODE_1, LOCALE_1, CURRENCY_1, mockShippingCalculationResult1);

		testGetPricedShippingOptionsUsesProvider(mockPricedShippableItems, mockDestination1, STORE_CODE_2, LOCALE_1, CURRENCY_1,
				mockShippingCalculationResult2);
	}

	@Test
	public void testGetPricedShippingOptionsFromProviderWhenDifferentLocale() {
		addToCache(mockPricedShippableItems, mockDestination1, STORE_CODE_1, LOCALE_1, CURRENCY_1,
				mockShippingCalculationResult1);

		testGetPricedShippingOptionsUsesProvider(mockPricedShippableItems, mockDestination1, STORE_CODE_1, LOCALE_2, CURRENCY_1,
				mockShippingCalculationResult2);
	}

	@Test
	public void testGetPricedShippingOptionsFromProviderWhenDifferentCurrency() {
		addToCache(mockPricedShippableItems, mockDestination1, STORE_CODE_1, LOCALE_1, CURRENCY_1, mockShippingCalculationResult1);

		testGetPricedShippingOptionsUsesProvider(mockPricedShippableItems, mockDestination1, STORE_CODE_1, LOCALE_1, CURRENCY_2,
				mockShippingCalculationResult2);
	}

	@Test
	public void testGetPricedShippingOptionsCachesForUnpriced() {
		testGetPricedShippingOptionsUsesProvider(mockPricedShippableItems, mockDestination1, STORE_CODE_1, LOCALE_1, CURRENCY_1,
				mockShippingCalculationResult2);

		// previous priced call should have cached unpriced result as well
		testGetUnpricedShippingOptionsUsesCache(mockShippableItemContainer1, mockShippingCalculationResult2);
	}

	@Test
	public void testGetPricedShippingOptionsFromProviderWhenDifferentPrice() {
		addToCache(mockPricedShippableItems, mockDestination1, STORE_CODE_1, LOCALE_1, CURRENCY_1, mockShippingCalculationResult1);

		when(mockPricedShippableItem.getUnitPrice()).thenReturn(Money.valueOf(BigDecimal.ONE, CURRENCY_1));

		testGetPricedShippingOptionsUsesProvider(mockPricedShippableItems, mockDestination1, STORE_CODE_1, LOCALE_1, CURRENCY_1,
				mockShippingCalculationResult2);
	}

	private ShippableItem mockForShippableItem(final ShippableItem mockShippableItem, final String skuGuid, final int quantity) {
		when(mockShippableItem.getSkuGuid()).thenReturn(skuGuid);
		when(mockShippableItem.getQuantity()).thenReturn(quantity);

		return mockShippableItem;
	}

	private PricedShippableItem mockForPricedShippableItem(final PricedShippableItem mockPricedShippableItem,
														   final String skuGuid,
														   final int quantity,
														   final Money price) {
		when(mockPricedShippableItem.getSkuGuid()).thenReturn(skuGuid);
		when(mockPricedShippableItem.getQuantity()).thenReturn(quantity);
		when(mockPricedShippableItem.getUnitPrice()).thenReturn(price);
		return mockPricedShippableItem;
	}

	private void testGetUnpricedShippingOptionsUsesProvider(final ShippableItemContainer<ShippableItem> shippableItemContainer,
															final ShippingCalculationResult mockShippingCalculationResult) {
		testGetUnpricedShippingOptions(shippableItemContainer, mockShippingCalculationResult, times(1));
	}

	private void testGetUnpricedShippingOptionsUsesCache(final ShippableItemContainer<ShippableItem> shippableItemContainer,
														 final ShippingCalculationResult mockShippingCalculationResult) {
		testGetUnpricedShippingOptions(shippableItemContainer, mockShippingCalculationResult, never());
	}

	private void testGetUnpricedShippingOptions(final ShippableItemContainer<ShippableItem> shippableItemContainer,
												final ShippingCalculationResult mockShippingCalculationResult,
												final VerificationMode mode) {
		when(mockFallBackShippingCalculationService.getUnpricedShippingOptions(shippableItemContainer)).thenReturn(mockShippingCalculationResult);

		final ShippingCalculationResult shippingCalculationResult = cachingShippingCalculationService
				.getUnpricedShippingOptions(shippableItemContainer);

		assertEquals("shipping calculation result does not match expected result", mockShippingCalculationResult, shippingCalculationResult);
		verify(mockFallBackShippingCalculationService, mode).getUnpricedShippingOptions(shippableItemContainer);
	}

	private void testGetPricedShippingOptionsUsesProvider(final Collection<PricedShippableItem> pricedShippableItems, final ShippingAddress
			destination,
														  final String storeCode, final Locale locale, final Currency currency,
														  final ShippingCalculationResult mockShippingCalculationResult) {
		testGetPricedShippingOptions(pricedShippableItems, destination, storeCode, locale, currency, mockShippingCalculationResult,
				times(1));
	}

	private void testGetPricedShippingOptionsUsesCache(final Collection<PricedShippableItem> pricedShippableItems, final ShippingAddress destination,
													   final String storeCode, final Locale locale, final Currency currency,
													   final ShippingCalculationResult mockShippingCalculationResult) {
		testGetPricedShippingOptions(pricedShippableItems, destination, storeCode, locale, currency, mockShippingCalculationResult,
				never());
	}

	@SuppressWarnings({"PMD.ExcessiveParameterList", "checkstyle:parameternumber"})
	private void testGetPricedShippingOptions(final Collection<PricedShippableItem> pricedShippableItems, final ShippingAddress destination,
											  final String storeCode, final Locale locale, final Currency currency,
											  final ShippingCalculationResult mockShippingCalculationResult, final VerificationMode mode) {
		when(mockPricedShippableItemContainer.getCurrency()).thenReturn(currency);
		when(mockPricedShippableItemContainer.getShippableItems()).thenReturn(pricedShippableItems);
		when(mockPricedShippableItemContainer.getDestinationAddress()).thenReturn(destination);
		when(mockPricedShippableItemContainer.getStoreCode()).thenReturn(storeCode);
		when(mockPricedShippableItemContainer.getLocale()).thenReturn(locale);

		when(mockFallBackShippingCalculationService.getPricedShippingOptions(mockPricedShippableItemContainer))
				.thenReturn(mockShippingCalculationResult);

		final ShippingCalculationResult shippingCalculationResult = cachingShippingCalculationService
				.getPricedShippingOptions(mockPricedShippableItemContainer);

		assertEquals("shipping calculation result does not match expected result", mockShippingCalculationResult, shippingCalculationResult);
		verify(mockFallBackShippingCalculationService, mode).getPricedShippingOptions(mockPricedShippableItemContainer);
	}

	private void addToCache(final Collection<? extends ShippableItem> shippableItems,
							final ShippingAddress destination,
							final String storeCode,
							final Locale locale,
							final ShippingCalculationResult result) {
		ShippingCalculationResultCacheKey cacheKey = createUnpricedCacheKey(shippableItems, destination, storeCode, locale);
		mockShippingCalculationResultCache.put(cacheKey, result);
	}

	private void addToCache(final Collection<PricedShippableItem> shippableItems,
							final ShippingAddress destination,
							final String storeCode,
							final Locale locale,
							final Currency currency,
							final ShippingCalculationResult result) {
		ShippingCalculationResultCacheKey cacheKey = createPricedCacheKey(shippableItems, destination, storeCode, locale, currency);
		mockShippingCalculationResultCache.put(cacheKey, result);
	}

	private ShippingCalculationResultCacheKey createUnpricedCacheKey(final Collection<? extends ShippableItem> shippableItems,
																	 final ShippingAddress destination,
																	 final String storeCode,
																	 final Locale locale) {
		return createCacheKeyBuilder()
				.withShippableItems(shippableItems)
				.withDestination(destination)
				.withStoreCode(storeCode)
				.withLocale(locale)
				.build();
	}

	private ShippingCalculationResultCacheKey createPricedCacheKey(final Collection<PricedShippableItem> shippableItems,
																   final ShippingAddress destination,
																   final String storeCode,
																   final Locale locale,
																   final Currency currency) {
		return createCacheKeyBuilder()
				.withPricedShippableItems(shippableItems)
				.withDestination(destination)
				.withStoreCode(storeCode)
				.withLocale(locale)
				.withCurrency(currency)
				.build();
	}

	private ShippingCalculationResultCacheKeyBuilder createCacheKeyBuilder() {
		return new ShippingCalculationResultCacheKeyBuilderImpl();
	}

	private static class MockShippingCalculationResultCache implements Cache<ShippingCalculationResultCacheKey, ShippingCalculationResult> {

		private final Map<ShippingCalculationResultCacheKey, ShippingCalculationResult> cacheMap = new HashMap<>();

		@Override
		public ShippingCalculationResult get(final ShippingCalculationResultCacheKey key) {
			return cacheMap.get(key);
		}

		@Override
		public void put(final ShippingCalculationResultCacheKey key, final ShippingCalculationResult value) {
			cacheMap.put(key, value);
		}

		@Override
		public boolean remove(final ShippingCalculationResultCacheKey key) {
			return cacheMap.remove(key) != null;
		}

		@Override
		public void removeAll() {
			cacheMap.clear();
		}

		@Override
		public String getName() {
			return null;
		}

		@Override
		public <T> T unwrap(final Class<T> clazz) {
			return null;
		}

		public Map<ShippingCalculationResultCacheKey, ShippingCalculationResult> getCacheMap() {
			return cacheMap;
		}
	}
}

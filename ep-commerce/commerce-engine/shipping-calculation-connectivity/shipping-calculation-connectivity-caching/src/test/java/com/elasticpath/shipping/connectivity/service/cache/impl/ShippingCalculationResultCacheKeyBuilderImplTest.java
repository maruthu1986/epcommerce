/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.service.cache.impl;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Currency;
import java.util.Locale;

import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.ShippingAddress;
import com.elasticpath.shipping.connectivity.dto.impl.ShippingAddressImpl;
import com.elasticpath.shipping.connectivity.service.cache.ShippingCalculationResultCacheKey;

/**
 * Tests {@link ShippingCalculationResultCacheKeyBuilderImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
@Ignore
public class ShippingCalculationResultCacheKeyBuilderImplTest {

	private static final Currency CURRENCY = Currency.getInstance("USD");
	private static final String SKU_GUID = "skuGuid";
	private static final String STORE_CODE = "storeCode";
	private static final Locale LOCALE = Locale.US;
	private static final int SHOPPING_ITEM_QUANTITY = 1;
	private static final ShippingAddressImpl DESTINATION = new ShippingAddressImpl();

	static {
		DESTINATION.setGuid("destinationGuid");
	}

	private ShippingCalculationResultCacheKeyBuilderImpl shippingCalculationResultCacheKeyBuilderImpl;

	@Mock
	private ShippableItem mockShippableItem;
	@Mock
	private ShippingAddress mockAddress;

	@Before
	public void setUp() {
		shippingCalculationResultCacheKeyBuilderImpl = new ShippingCalculationResultCacheKeyBuilderImpl();

		when(mockShippableItem.getSkuGuid()).thenReturn(SKU_GUID);
		when(mockShippableItem.getQuantity()).thenReturn(SHOPPING_ITEM_QUANTITY);
		when(mockAddress.getGuid()).thenReturn(DESTINATION.getGuid());
	}

	@Test
	public void testBuild() {
		final ShippingCalculationResultCacheKeyImpl expectedCacheKey = new ShippingCalculationResultCacheKeyImpl(
				ImmutableSet.of(SKU_GUID + ":" + SHOPPING_ITEM_QUANTITY), DESTINATION, STORE_CODE,
				LOCALE.toString(), CURRENCY.getCurrencyCode());

		final ShippingCalculationResultCacheKey actualCacheKey = shippingCalculationResultCacheKeyBuilderImpl
				.withShippableItems(singletonList(mockShippableItem))
				.withDestination(mockAddress)
				.withLocale(LOCALE)
				.withCurrency(CURRENCY)
				.withStoreCode(STORE_CODE)
				.build();

		assertThat(actualCacheKey)
				.as("shipping calculation result cache key does not match expected key")
				.isEqualTo(expectedCacheKey);
	}

	@Test
	public void testBuildFrom() {
		final ShippingCalculationResultCacheKeyImpl expectedCacheKey = new ShippingCalculationResultCacheKeyImpl(ImmutableSet.of("FOO"),
				DESTINATION, STORE_CODE, LOCALE.toString(), CURRENCY.getCurrencyCode());

		final ShippingCalculationResultCacheKey actualCacheKey = shippingCalculationResultCacheKeyBuilderImpl.from(expectedCacheKey).build();
		assertThat(actualCacheKey)
				.as("shipping calculation result cache key does not match expected key")
				.isEqualTo(expectedCacheKey);
	}
}

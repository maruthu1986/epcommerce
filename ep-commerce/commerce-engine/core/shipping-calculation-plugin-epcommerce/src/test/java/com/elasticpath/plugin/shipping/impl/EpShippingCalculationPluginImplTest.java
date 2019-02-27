/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.plugin.shipping.impl;

import static com.elasticpath.test.util.MatcherFactory.supplierOf;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.shipping.ShippingCostCalculationMethod;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.money.Money;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.shipping.ShippingOptionTransformer;
import com.elasticpath.service.shipping.ShippingServiceLevelService;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItem;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItemContainer;
import com.elasticpath.shipping.connectivity.dto.ShippingAddress;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * Tests {@link EpShippingCalculationPluginImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class EpShippingCalculationPluginImplTest {

	private static final String STORE_CODE = "storeCode";
	private static final Locale LOCALE = Locale.US;
	private static final Currency CURRENCY = Currency.getInstance("USD");

	@InjectMocks
	private EpShippingCalculationPluginImpl target;

	@Mock
	private ShippingServiceLevelService shippingServiceLevelService;

	@Mock
	private PricedShippableItem pricedShippableItem;

	@Mock
	private ShippingAddress destinationAddress;

	@Mock
	private ShippingServiceLevel shippingServiceLevel;

	@Mock
	private ShippingCostCalculationMethod shippingCostCalculationMethod;

	@Mock
	private ProductSkuLookup productSkuLookup;

	@Mock
	private ShippingOptionTransformer shippingOptionTransformer;

	@Mock
	private ShippingOption unpricedShippingOption;

	@Mock
	private ShippingOption pricedShippingOption;

	@Mock
	private PricedShippableItemContainer<PricedShippableItem> pricedShippableItemContainer;

	@Before
	@SuppressWarnings("unchecked")
	public void setUp() {
		final List<PricedShippableItem> pricedShippableItems = singletonList(pricedShippableItem);
		when(pricedShippableItem.getTotalPrice()).thenReturn(Money.valueOf(BigDecimal.TEN, CURRENCY));

		when(shippingServiceLevelService.retrieveShippingServiceLevel(STORE_CODE, destinationAddress))
				.thenReturn(singletonList(shippingServiceLevel));

		when(shippingServiceLevel.getShippingCostCalculationMethod()).thenReturn(shippingCostCalculationMethod);

		final Money shippingCost = Money.valueOf(BigDecimal.TEN, CURRENCY);
		when(shippingCostCalculationMethod.calculateShippingCost(
				eq(pricedShippableItems),
				any(Money.class),
				eq(CURRENCY),
				eq(productSkuLookup)))
				.thenReturn(shippingCost);

		when(shippingOptionTransformer.transform(eq(shippingServiceLevel),
												 supplierOf(null),
												 eq(LOCALE))).thenReturn(unpricedShippingOption);

		when(shippingOptionTransformer.transform(eq(shippingServiceLevel),
												 supplierOf(shippingCost),
												 eq(LOCALE))).thenReturn(pricedShippingOption);

		when(pricedShippableItemContainer.getCurrency()).thenReturn(CURRENCY);
		when(pricedShippableItemContainer.getShippableItems()).thenReturn(pricedShippableItems);
		when(pricedShippableItemContainer.getLocale()).thenReturn(LOCALE);
		when(pricedShippableItemContainer.getStoreCode()).thenReturn(STORE_CODE);
		when(pricedShippableItemContainer.getDestinationAddress()).thenReturn(destinationAddress);
	}

	@Test
	public void testGetUnpricedShippingOptions() {
		final List<ShippingOption> unpricedShippingOptions = target.getUnpricedShippingOptions(destinationAddress, STORE_CODE, LOCALE);

		assertThat(unpricedShippingOptions).hasSize(1);
		assertThat(unpricedShippingOptions.get(0)).isEqualTo(unpricedShippingOption);
	}

	@Test
	public void testGetPricedShippingOptions() {
		final List<ShippingOption> pricedShippingOptions = target.getPricedShippingOptions(pricedShippableItemContainer);

		assertThat(pricedShippingOptions).hasSize(1);
		assertThat(pricedShippingOptions.get(0)).isEqualTo(pricedShippingOption);
	}
}

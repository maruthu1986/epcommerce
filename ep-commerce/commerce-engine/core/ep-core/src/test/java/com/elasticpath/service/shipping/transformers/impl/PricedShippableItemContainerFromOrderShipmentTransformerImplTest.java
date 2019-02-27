/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers.impl;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.service.shipping.ShippableItemsPricing;
import com.elasticpath.service.shipping.transformers.PricedShippableItemsTransformer;
import com.elasticpath.service.shipping.transformers.ShippingAddressTransformer;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItem;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItemContainer;
import com.elasticpath.shipping.connectivity.dto.ShippingAddress;
import com.elasticpath.shipping.connectivity.dto.builder.PricedShippableItemContainerBuilder;
import com.elasticpath.shipping.connectivity.dto.builder.populators.PricedShippableItemContainerBuilderPopulator;

/**
 * Tests of {@link PricedShippableItemContainerFromOrderShipmentTransformerImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PricedShippableItemContainerFromOrderShipmentTransformerImplTest {

	private static final String STORE_CODE = "STORE_CODE";
	private static final Locale LOCALE = Locale.CANADA;
	private static final Currency CURRENCY = Currency.getInstance("CAD");
	private static final Money SUBTOTAL_DISCOUNT = Money.valueOf(BigDecimal.TEN, CURRENCY);

	@Mock
	private PricedShippableItemContainerBuilder builder;

	@Mock
	private PricedShippableItemContainerBuilderPopulator<PricedShippableItem> populator;

	@Mock
	private ShippingAddressTransformer shippingAddressTransformer;

	@Mock
	private ShippingAddress destinationAddress;

	@Mock
	private PricedShippableItemsTransformer pricedShippableItemsTransformer;

	@Mock
	private PricedShippableItem pricedShippableItem;

	@Mock
	private Order order;

	@Mock
	private PhysicalOrderShipment orderShipment;

	@Mock
	private OrderAddress orderShipmentAddress;

	private OrderSku shippableOrderSku1;

	private OrderSku shippableOrderSku2;

	private Set<OrderSku> allOrderSkus;

	@Mock
	private PricedShippableItemContainer<PricedShippableItem> expectedContainer;

	@InjectMocks
	private PricedShippableItemContainerFromOrderShipmentTransformerImpl objectUnderTest;

	@Before
	public void setUp() {
		shippableOrderSku1 = mock(OrderSku.class, withSettings().extraInterfaces(ShoppingItemPricingSnapshot.class));
		shippableOrderSku2 = mock(OrderSku.class, withSettings().extraInterfaces(ShoppingItemPricingSnapshot.class));

		allOrderSkus = ImmutableSet.of(shippableOrderSku1, shippableOrderSku2);

		when(order.getCurrency()).thenReturn(CURRENCY);
		when(order.getLocale()).thenReturn(LOCALE);
		when(order.getStoreCode()).thenReturn(STORE_CODE);

		when(orderShipment.getOrder()).thenReturn(order);
		when(orderShipment.getShipmentOrderSkus()).thenReturn(allOrderSkus);
		when(orderShipment.getShipmentAddress()).thenReturn(orderShipmentAddress);
		when(orderShipment.getSubtotalDiscountMoney()).thenReturn(SUBTOTAL_DISCOUNT);

		when(shippingAddressTransformer.apply(orderShipmentAddress)).thenReturn(destinationAddress);

		objectUnderTest.setSupplier(() -> builder);

		when(builder.build()).thenReturn(expectedContainer);
		when(builder.getPopulator()).thenReturn(populator);

		when(populator.withShippableItems(anyCollection())).thenReturn(populator);
		when(populator.withDestinationAddress(any(ShippingAddress.class))).thenReturn(populator);
		when(populator.withStoreCode(STORE_CODE)).thenReturn(populator);
		when(populator.withCurrency(CURRENCY)).thenReturn(populator);
		when(populator.withLocale(LOCALE)).thenReturn(populator);
	}

	@Test
	public void verifyTransformerReturnsCorrectResult() {
		when(pricedShippableItemsTransformer.apply(eq(allOrderSkus), any(ShippableItemsPricing.class)))
				.thenReturn(Stream.of(pricedShippableItem));

		final PricedShippableItemContainer<PricedShippableItem> actualContainer = objectUnderTest.apply(orderShipment);

		verify(populator).withShippableItems(singletonList(pricedShippableItem));
		verify(populator).withLocale(LOCALE);
		verify(populator).withCurrency(CURRENCY);
		verify(populator).withDestinationAddress(destinationAddress);
		verify(populator).withStoreCode(STORE_CODE);

		verify(builder).build();

		assertThat(actualContainer).isSameAs(expectedContainer);
	}

	@Test
	public void verifyCreateShippableItemsPricingSetsStandardFieldsCorrectly() {
		final ShippableItemsPricing actualShippableItemsPricing = objectUnderTest.createShippableItemsPricing(orderShipment);

		assertThat(actualShippableItemsPricing.getCurrency()).isEqualTo(CURRENCY);
		assertThat(actualShippableItemsPricing.getSubtotalDiscount()).isEqualTo(SUBTOTAL_DISCOUNT);
		assertThat(actualShippableItemsPricing.getShippableItemPredicate()).isEqualTo(Optional.empty());
	}

	@Test
	public void verifyCreateShippableItemsPricingCreatesCorrectPricingFunction() {
		final ShippableItemsPricing actualShippableItemsPricing = objectUnderTest.createShippableItemsPricing(orderShipment);

		final Function<ShoppingItem, ShoppingItemPricingSnapshot> actualPricingFunction
				= actualShippableItemsPricing.getShoppingItemPricingFunction();

		assertThat(actualPricingFunction).isNotNull();

		final ShoppingItemPricingSnapshot actualPricingSnapshot1 = actualPricingFunction.apply(shippableOrderSku1);
		assertThat(actualPricingSnapshot1).isSameAs(shippableOrderSku1);
		final ShoppingItemPricingSnapshot actualPricingSnapshot2 = actualPricingFunction.apply(shippableOrderSku2);
		assertThat(actualPricingSnapshot2).isSameAs(shippableOrderSku2);
	}
}

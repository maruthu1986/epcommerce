/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers.visitors.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Currency;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.shipping.transformers.ShippingAddressTransformer;
import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.ShippingAddress;
import com.elasticpath.shipping.connectivity.dto.builder.populators.BaseShippableItemContainerBuilderPopulator;

/**
 * Unit test for {@link ShippableItemContainerPopulatorVisitorImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShippableItemContainerPopulatorVisitorImplTest {
	private static final String STORE_CODE = "STORE_CODE";
	private static final Locale LOCALE = Locale.CANADA;
	private static final Currency CURRENCY = Currency.getInstance("CAD");

	@Mock
	private ShoppingCart shoppingCart;

	@Mock
	private Shopper shopper;

	@Mock
	private Store store;

	@Mock
	private Address shoppingCartAddress;

	@Mock
	private ShippingAddress shippingAddress;

	@Mock
	private ShippableItem shippableItem;

	@Mock
	private ShippingAddressTransformer shippingAddressTransformer;

	@Mock
	private BaseShippableItemContainerBuilderPopulator populator;

	@InjectMocks
	private ShippableItemContainerPopulatorVisitorImpl objectUnderTest;

	@Before
	public void setUp() {
		when(shoppingCart.getShopper()).thenReturn(shopper);
		when(shoppingCart.getShippingAddress()).thenReturn(shoppingCartAddress);
		when(shoppingCart.getStore()).thenReturn(store);

		when(shopper.getLocale()).thenReturn(LOCALE);
		when(shopper.getCurrency()).thenReturn(CURRENCY);

		when(store.getCode()).thenReturn(STORE_CODE);

		when(shippingAddressTransformer.apply(shoppingCartAddress)).thenReturn(shippingAddress);
	}

	@Test
	public void verifyAllFieldsAreSetOnPopulator() {
		when(populator.withDestinationAddress(shippingAddress)).thenReturn(populator);
		when(populator.withStoreCode(STORE_CODE)).thenReturn(populator);
		when(populator.withLocale(LOCALE)).thenReturn(populator);
		when(populator.withCurrency(CURRENCY)).thenReturn(populator);
		
		objectUnderTest.accept(shoppingCart, Collections.singletonList(shippableItem), populator);

		verify(populator).withDestinationAddress(shippingAddress);
		verify(populator).withStoreCode(STORE_CODE);
		verify(populator).withLocale(LOCALE);
		verify(populator).withCurrency(CURRENCY);
	}
}
/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers.visitors.impl;

import static java.util.Collections.singletonList;

import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.builder.populators.ShippableItemContainerBuilderPopulator;

/**
 * Unit test for {@link UnpricedShippableItemContainerItemsPopulatorVisitorImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class UnpricedShippableItemContainerItemsPopulatorVisitorImplTest {
	@Mock
	private ShoppingCart shoppingCart;

	@Mock
	private ShippableItem shippableItem;

	@Mock
	private ShippableItemContainerBuilderPopulator<ShippableItem> populator;

	private UnpricedShippableItemContainerItemsPopulatorVisitorImpl objectUnderTest;

	@Before
	public void setUp() {
		objectUnderTest = new UnpricedShippableItemContainerItemsPopulatorVisitorImpl();
	}

	@Test
	public void verifyShippableItemsSet() {
		final List<ShippableItem> shippableItems = singletonList(shippableItem);

		objectUnderTest.accept(shoppingCart, shippableItems, populator);

		verify(populator).withShippableItems(shippableItems);
	}
}
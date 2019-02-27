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
import com.elasticpath.shipping.connectivity.dto.PricedShippableItem;
import com.elasticpath.shipping.connectivity.dto.builder.populators.PricedShippableItemContainerBuilderPopulator;

/**
 * Unit test for {@link PricedShippableItemContainerItemsPopulatorVisitorImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PricedShippableItemContainerItemsPopulatorVisitorImplTest {
	@Mock
	private ShoppingCart shoppingCart;
	
	@Mock
	private PricedShippableItem shippableItem;
	
	@Mock
	private PricedShippableItemContainerBuilderPopulator<PricedShippableItem> populator;
	
	private PricedShippableItemContainerItemsPopulatorVisitorImpl objectUnderTest;
	
	@Before
	public void setUp() {
		objectUnderTest = new PricedShippableItemContainerItemsPopulatorVisitorImpl();
	}
	
	@Test
	public void verifyShippableItemsSet() {
		final List<PricedShippableItem> shippableItems = singletonList(shippableItem);

		objectUnderTest.accept(shoppingCart, shippableItems, populator);

		verify(populator).withShippableItems(shippableItems);
	}
}
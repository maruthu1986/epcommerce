/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers.visitors.impl;

import static java.util.Collections.singletonList;

import static com.elasticpath.commons.constants.MetaDataConstants.SHOPPING_CART_KEY;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.builder.populators.BaseShippableItemContainerBuilderPopulator;

/**
 * Unit test for {@link ShippableItemContainerMetadataPopulatorVisitorImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShippableItemContainerMetadataPopulatorVisitorImplTest {
	@Mock
	private ShoppingCart shoppingCart;

	@Mock
	private ShippableItem shippableItem;

	@Mock
	private BaseShippableItemContainerBuilderPopulator populator;

	private ShippableItemContainerMetadataPopulatorVisitorImpl objectUnderTest;

	@Before
	public void setUp() {
		objectUnderTest = new ShippableItemContainerMetadataPopulatorVisitorImpl();
	}

	@Test
	public void verifyShoppingCartSetAsMetadata() {
		objectUnderTest.accept(shoppingCart, singletonList(shippableItem), populator);

		verify(populator).withField(SHOPPING_CART_KEY, shoppingCart);
	}
}
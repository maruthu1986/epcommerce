/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.plugin.shipping.impl;

import static com.elasticpath.commons.constants.MetaDataConstants.SHOPPING_CART_KEY;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.service.shipping.transformers.PricedShippableItemContainerTransformer;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItem;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItemContainer;
import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.ShippableItemContainer;
import com.elasticpath.shipping.connectivity.dto.ShippingCalculationResult;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;
import com.elasticpath.shipping.connectivity.service.ShippingCalculationService;

/**
 * Tests of {@link UnpricedToPricedShippingCalculationPluginImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class UnpricedToPricedShippingCalculationPluginImplTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ShippingCalculationService shippingCalculationService;

	@Mock
	private PricingSnapshotService pricingSnapshotService;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private ShippableItemContainer<ShippableItem> unpricedShippableItemContainer;

	@Mock
	private PricedShippableItemContainerTransformer pricedShippableItemContainerTransformer;

	@Mock
	private PricedShippableItemContainer<PricedShippableItem> pricedShippableItemContainer;

	@Mock
	private ShoppingCartPricingSnapshot shoppingCartPricingSnapshot;

	@Mock
	private ShoppingCart shoppingCart;

	@Mock
	private ShippingCalculationResult shippingCalculationResult;

	private final List<ShippingOption> availableShippingOptions = Collections.emptyList();

	@InjectMocks
	private UnpricedToPricedShippingCalculationPluginImpl target;

	@Before
	public void setUp() throws Exception {

		when(unpricedShippableItemContainer.getFields().get(SHOPPING_CART_KEY)).thenReturn(shoppingCart);

		when(pricingSnapshotService.getPricingSnapshotForCart(shoppingCart)).thenReturn(shoppingCartPricingSnapshot);
		when(pricedShippableItemContainerTransformer.apply(shoppingCart, shoppingCartPricingSnapshot)).thenReturn(pricedShippableItemContainer);
		when(shippingCalculationService.getPricedShippingOptions(pricedShippableItemContainer)).thenReturn(shippingCalculationResult);
		when(shippingCalculationResult.getAvailableShippingOptions()).thenReturn(availableShippingOptions);
	}

	@Test
	public void testGetUnpricedShippingOptions() {

		final List<ShippingOption> actualShippingOptions = target.getUnpricedShippingOptions(unpricedShippableItemContainer);

		verify(shippingCalculationService).getPricedShippingOptions(pricedShippableItemContainer);
		assertThat(actualShippingOptions).isSameAs(availableShippingOptions);
	}


	@Test
	public void testGetUnpricedShippingOptionsWithoutFoundShoppingCart() {

		when(unpricedShippableItemContainer.getFields().get(SHOPPING_CART_KEY)).thenReturn(null);

		thrown.expect(NullPointerException.class);
		thrown.expectMessage(containsString("Can not find shopping cart with metadata key [SHOPPING_CART_KEY]"));

		target.getUnpricedShippingOptions(unpricedShippableItemContainer);
	}
}

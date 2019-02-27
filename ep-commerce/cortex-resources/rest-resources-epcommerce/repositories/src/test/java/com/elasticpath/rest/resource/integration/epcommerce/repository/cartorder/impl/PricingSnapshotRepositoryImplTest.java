/**
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItemTaxSnapshot;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ExceptionTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl.ReactiveAdapterImpl;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.service.shoppingcart.TaxSnapshotService;

/**
 * Test for {@link PricingSnapshotRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PricingSnapshotRepositoryImplTest {

	private static final String SUCCESSFUL_OPERATION = "The operation should have been successful";

	@Mock
	private OrderSku orderSku;

	@Mock
	private ShoppingItemPricingSnapshot itemPricingSnapshot;

	@Mock
	private ShoppingCartPricingSnapshot pricingSnapshot;

	@Mock
	private ShoppingItemTaxSnapshot itemTaxSnapshot;

	@Mock
	private ShoppingCartTaxSnapshot taxSnapshot;

	@Mock
	private PricingSnapshotService pricingSnapshotService;

	@Mock
	private TaxSnapshotService taxSnapshotService;

	@Mock
	private ShoppingCart shoppingCart;

	@InjectMocks
	private PricingSnapshotRepositoryImpl repository;

	@Mock
	private ExceptionTransformer exceptionTransformer;

	@Spy
	private final ReactiveAdapterImpl reactiveAdapter = new ReactiveAdapterImpl(exceptionTransformer);

	@Test
	public void testGetShoppingCartPricingSnapshot() throws Exception {
		when(pricingSnapshotService.getPricingSnapshotForCart(shoppingCart)).thenReturn(pricingSnapshot);

		final ExecutionResult<ShoppingCartPricingSnapshot> result = repository.getShoppingCartPricingSnapshot(shoppingCart);
		assertTrue(SUCCESSFUL_OPERATION, result.isSuccessful());
		assertEquals("The cart snapshot should match the one from the service", pricingSnapshot, result.getData());
	}

	@Test
	public void testGetShoppingCartPricingSnapshotSingle() throws Exception {
		when(pricingSnapshotService.getPricingSnapshotForCart(shoppingCart)).thenReturn(pricingSnapshot);

		repository.getShoppingCartPricingSnapshotSingle(shoppingCart).test()
				.assertNoErrors()
				.assertValue(pricingSnapshot);
	}

	@Test
	public void testGetPricingSnapshotForOrderSku() {
		when(pricingSnapshotService.getPricingSnapshotForOrderSku(orderSku)).thenReturn(itemPricingSnapshot);

		repository.getPricingSnapshotForOrderSku(orderSku)
				.test()
				.assertNoErrors()
				.assertValue(itemPricingSnapshot);
	}

	@Test
	public void testGetShoppingCartTaxSnapshot() throws Exception {
		when(pricingSnapshotService.getPricingSnapshotForCart(shoppingCart)).thenReturn(pricingSnapshot);
		when(taxSnapshotService.getTaxSnapshotForCart(shoppingCart, pricingSnapshot)).thenReturn(taxSnapshot);

		repository.getShoppingCartTaxSnapshot(shoppingCart)
				.test()
				.assertNoErrors()
				.assertValue(taxSnapshot);

		verify(reactiveAdapter, atLeastOnce()).fromServiceAsSingle(any());
	}

	@Test
	public void testGetTaxSnapshotForOrderSku() {
		when(pricingSnapshotService.getPricingSnapshotForOrderSku(orderSku)).thenReturn(itemPricingSnapshot);
		when(taxSnapshotService.getTaxSnapshotForOrderSku(orderSku, itemPricingSnapshot)).thenReturn(itemTaxSnapshot);

		repository.getTaxSnapshotForOrderSku(orderSku)
				.test()
				.assertNoErrors()
				.assertValue(itemTaxSnapshot);
	}
}
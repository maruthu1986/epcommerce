/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.sellingchannel.presentation.impl;

import static org.junit.Assert.assertSame;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.quantity.Quantity;
import com.elasticpath.domain.shoppingcart.FrequencyAndRecurringPrice;
import com.elasticpath.domain.shoppingcart.FrequencyAndRecurringPriceFactory;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.service.shoppingcart.OrderSkuToPricingSnapshotFunction;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;

public class OrderPresentationHelperImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
			setThreadingPolicy(new Synchroniser());
		}
	};

	@Mock
	private PricingSnapshotService pricingSnapshotService;

	@Mock
	private FrequencyAndRecurringPriceFactory frequencyAndRecurringPriceFactory;

	private OrderPresentationHelperImpl orderPresentationHelper;

	@Before
	public void setUp() {
		orderPresentationHelper = new OrderPresentationHelperImpl() {
			@Override
			protected FrequencyAndRecurringPriceFactory createFrequencyAndRecurringPriceFactory() {
				return frequencyAndRecurringPriceFactory;
			}
		};
		orderPresentationHelper.setOrderSkuToPricingSnapshotFunction(new OrderSkuToPricingSnapshotFunction(pricingSnapshotService));
	}

	@Test
	public void verifyGetFrequencyMapDelegates() throws Exception {
		final Map<Quantity, FrequencyAndRecurringPrice> expectedFrequencyMap = Maps.newHashMap();
		final Order order = context.mock(Order.class);

		final OrderSku orderSku1 = context.mock(OrderSku.class, "OrderSku1");
		final OrderSku orderSku2 = context.mock(OrderSku.class, "OrderSku2");

		final ShoppingItemPricingSnapshot itemPricingSnapshot1 = context.mock(ShoppingItemPricingSnapshot.class, "Snapshot1");
		final ShoppingItemPricingSnapshot itemPricingSnapshot2 = context.mock(ShoppingItemPricingSnapshot.class, "Snapshot2");

		final Collection<? extends ShoppingItem> orderSkus = ImmutableList.of(orderSku1, orderSku2);

		final Map<? extends ShoppingItem, ShoppingItemPricingSnapshot> shoppingItemPricingSnapshotMap = ImmutableMap.of(
				orderSku1, itemPricingSnapshot1,
				orderSku2, itemPricingSnapshot2
		);

		context.checking(new Expectations() {
			{
				allowing(order).getRootShoppingItems();
				will(returnValue(orderSkus));

				oneOf(pricingSnapshotService).getPricingSnapshotForOrderSku(orderSku1);
				will(returnValue(itemPricingSnapshot1));

				oneOf(pricingSnapshotService).getPricingSnapshotForOrderSku(orderSku2);
				will(returnValue(itemPricingSnapshot2));

				oneOf(frequencyAndRecurringPriceFactory).getFrequencyMap(shoppingItemPricingSnapshotMap);
				will(returnValue(expectedFrequencyMap));
			}
		});

		final Map<Quantity, FrequencyAndRecurringPrice> frequencyMap = orderPresentationHelper.getFrequencyMap(order);

		assertSame(expectedFrequencyMap, frequencyMap);
	}

}

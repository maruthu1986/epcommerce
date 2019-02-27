/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.shoppingcart.actions.impl;

import static org.junit.Assert.assertEquals;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.service.shoppingcart.OrderFactory;
import com.elasticpath.service.shoppingcart.actions.CheckoutActionContext;

/**
 * Test class for {@link com.elasticpath.service.shoppingcart.actions.impl.CreateNewOrderCheckoutAction}.
 */
public class CreateNewOrderCheckoutActionTest {

	private CreateNewOrderCheckoutAction checkoutAction;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	@Mock private OrderFactory orderFactory;
	@Mock private Customer customer;
	@Mock private ShoppingCart shoppingCart;
	@Mock private CustomerSession customerSession;
	@Mock private ShoppingCartTaxSnapshot taxSnapshot;
	@Mock private Shopper shopper;
	@Mock private Order order;

	@Before
	public void setUp() {
		checkoutAction = new CreateNewOrderCheckoutAction();
		checkoutAction.setOrderFactory(orderFactory);

		context.checking(new Expectations() {
			{
				allowing(shoppingCart).getShopper();
				will(returnValue(shopper));

				allowing(shopper).getCustomer();
				will(returnValue(customer));
			}
		});
	}

	@Test
	public void verifyExecuteDelegatesToOrderFactory() throws Exception {
		final boolean orderExchange = false;
		final boolean awaitExchangeCompletion = false;
		final OrderReturn exchange = null;

		context.checking(new Expectations() {
			{
				oneOf(orderFactory).createAndPersistNewEmptyOrder(
						customer,
						customerSession,
						shoppingCart,
						orderExchange,
						awaitExchangeCompletion);
				will(returnValue(order));

				oneOf(orderFactory).fillInNewOrderFromShoppingCart(
						order,
						customer,
						customerSession,
						shoppingCart,
						taxSnapshot);
				will(returnValue(order));
			}
		});

		final CheckoutActionContext checkoutActionContext = new CheckoutActionContextImpl(
				shoppingCart,
				taxSnapshot,
				customerSession,
				null,
				orderExchange,
				awaitExchangeCompletion,
				exchange);

		checkoutAction.execute(checkoutActionContext);


		assertEquals("Unexpected Order populated on CheckoutActionContext", order, checkoutActionContext.getOrder());
	}

	@Test
	public void verifyExecuteDelegatesToOrderFactoryForExchanges() throws Exception {
		final boolean orderExchange = true;
		final boolean awaitExchangeCompletion = true;
		final OrderReturn exchange = context.mock(OrderReturn.class);

		context.checking(new Expectations() {
			{
				oneOf(orderFactory).createAndPersistNewEmptyOrder(
						customer,
						customerSession,
						shoppingCart,
						orderExchange,
						awaitExchangeCompletion);
				will(returnValue(order));

				oneOf(orderFactory).fillInNewExchangeOrderFromShoppingCart(
						order,
						customer,
						customerSession,
						shoppingCart,
						taxSnapshot,
						awaitExchangeCompletion,
						exchange);
				will(returnValue(order));
			}
		});

		final CheckoutActionContext checkoutActionContext = new CheckoutActionContextImpl(
				shoppingCart,
				taxSnapshot,
				customerSession,
				null,
				orderExchange,
				awaitExchangeCompletion,
				exchange);

		checkoutAction.execute(checkoutActionContext);


		assertEquals("Unexpected Order populated on CheckoutActionContext", order, checkoutActionContext.getOrder());
	}
}
/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
/**
 *
 */
package com.elasticpath.service.shoppingcart.impl;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.misc.CheckoutResults;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.service.shoppingcart.actions.CheckoutActionContext;
import com.elasticpath.service.shoppingcart.actions.ReversibleCheckoutAction;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for the {@code CheckoutServiceImpl} class.
 */
public class CheckoutServiceImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**
	 * Test that when checkout fails due to an exception thrown during processing of an action,
	 * all previous actions are rolled back.
	 */
	@Test
	public void testCheckoutRollbackMechanism() {
		final Shopper shopper = context.mock(Shopper.class);
		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		final ShoppingCartTaxSnapshot pricingSnapshot = context.mock(ShoppingCartTaxSnapshot.class);
		final ReversibleCheckoutAction action1 = context.mock(ReversibleCheckoutAction.class, "action1");
		final ReversibleCheckoutAction action2 = context.mock(ReversibleCheckoutAction.class, "action2");
		final CustomerSession customerSession = context.mock(CustomerSession.class);

		final CheckoutResults checkoutResults = context.mock(CheckoutResults.class);
		context.checking(new Expectations() { {
			oneOf(action1).execute(with(aNonNull(CheckoutActionContext.class)));
			oneOf(action2).execute(with(aNonNull(CheckoutActionContext.class)));
			will(throwException(new EpSystemException("testing exception handling.")));
			allowing(shopper).getCurrentShoppingCart(); will(returnValue(shoppingCart));
			oneOf(action2).rollback(with(aNonNull(CheckoutActionContext.class)));
			oneOf(action1).rollback(with(aNonNull(CheckoutActionContext.class)));
			allowing(shopper).getUidPk(); will(returnValue(1L));
			oneOf(checkoutResults).setOrder(null);
		} });

		CheckoutServiceImpl service = new CheckoutServiceImpl();
		List<ReversibleCheckoutAction> actionList = new ArrayList<>();
		actionList.add(action1);
		actionList.add(action2);
		service.setReversibleActionList(actionList);

		try {
			service.checkoutInternal(shoppingCart, pricingSnapshot, customerSession, null, true, false, null, checkoutResults);
		} catch (EpSystemException ex) {
			assertNotNull("The EpSystemException should be rethrown after the payments have been rolled back.", ex);
		}
		//The check is performed by expecting the payment service rollBackPayments() method call on the mock object
	}

	/**
	 * TODO: Test that if a payment service throws an exception during the processing of OrderPayments,
	 * all changes to the Order will be rolled back (including any payments already made), and then then EpSystemException
	 * will be re-thrown. The ShoppingCart will still have all of its items.
	 * This test should simply ensure that the PaymentService.initializePayments method throws an exception
	 * and then check that it's handled property.
	 */

	@Test
	public void verifyCheckoutActionContextPopulatedWithAllExpectedParameters() throws Exception {
		final ShoppingCart cart = context.mock(ShoppingCart.class);
		final CustomerSession customerSession = context.mock(CustomerSession.class);
		final OrderPayment templateOrderPayment = context.mock(OrderPayment.class);
		final boolean isOrderExchange = true;
		final boolean awaitExchangeCompletion = true;
		final OrderReturn exchange = context.mock(OrderReturn.class);

		final CheckoutActionContext actionContext = new CheckoutServiceImpl().createActionContext(
				cart,
				null, customerSession,
				templateOrderPayment,
				isOrderExchange,
				awaitExchangeCompletion,
				exchange);
	
		assertEquals("Unexpected shopping cart", cart, actionContext.getShoppingCart());
		assertEquals("Unexpected customer session", customerSession, actionContext.getCustomerSession());
		assertEquals("Unexpected templateOrderPayment", templateOrderPayment, actionContext.getOrderPaymentTemplate());
		assertEquals("Unexpected isOrderExchange", isOrderExchange, actionContext.isOrderExchange());
		assertEquals("Unexpected awaitExchangeCompletion", awaitExchangeCompletion, actionContext.isAwaitExchangeCompletion());
		assertEquals("Unexpected exchange", exchange, actionContext.getExchange());
	}

}

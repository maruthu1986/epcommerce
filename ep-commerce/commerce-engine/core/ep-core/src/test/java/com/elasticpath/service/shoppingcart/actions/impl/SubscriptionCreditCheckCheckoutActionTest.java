/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.shoppingcart.actions.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.impl.CustomerImpl;
import com.elasticpath.domain.customer.impl.CustomerProfileImpl;
import com.elasticpath.domain.customer.impl.CustomerSessionImpl;
import com.elasticpath.domain.customer.impl.CustomerSessionMementoImpl;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.impl.OrderImpl;
import com.elasticpath.domain.order.impl.OrderPaymentImpl;
import com.elasticpath.domain.order.impl.PhysicalOrderShipmentImpl;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shopper.impl.ShopperImpl;
import com.elasticpath.domain.shopper.impl.ShopperMementoImpl;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartImpl;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartMementoImpl;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.plugin.payment.PaymentGatewayType;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

public class SubscriptionCreditCheckCheckoutActionTest {

	private static final Currency CURRENCY = Currency.getInstance("CAD");

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	@Mock
	protected StoreService storeService;
	@Mock
	protected BeanFactory beanFactory;
	@Mock
	protected PaymentGateway gateway;
	@Mock
	protected TimeService timeService;

	private StoreImpl store;
	private SubscriptionCreditCheckCheckoutAction checkoutAction;
	private CheckoutActionContextImpl checkoutActionContext;
	private OrderPaymentImpl orderPayment;
	private ShoppingCartImpl cart;
	private ShoppingCartTaxSnapshot cartTaxSnapshot;
	private OrderImpl order;
	private PhysicalOrderShipmentImpl shipment;
	private CustomerImpl customer;
	private BeanFactoryExpectationsFactory bfef;
	private CustomerSession customerSession;

	@Before
	public void setUp() throws Exception {
		bfef = new BeanFactoryExpectationsFactory(context, beanFactory);
		bfef.allowingBeanFactoryGetBean(ContextIdNames.ORDER_PAYMENT, OrderPaymentImpl.class);

		store = new StoreImpl();
		store.setCode("store");
		store.setPaymentGateways(Collections.<PaymentGateway>singleton(gateway));

		cartTaxSnapshot = context.mock(ShoppingCartTaxSnapshot.class);

		context.checking(new Expectations() { {
			allowing(gateway).getPaymentGatewayType(); will(returnValue(PaymentGatewayType.GIFT_CERTIFICATE));
			allowing(storeService).findStoreWithCode(store.getCode()); will(returnValue(store));
			allowing(timeService).getCurrentTime(); will(returnValue(new Date()));
		} });

		customer = new CustomerImpl();
		customer.setCustomerProfile(new CustomerProfileImpl());

		final Shopper shopper = new ShopperImpl();
		shopper.setShopperMemento(new ShopperMementoImpl());
		shopper.setUidPk(0L);

		customerSession = new CustomerSessionImpl();
		customerSession.setCustomerSessionMemento(new CustomerSessionMementoImpl());
		customerSession.setCurrency(CURRENCY);
		customerSession.setShopper(shopper);

		cart = new MockRecurringShoppingCart();
		cart.setShoppingCartMemento(new ShoppingCartMementoImpl());
		cart.setStore(store);
		cart.setCustomerSession(customerSession);

		order = new OrderImpl();
		order.setOrderNumber("123");
		order.setStoreCode(store.getCode());
		order.setCustomer(customer);
		order.setCurrency(CURRENCY);
		shipment = new PhysicalOrderShipmentImpl();
		shipment.setOrder(order);
		orderPayment = new OrderPaymentImpl();
		orderPayment.setOrderShipment(shipment);
		orderPayment.setPaymentMethod(PaymentType.GIFT_CERTIFICATE);

		checkoutActionContext = new CheckoutActionContextImpl(cart, cartTaxSnapshot, customerSession, orderPayment, false, false, null);
		checkoutActionContext.setOrder(order);

		checkoutAction = new SubscriptionCreditCheckCheckoutAction();
		checkoutAction.setBeanFactory(beanFactory);
		checkoutAction.setStoreService(storeService);
		checkoutAction.setTimeService(timeService);
	}

	@Test
	public void testExecuteCompletesWhenOrderStoreIsNull() throws Exception {
		context.checking(new Expectations() { {
			// Given
			oneOf(cartTaxSnapshot).getTotal(); will(returnValue(BigDecimal.ZERO));

			// Then
			oneOf(gateway).preAuthorize(with(any(OrderPayment.class)), with(aNull(Address.class)));
			oneOf(gateway).reversePreAuthorization(with(any(OrderPayment.class)));
		} });

		// When
		checkoutAction.execute(checkoutActionContext);
	}

	private static class MockRecurringShoppingCart extends ShoppingCartImpl {
		private static final long serialVersionUID = 1L;

		@Override
		public boolean hasRecurringPricedShoppingItems() {
			return true;
		}

		public Currency getCurrency() {
			return Currency.getInstance("CAD");
		}
	}
}

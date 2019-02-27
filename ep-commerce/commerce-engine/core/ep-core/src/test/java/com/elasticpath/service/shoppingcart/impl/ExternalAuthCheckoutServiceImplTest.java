/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.shoppingcart.impl;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.plugin.payment.dto.PaymentOptionFormDescriptor;
import com.elasticpath.service.shoppingcart.ExternalAuthUrls;
import com.elasticpath.service.shoppingcart.OrderSkuFactory;

/**
 * Test class for {@link com.elasticpath.service.shoppingcart.impl.ExternalAuthCheckoutServiceImpl}.
 */
public class ExternalAuthCheckoutServiceImplTest {

	private static final String CANCEL_URL = "TEST_CANCEL_URL";
	private static final String FINISH_URL = "TEST_FINISH_URL";
	private static final String REDIRECT_URL = "TEST_REDIRECT_URL";

	private static final PaymentType PAYMENT_TYPE = PaymentType.CREDITCARD_DIRECT_POST;
	private static final Currency DEFAULT_CURRENCY = Currency.getInstance("CAD");
	private static final Locale DEFAULT_LOCALE = Locale.CANADA;
	private static final long CUSTOMER_UID = 100L;
	private static final String IP_ADDRESS = "TEST_IP_ADDRESS";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	@Mock
	private BeanFactory beanFactory;
	@Mock
	private OrderPayment orderPaymentTemplate;
	@Mock
	private PaymentGateway paymentGateway;
	@Mock
	private ShoppingCart shoppingCart;
	@Mock
	private Address billingAddress;
	@Mock
	private ShoppingCartTaxSnapshot pricingSnapshot;
	@Mock
	private CustomerSession customerSession;
	@Mock
	private Shopper shopper;
	@Mock
	private Customer customer;
	@Mock
	private OrderShipment orderShipmentTemplate;
	@Mock
	private OrderSkuFactory orderSkuFactory;

	private final ExternalAuthCheckoutServiceImpl externalAuthCheckoutServiceImpl = new ExternalAuthCheckoutServiceImpl();

	@Before
	public void setUp() {
		externalAuthCheckoutServiceImpl.setBeanFactory(beanFactory);
		externalAuthCheckoutServiceImpl.setOrderSkuFactory(orderSkuFactory);
		externalAuthCheckoutServiceImpl.setTimeService(Date::new);
	}

	@Test
	public void verifyCreatePaymentOptionFormDescriptor() throws Exception {
		final PaymentOptionFormDescriptor expectedPaymentOptionFormDescriptor = new PaymentOptionFormDescriptor();
		final String expectedRedirectExternalAuthUrl = REDIRECT_URL + "?paymentType=" + PAYMENT_TYPE.getName();
		final String expectedFinishExternalAuthUrl = FINISH_URL + "?paymentType=" + PAYMENT_TYPE.getName();

		ExternalAuthUrls externalAuthUrls = new ExternalAuthUrls();
		externalAuthUrls.setCancelUrl(CANCEL_URL);
		externalAuthUrls.setFinishUrl(FINISH_URL);
		externalAuthUrls.setRedirectUrl(REDIRECT_URL);

		context.checking(new Expectations() { {
			oneOf(beanFactory).getBean(ContextIdNames.ORDER_PAYMENT); will(returnValue(orderPaymentTemplate));
			oneOf(beanFactory).getBean(ContextIdNames.TEMPLATE_ORDER_SHIPMENT); will(returnValue(orderShipmentTemplate));

			ignoring(orderPaymentTemplate);
			allowing(shoppingCart).getBillingAddress(); will(returnValue(billingAddress));
			allowing(customerSession).getCurrency(); will(returnValue(DEFAULT_CURRENCY));
			allowing(customerSession).getLocale(); will(returnValue(DEFAULT_LOCALE));
			allowing(customerSession).getIpAddress(); will(returnValue(IP_ADDRESS));
			allowing(customer).getEmail();
			allowing(customer).getUidPk(); will(returnValue(CUSTOMER_UID));
			allowing(shoppingCart).getShopper(); will(returnValue(shopper));
			allowing(shoppingCart).getRootShoppingItems(); will(returnValue(Collections.emptyList()));
			allowing(shopper).getCustomer(); will(returnValue(customer));
			allowing(orderShipmentTemplate).setShipmentNumber(with(any(String.class)));

			allowing(orderSkuFactory).createOrderSkus(Collections.emptyList(), pricingSnapshot, DEFAULT_LOCALE);
			will(returnValue(Collections.emptyList()));

			oneOf(pricingSnapshot).getTotal();
			oneOf(paymentGateway).buildExternalAuthRequest(orderPaymentTemplate,
					billingAddress,
					expectedRedirectExternalAuthUrl,
					expectedFinishExternalAuthUrl,
					CANCEL_URL);
			will(returnValue(expectedPaymentOptionFormDescriptor));
		} });

		final PaymentOptionFormDescriptor actualPaymentOptionFormDescriptor = externalAuthCheckoutServiceImpl
				.createPaymentOptionFormDescriptor(customerSession, shoppingCart, pricingSnapshot, PAYMENT_TYPE, externalAuthUrls, paymentGateway);

		assertEquals(expectedPaymentOptionFormDescriptor, actualPaymentOptionFormDescriptor);
	}
}

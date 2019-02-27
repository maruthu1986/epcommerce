/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.converters;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.impl.OrderAddressImpl;
import com.elasticpath.domain.order.impl.OrderPaymentImpl;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.rest.definition.purchases.PaymentMeansEntity;
import com.elasticpath.rest.definition.purchases.PaymentMeansPaymentTokenEntity;


/**
 * The test of {@link PaymentMeansEntityConverter}.
 */
public class PaymentMeansEntityConverterTest {

	private final PaymentMeansEntityConverter paymentMeansEntityConverter = new PaymentMeansEntityConverter();

	/**
	 * Test convert to {@link PaymentMeansPaymentTokenEntity}.
	 */
	@Test
	public void testResultOfTransformToPaymentTokenIsAssignableToPaymentMeansPaymentTokenEntity() {
		OrderPaymentImpl orderPayment = new OrderPaymentImpl();
		orderPayment.setPaymentMethod(PaymentType.PAYMENT_TOKEN);
		Pair<OrderPayment, OrderAddress> orderPaymentWrapper = Pair.of(orderPayment, new OrderAddressImpl());

		PaymentMeansEntity paymentTokenEntity = paymentMeansEntityConverter.convert(orderPaymentWrapper);

		assertTrue(PaymentMeansPaymentTokenEntity.class.isAssignableFrom(paymentTokenEntity.getClass()));
	}

}

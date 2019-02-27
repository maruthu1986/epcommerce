/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.test.integration.payment.gateway.impl;

import static org.junit.Assert.assertNotNull;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import com.elasticpath.domain.customer.impl.AbstractPaymentMethodImpl;
import com.elasticpath.domain.customer.impl.PaymentTokenImpl;
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.service.payment.gateway.PaymentMethodTransformer;
import com.elasticpath.service.payment.gateway.PaymentMethodTransformerFactory;
import com.elasticpath.test.integration.BasicSpringContextTest;

/**
 Integration test for {@link PaymentMethodTransformerFactory}.
 */
@DirtiesContext
public class PaymentMethodTransformerFactoryTest extends BasicSpringContextTest {
	@Autowired
	private PaymentMethodTransformerFactory paymentMethodTransformerFactory;
	
	@Rule
	public ExpectedException exception = ExpectedException.none();

	/**
	 * Test factory for payment token.
	 */
	@Test
	public void testFactoryForPaymentToken() {
		PaymentMethod paymentMethod = new PaymentTokenImpl.TokenBuilder().build();
		PaymentMethodTransformer transformer = paymentMethodTransformerFactory.getTransformerInstance(paymentMethod);
		assertNotNull(transformer);
	}
	
	/**
	 * Test factory for non existent payment method.
	 */
	@Test
	public void testFactoryForNonExistentPaymentMethod() {
		PaymentMethod paymentMethod = new UnimplementedPaymentMethod();
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("UnimplementedPaymentMethod");
		exception.expectMessage("No transformer found");
		paymentMethodTransformerFactory.getTransformerInstance(paymentMethod);
	}
	
	class UnimplementedPaymentMethod extends AbstractPaymentMethodImpl<UnimplementedPaymentMethod>  {
		private static final long serialVersionUID = 1L;

		@Override
		public UnimplementedPaymentMethod copy() {
			throw new UnsupportedOperationException();
		}
	}

}

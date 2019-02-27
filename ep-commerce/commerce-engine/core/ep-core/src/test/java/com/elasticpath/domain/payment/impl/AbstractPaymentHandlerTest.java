/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.domain.payment.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.plugin.payment.PaymentType;
/**
 * 
 * test case to test the newly introduced methods, which refactor
 * the logic to decide if an amount is eligible for create a payment.
 *  
 *
 */

public class AbstractPaymentHandlerTest {

	private AbstractPaymentHandler paymentHandler; 
	
	/**
	 * set up the AbstractPayment Handler for test.
	 */
	@Before
	public void setUp() {
		paymentHandler = new AbstractPaymentHandler() {

			private static final long serialVersionUID = 50001L;

			@Override
			protected PaymentType getPaymentType() {
				return null;
			}
		};
		
	}
	
	/**
	 * Test amount greater than Zero is valid for payment.
	 */
	@Test
	public void testAmountGreaterThanZeroValidForPayment() {
	
		BigDecimal amount = BigDecimal.ONE;
		assertTrue(paymentHandler.isAmountEligibleForAuthorize(amount));
		assertTrue(paymentHandler.isAmountEligibleForCapture(amount));
		assertTrue(paymentHandler.isAmountEligibleForPayment(amount));
		
	}
	
	/**
	 * Test amount equal Zero is not valid for payment.
	 */
	@Test
	public void testAmountEqualZeroInvalidForPayment() {

		BigDecimal amount = BigDecimal.ZERO;
		assertFalse(paymentHandler.isAmountEligibleForAuthorize(amount));
		assertFalse(paymentHandler.isAmountEligibleForCapture(amount));
		assertFalse(paymentHandler.isAmountEligibleForPayment(amount));

	}
	
	/**
	 * Test amount less than zero is not valid for payment.
	 */
	@Test
	public void testAmountLessThanZeroInvalidForPayment() {
	
		BigDecimal amount = BigDecimal.valueOf((long) -1);
		assertFalse(paymentHandler.isAmountEligibleForAuthorize(amount));
		assertFalse(paymentHandler.isAmountEligibleForCapture(amount));
		assertFalse(paymentHandler.isAmountEligibleForPayment(amount));
		
	}
	
}

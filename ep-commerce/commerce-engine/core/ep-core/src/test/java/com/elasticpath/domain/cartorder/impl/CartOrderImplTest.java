/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.domain.cartorder.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.PaymentToken;
import com.elasticpath.domain.customer.impl.AbstractPaymentMethodImpl;
import com.elasticpath.domain.customer.impl.PaymentTokenImpl;
import com.elasticpath.plugin.payment.dto.PaymentMethod;


/**
 * Test for {@link CartOrderImpl}.
 *
 */

public class CartOrderImplTest {
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	private CartOrder cartOrder;
	
	/**
	 * Sets the up object under test.
	 */
	@Before
	public void setUpObjectUnderTest() {
		cartOrder = new CartOrderImpl();
	}
	
	/**
	 * Ensure use payment method throws exception on null.
	 */
	@Test
	public void ensureUsePaymentMethodThrowsExceptionOnNull() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("not be null");
		cartOrder.usePaymentMethod(null);
	}
	
	/**
	 * Ensure use payment method throws exception on incorrect type.
	 */
	@Test
	public void ensureUsePaymentMethodThrowsExceptionOnIncorrectType() {
		PaymentMethod paymentMethod = new PaymentMethod() {
		};
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("must be a subclass");
		exception.expectMessage(AbstractPaymentMethodImpl.class.getSimpleName());
		cartOrder.usePaymentMethod(paymentMethod);
	}
	
	/**
	 * Ensure use payment method copies payment method.
	 */
	@Test
	public void ensureUsePaymentMethodCopiesPaymentMethod() {
		PaymentToken paymentMethod = new PaymentTokenImpl.TokenBuilder()
				.withValue("value")
				.withDisplayValue("displayValue")
				.withGatewayGuid("gatewayGuid")
				.build();
		
		cartOrder.usePaymentMethod(paymentMethod);
		PaymentToken tokenFromCartOrder = (PaymentToken) cartOrder.getPaymentMethod();
		assertNotSame("Object reference should not be the same.", (Object) paymentMethod, (Object) tokenFromCartOrder);
		assertEquals("Token values should match.", paymentMethod.getValue(), tokenFromCartOrder.getValue());
		assertEquals("Token display values should match.", paymentMethod.getDisplayValue(), tokenFromCartOrder.getDisplayValue());
		assertEquals("Token gateway guids should match.", paymentMethod.getGatewayGuid(), tokenFromCartOrder.getGatewayGuid());
		assertEquals("UIDPK should be zero..", 0L, tokenFromCartOrder.getUidPk());
	}
}

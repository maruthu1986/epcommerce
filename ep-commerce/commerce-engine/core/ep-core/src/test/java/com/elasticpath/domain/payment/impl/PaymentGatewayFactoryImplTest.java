/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.domain.payment.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.plugin.payment.PaymentGatewayPlugin;
import com.elasticpath.plugin.payment.PaymentGatewayType;
import com.elasticpath.service.payment.gateway.impl.UnresolvablePaymentGatewayPluginImpl;

/**
 * Tests for {@link PaymentGatewayFactoryImpl}.
 */
public class PaymentGatewayFactoryImplTest {
	@Rule
	public JUnitRuleMockery context = new JUnitRuleMockery();

	@Test(expected = NullPointerException.class)
	public void testNullInvokerThrowsException() throws Exception {
		PaymentGatewayFactoryImpl factory = new PaymentGatewayFactoryImplStub(null);
		factory.getPaymentGatewayTypeForPlugin("pluginType");
	}

	@Test
	public void testInvokersPaymentTypeReturned() throws Exception {
		final PaymentGatewayPlugin mockInvoker = context.mock(PaymentGatewayPlugin.class);
		context.checking(new Expectations() {
			{
				atLeast(1).of(mockInvoker).getPaymentGatewayType();
				will(returnValue(PaymentGatewayType.CREDITCARD));
			}
		});

		PaymentGatewayFactoryImpl factory = new PaymentGatewayFactoryImplStub(mockInvoker);
		assertEquals("Factory should return the same PaymentGatewayType that the invoker returns",
				PaymentGatewayType.CREDITCARD, factory.getPaymentGatewayTypeForPlugin("pluginType"));
	}

	@Test
	public void ensureUnresolvablePaymentGatewayPluginIsReturnedWhenConfiguredGatewayNotFound() {
		PaymentGatewayFactoryImpl factory = new PaymentGatewayFactoryImpl();

		PaymentGatewayPlugin paymentGatewayPlugin = factory.createUnconfiguredPluginGatewayPlugin("nonExistentType");

		assertTrue("The gateway returned should be of type unresolvable", paymentGatewayPlugin instanceof UnresolvablePaymentGatewayPluginImpl);
	}

	private static final class PaymentGatewayFactoryImplStub extends PaymentGatewayFactoryImpl {

		private final PaymentGatewayPlugin invoker;

		PaymentGatewayFactoryImplStub(final PaymentGatewayPlugin invoker) {
			this.invoker = invoker;
		}

		@Override
		public PaymentGatewayPlugin createUnconfiguredPluginGatewayPlugin(final String pluginType) {
			return invoker;
		}
	}
}

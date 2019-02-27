/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.test.util.mock;

import java.util.Collections;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.factory.TestPaymentGatewayPluginFactoryImpl;
import com.elasticpath.plugin.payment.PaymentGatewayPlugin;
import com.elasticpath.plugin.payment.PaymentGatewayType;
import com.elasticpath.plugin.payment.capabilities.CaptureCapability;
import com.elasticpath.plugin.payment.capabilities.CreditCardCapability;
import com.elasticpath.plugin.payment.capabilities.DirectPostAuthCapability;
import com.elasticpath.plugin.payment.capabilities.ExternalAuthCapability;
import com.elasticpath.plugin.payment.capabilities.FinalizeShipmentCapability;
import com.elasticpath.plugin.payment.capabilities.PreAuthorizeCapability;

/**
 * Helper to make it easier to create and manage expectations for mock payment gateway plugins.
 */
public class MockPaymentGatewayPluginHelper {
	private final PaymentGatewayPlugin mockPaymentGatewayPlugin;

	private final CreditCardCapability mockCreditCardCapability;

	private final PreAuthorizeCapability mockPreAuthorizeCapability;

	private final ExternalAuthCapability mockExternalAuthCapability;

	private final DirectPostAuthCapability mockDirectPostAuthCapability;

	private final CaptureCapability mockCaptureCapability;

	private final FinalizeShipmentCapability mockFinalizeShipmentCapability;

	public MockPaymentGatewayPluginHelper(final BeanFactory beanFactory, final JUnitRuleMockery context,
			final String pluginType, final PaymentGatewayType paymentGatewayType) {
		mockPaymentGatewayPlugin = context.mock(PaymentGatewayPlugin.class);
		mockCreditCardCapability = context.mock(CreditCardCapability.class);
		mockPreAuthorizeCapability = context.mock(PreAuthorizeCapability.class);
		mockExternalAuthCapability = context.mock(ExternalAuthCapability.class);
		mockDirectPostAuthCapability = context.mock(DirectPostAuthCapability.class);
		mockCaptureCapability = context.mock(CaptureCapability.class);
		mockFinalizeShipmentCapability = context.mock(FinalizeShipmentCapability.class);

		context.checking(new Expectations() {
			{
				allowing(mockPaymentGatewayPlugin).getPluginType();
					will(returnValue(pluginType));
				allowing(mockPaymentGatewayPlugin).getPaymentGatewayType();
					will(returnValue(paymentGatewayType));
				allowing(mockPaymentGatewayPlugin).getConfigurationParameters();
					will(returnValue(Collections.emptySet()));
				ignoring(mockCreditCardCapability).setValidateCvv2(with(any(Boolean.class)));
				allowing(mockPaymentGatewayPlugin).getCapability(with(CreditCardCapability.class));
					will(returnValue(mockCreditCardCapability));
				allowing(mockPaymentGatewayPlugin).getCapability(with(PreAuthorizeCapability.class));
					will(returnValue(mockPreAuthorizeCapability));
				allowing(mockPaymentGatewayPlugin).getCapability(with(ExternalAuthCapability.class));
					will(returnValue(mockExternalAuthCapability));
				allowing(mockPaymentGatewayPlugin).getCapability(with(DirectPostAuthCapability.class));
					will(returnValue(mockDirectPostAuthCapability));
				allowing(mockPaymentGatewayPlugin).getCapability(with(CaptureCapability.class));
					will(returnValue(mockCaptureCapability));
				allowing(mockPaymentGatewayPlugin).getCapability(with(FinalizeShipmentCapability.class));
					will(returnValue(mockFinalizeShipmentCapability));
			}
		});

		final TestPaymentGatewayPluginFactoryImpl paymentGatewayFactory = beanFactory.getBean(ContextIdNames.PAYMENT_GATEWAY_FACTORY);

		// Remove any existing plugin of this type from the factory
		CollectionUtils.filter(paymentGatewayFactory.getPaymentGatewayPlugins(), new Predicate() {
			@Override
			public boolean evaluate(Object o) {
				PaymentGatewayPlugin paymentGatewayPlugin = (PaymentGatewayPlugin) o;
				return !paymentGatewayPlugin.getPluginType().equals(pluginType);
			}
		});

		// Inject the mock payment gateway plugin into the factory
		paymentGatewayFactory.getPaymentGatewayPlugins().add(mockPaymentGatewayPlugin);
	}

	public PaymentGatewayPlugin getMockPaymentGatewayPlugin() {
		return mockPaymentGatewayPlugin;
	}

	public CreditCardCapability getMockCreditCardCapability() {
		return mockCreditCardCapability;
	}

	public PreAuthorizeCapability getMockPreAuthorizeCapability() {
		return mockPreAuthorizeCapability;
	}

	public ExternalAuthCapability getMockExternalAuthCapability() {
		return mockExternalAuthCapability;
	}

	public DirectPostAuthCapability getMockDirectPostAuthCapability() {
		return mockDirectPostAuthCapability;
	}

	public CaptureCapability getMockCaptureCapability() {
		return mockCaptureCapability;
	}

	public FinalizeShipmentCapability getMockFinalizeShipmentCapability() {
		return mockFinalizeShipmentCapability;
	}
}

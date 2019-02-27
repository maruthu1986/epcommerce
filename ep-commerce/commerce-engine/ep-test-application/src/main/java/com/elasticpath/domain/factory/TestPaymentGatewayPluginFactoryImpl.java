/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.domain.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.payment.PaymentGatewayFactory;
import com.elasticpath.domain.payment.PaymentGatewayProperty;
import com.elasticpath.plugin.payment.PaymentGatewayPlugin;
import com.elasticpath.plugin.payment.PaymentGatewayType;
import com.elasticpath.service.payment.gateway.impl.UnresolvablePaymentGatewayPluginImpl;

/**
 * Implementation of <code>PaymentGatewayFactory</code> that allows fake or mock payment gateway plugins to be injected.
 * This cannot be used as a production implementation because it does not support multiple payment gateways of
 * the same type with different configuration properties.
 */
public class TestPaymentGatewayPluginFactoryImpl implements PaymentGatewayFactory {

	private Set<PaymentGatewayPlugin> paymentGatewayPlugins;

	private BeanFactory beanFactory;

	/**
	 * Create an return a {@link com.elasticpath.domain.payment.PaymentGateway} with the specified plugin type.
	 *
	 * @param pluginType the plugin type
	 * @return a prototype of a payment gateway with the specified plugin type
	 */
	@Override
	public PaymentGateway getPaymentGateway(final String pluginType) {
		PaymentGateway paymentGateway = beanFactory.getBean(ContextIdNames.PAYMENT_GATEWAY);
		paymentGateway.setType(pluginType);
		return paymentGateway;
	}

	@Override
	public Map<String, Class<? extends PaymentGatewayPlugin>> getAvailableGatewayPlugins() {
		Map<String, Class<? extends PaymentGatewayPlugin>> results = new HashMap<>();
		for (PaymentGatewayPlugin paymentGatewayPlugin : paymentGatewayPlugins) {
			results.put(paymentGatewayPlugin.getPluginType(), paymentGatewayPlugin.getClass());
		}
		return results;
	}

	@Override
	public PaymentGatewayPlugin createConfiguredPaymentGatewayPlugin(final String pluginType,
			final Map<String, PaymentGatewayProperty> properties) {
		return createUnconfiguredPluginGatewayPlugin(pluginType);
	}

	@Override
	public PaymentGatewayPlugin createUnconfiguredPluginGatewayPlugin(final String pluginType) {
		for (PaymentGatewayPlugin paymentGatewayPlugin : paymentGatewayPlugins) {
			if (paymentGatewayPlugin.getPluginType().equals(pluginType)) {
				return paymentGatewayPlugin;
			}
		}
		return new UnresolvablePaymentGatewayPluginImpl(pluginType);
	}

	@Override
	public PaymentGatewayType getPaymentGatewayTypeForPlugin(final String pluginType) {
		return createUnconfiguredPluginGatewayPlugin(pluginType).getPaymentGatewayType();
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public void setPaymentGatewayPlugins(final Set<PaymentGatewayPlugin> paymentGatewayPlugins) {
		this.paymentGatewayPlugins = paymentGatewayPlugins;
	}

	public Set<PaymentGatewayPlugin> getPaymentGatewayPlugins() {
		return paymentGatewayPlugins;
	}
}

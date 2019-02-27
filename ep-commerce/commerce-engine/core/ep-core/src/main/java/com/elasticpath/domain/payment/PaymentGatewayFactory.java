/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.domain.payment;

import java.util.Map;

import com.elasticpath.plugin.payment.PaymentGatewayPlugin;
import com.elasticpath.plugin.payment.PaymentGatewayType;

/**
 * The factory for handling <code>PaymentGateway</code>s.
 */
public interface PaymentGatewayFactory {

	/**
	 * Create an return a {@link PaymentGateway} with the specified plugin type.
	 *
	 * @param pluginType the plugin type
	 * @return a prototype of a payment gateway with the specified plugin type
	 */
	PaymentGateway getPaymentGateway(String pluginType);

	/**
	 * Create an unconfigured instance of the specified gateway plugin.
	 * 
	 * @param pluginType the plugin type
	 * @return the payment gateway plugin
	 */
	PaymentGatewayPlugin createUnconfiguredPluginGatewayPlugin(String pluginType);

	/**
	 * Gets the available gateway plugin classes.
	 * 
	 * @return the available gateway plugin classes
	 */
	Map<String, Class<? extends PaymentGatewayPlugin>> getAvailableGatewayPlugins();

	/**
	 * Create a configured instance of the specified gateway plugin configured with the given properties.
	 * 
	 * @param pluginType the plugin type
	 * @param properties the properties to use when configuring the plugin
	 * @return the configured payment gateway plugin
	 */
	PaymentGatewayPlugin createConfiguredPaymentGatewayPlugin(String pluginType, Map<String, PaymentGatewayProperty> properties);

	/**
	 * Returns the {@link PaymentGatewayType} supported by a specific plugin.
	 * @param pluginType the plugin type
	 * @return the supported {@link PaymentGatewayType}
	 */
	PaymentGatewayType getPaymentGatewayTypeForPlugin(String pluginType);

}

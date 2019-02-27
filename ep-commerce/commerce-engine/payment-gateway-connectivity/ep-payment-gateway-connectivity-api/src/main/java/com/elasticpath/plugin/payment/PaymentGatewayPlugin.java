/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.plugin.payment;

import java.util.Collection;
import java.util.Map;

import com.elasticpath.plugin.payment.capabilities.PaymentGatewayCapability;

/**
 * API for setting up an unconfigured payment gateway plugin.
 */
public interface PaymentGatewayPlugin {
	/**
	 * Provides access to payment gateway capabilities not exposed through the base API.
	 * @param capability the requested capability
	 * @param <T> a class or interface implementing {@link PaymentGatewayCapability}
	 * @return the capability requested, if available on this instance. Null otherwise.
	 */
	<T extends PaymentGatewayCapability> T getCapability(Class<T> capability);

	/**
	 * Get the type of this payment gateway - a discriminator to other plugin classes.
	 *
	 * @return the discriminator value of this gateway
	 */
	String getPluginType();

	/**
	 * Get the method type of this payment gateway. e.g. CREDIT CARD, Paypal, Debit ...
	 *
	 * @return the payment gateway type
	 */
	PaymentGatewayType getPaymentGatewayType();

	/**
	 * Determines if this {@link PaymentGatewayPlugin} is resolved.
	 *
	 * @return boolean value representing whether the plugin is resolved.
	 */
	boolean isResolved();
	/**
	 * Sets the configurations of the payment gateway via map.
	 *
	 * @param configurations a map of configuration keys to values
	 */
	void setConfigurationValues(Map<String, String> configurations);

	/**
	 * Gets the configuration parameter names.
	 *
	 * @return the configuration parameters
	 */
	Collection<String> getConfigurationParameters();

	/**
	 * Sets the certificate path prefix.
	 *
	 * @param certificatePathPrefix the certificate path prefix
	 */
	void setCertificatePathPrefix(String certificatePathPrefix);
}

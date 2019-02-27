/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.domain.payment.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.payment.PaymentGatewayFactory;
import com.elasticpath.domain.payment.PaymentGatewayProperty;
import com.elasticpath.plugin.payment.PaymentGatewayPlugin;
import com.elasticpath.plugin.payment.PaymentGatewayType;
import com.elasticpath.service.payment.GiftCertificateTransactionService;
import com.elasticpath.service.payment.gateway.impl.GiftCertificatePaymentGatewayPluginImpl;
import com.elasticpath.service.payment.gateway.impl.UnresolvablePaymentGatewayPluginImpl;
import com.elasticpath.settings.provider.SettingValueProvider;

/**
 * Default implementation of <code>PaymentGatewayFactory</code>.
 */
public class PaymentGatewayFactoryImpl implements PaymentGatewayFactory {

	private static final Logger LOG = Logger.getLogger(PaymentGatewayFactoryImpl.class);
	
	// plugin type -> class of PaymentGatewayPlugin
	private final Map<String, Class<? extends PaymentGatewayPlugin>> gatewayClassesByType =
		new HashMap<>();

	private BeanFactory beanFactory;
	
	private SettingValueProvider<String> certificatePathPrefixProvider;

	private GiftCertificateTransactionService giftCertificateTransactionService;

	/**
	 * Create an return a {@link PaymentGateway} with the specified plugin type.
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
		return gatewayClassesByType;
	}

	@Override
	public PaymentGatewayPlugin createConfiguredPaymentGatewayPlugin(final String pluginType,
			final Map<String, PaymentGatewayProperty> properties) {

		PaymentGatewayPlugin paymentGatewayPlugin = createUnconfiguredPluginGatewayPlugin(pluginType);
		if (paymentGatewayPlugin == null) {
			throw new EpSystemException("Unable to find payment gateway plugin of pluginType " + pluginType);
		}
		
		// Inject the certificate path prefix
		paymentGatewayPlugin.setCertificatePathPrefix(getCertificatePathPrefix());
		
		// Inject configuration settings
		Map<String, String> configurations = new HashMap<>();
		for (Entry<String, PaymentGatewayProperty> entry : properties.entrySet()) {
			configurations.put(entry.getValue().getKey(), entry.getValue().getValue());
		}
		paymentGatewayPlugin.setConfigurationValues(configurations);

		return paymentGatewayPlugin;
	}

	@Override
	public PaymentGatewayPlugin createUnconfiguredPluginGatewayPlugin(final String pluginType) {
		if (getAvailableGatewayPlugins().get(pluginType) == null) {
			LOG.warn("No payment gateway plugin found for type: " + pluginType);
			return new UnresolvablePaymentGatewayPluginImpl(pluginType);
		} else {
			return createInstance(getAvailableGatewayPlugins().get(pluginType));
		}
	}

	@Override
	public PaymentGatewayType getPaymentGatewayTypeForPlugin(final String pluginType) {
		return createUnconfiguredPluginGatewayPlugin(pluginType).getPaymentGatewayType();
	}

	private PaymentGatewayPlugin createInstance(final Class<? extends PaymentGatewayPlugin> clazz) {
		try {
			PaymentGatewayPlugin plugin = clazz.newInstance();
			if (plugin instanceof GiftCertificatePaymentGatewayPluginImpl) {
				((GiftCertificatePaymentGatewayPluginImpl) plugin).setGiftCertificateTransactionService(giftCertificateTransactionService);
			}
			return plugin;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new EpSystemException("Failed to create payment gateway plugin :" + clazz, e);
		}
	}

	/**
	 * Setter for injecting the gateway classes through Spring.
	 * @param gatewayClasses set of gateway class names
	 */
	public void setGatewayClasses(final Set<Class<? extends PaymentGatewayPlugin>> gatewayClasses) {
		for (Class<? extends PaymentGatewayPlugin> gatewayClass : gatewayClasses) {
				PaymentGatewayPlugin gatewayInstance = createInstance(gatewayClass);
				gatewayClassesByType.put(gatewayInstance.getPluginType(), gatewayClass);
		}
	}

	/**
	 * Gets the path prefix for the payment gateway certificate files.
	 * 
	 * @return the path prefix
	 */
	protected String getCertificatePathPrefix() {
		final String path = getCertificatePathPrefixProvider().get();
		LOG.debug("Certificate path prefix: " + path);
		return path;
	}

	/**
	 * Get the gift certificate transaction service.
	 * 
	 * @return the gift certificate transaction service
	 */
	protected GiftCertificateTransactionService getGiftCertificateTransactionService() {
		return giftCertificateTransactionService;
	}

	public void setGiftCertificateTransactionService(final GiftCertificateTransactionService giftCertificateTransactionService) {
		this.giftCertificateTransactionService = giftCertificateTransactionService;
	}

	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	protected SettingValueProvider<String> getCertificatePathPrefixProvider() {
		return certificatePathPrefixProvider;
	}

	public void setCertificatePathPrefixProvider(final SettingValueProvider<String> certificatePathPrefixProvider) {
		this.certificatePathPrefixProvider = certificatePathPrefixProvider;
	}

}

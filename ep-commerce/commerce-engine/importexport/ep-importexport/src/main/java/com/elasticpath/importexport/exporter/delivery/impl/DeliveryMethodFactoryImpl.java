/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.importexport.exporter.delivery.impl;

import java.util.Map;

import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.TransportType;
import com.elasticpath.importexport.exporter.configuration.DeliveryConfiguration;
import com.elasticpath.importexport.exporter.delivery.DeliveryMethod;
import com.elasticpath.importexport.exporter.delivery.DeliveryMethodFactory;

/**
 * DeliveryMethodFactory creates Delivery Methods by delivery configuration.<br>
 * Map of available delivery methods is initialized by Spring.
 */
public class DeliveryMethodFactoryImpl implements DeliveryMethodFactory {

	private Map<TransportType, DeliveryMethod> deliveryMethods;

	@Override
	public DeliveryMethod createDeliveryMethod(final DeliveryConfiguration deliveryConfiguration) throws ConfigurationException {
		final TransportType methodType = deliveryConfiguration.getMethod();
		final DeliveryMethod deliveryMethod = deliveryMethods.get(methodType);

		if (deliveryMethod == null) {
			throw new ConfigurationException("Delivery method of type " + methodType + " doesn't exist");
		}

		deliveryMethod.initialize(deliveryConfiguration.getTarget());
		return deliveryMethod;
	}

	/**
	 * Gets available delivery methods.
	 * 
	 * @return the map of available delivery methods
	 */
	public Map<TransportType, DeliveryMethod> getDeliveryMethods() {
		return deliveryMethods;
	}

	/**
	 * Sets available delivery methods.
	 * 
	 * @param deliveryMethods the map of delivery methods
	 */
	public void setDeliveryMethods(final Map<TransportType, DeliveryMethod> deliveryMethods) {
		this.deliveryMethods = deliveryMethods;
	}
}

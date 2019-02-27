/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.importexport.exporter.delivery;

import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.exporter.configuration.DeliveryConfiguration;

/**
 * Delivery Method Factory produces ready to use Delivery Methods.
 */
public interface DeliveryMethodFactory {

	/**
	 * Creates and initialize delivery method using configuration.
	 *
	 * @param deliveryConfiguration containing parameters to initialize delivery method with
	 * @throws ConfigurationException if delivery method couldn't be configured
	 * @return DeliveryMethod instance
	 */
	DeliveryMethod createDeliveryMethod(DeliveryConfiguration deliveryConfiguration) throws ConfigurationException;
}

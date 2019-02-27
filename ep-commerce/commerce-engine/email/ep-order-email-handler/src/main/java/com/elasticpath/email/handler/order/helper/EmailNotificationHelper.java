/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.email.handler.order.helper;
import com.elasticpath.email.domain.EmailProperties;

/**
 * EmailNotificationHelper interface. 
 * EmailNotificationHelper a notification helper class to encapsulate the dependencies and functionality
 * required to send an email.
 */
public interface EmailNotificationHelper {
	
	/**
	 * Get email properties for a confirmation email.
	 * @param orderNumber is the order number
	 * @return email properties for a confirmation email
	 */
	EmailProperties getOrderEmailProperties(String orderNumber);
	
	/**
	 * Get email properties for order shipped email.
	 * @param orderNumber is the order number
	 * @param shipmentNumber is the shipment number
	 * @return whether the email was sent successfully
	 */
	EmailProperties getShipmentConfirmationEmailProperties(String orderNumber, String shipmentNumber);
		
}

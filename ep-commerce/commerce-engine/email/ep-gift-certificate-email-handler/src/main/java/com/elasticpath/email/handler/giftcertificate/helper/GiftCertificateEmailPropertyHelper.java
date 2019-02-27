/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.email.handler.giftcertificate.helper;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.email.domain.EmailProperties;

/**
 * Helper for constructing email properties.
 */
public interface GiftCertificateEmailPropertyHelper {

	/**
	 * Gets the {@link EmailProperties} for Gift Certificates confirmation e-mails.
	 * 
	 * @param order the order
	 * @param orderSku the orderSku of the purchased {@code GiftCertificate}
	 * @param giftCertificateThemeImageFilename the filename of the image to be used for the given gift certificate
	 * @return {@link EmailProperties}
	 */
	EmailProperties getEmailProperties(Order order, OrderSku orderSku, String giftCertificateThemeImageFilename);

}

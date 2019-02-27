/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.plugin.payment.capabilities;

import java.util.Map;

import com.elasticpath.plugin.payment.dto.PaymentOptionFormDescriptor;

/**
 * This interface indicates that the payment gateway supports hosted-payment-pages authentication capability.
 */
public interface HostedPageAuthCapability extends ExternalAuthCapability {
	
	/**
	 * Prepares for redirecting to the real payment gateway hosted order page.
	 * 
	 * @param responseMap the responseMap
	 * @return the URL what the payment gateway needs to redirect to along with any session values that need to be stored
	 */
	PaymentOptionFormDescriptor prepareForRedirect(Map<String, String> responseMap);
}

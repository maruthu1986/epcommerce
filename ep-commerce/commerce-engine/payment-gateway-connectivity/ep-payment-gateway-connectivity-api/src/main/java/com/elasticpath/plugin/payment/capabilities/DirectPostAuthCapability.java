/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.plugin.payment.capabilities;

/**
 * This interface indicates that the payment gateway supports direct post authentication capability.
 */
public interface DirectPostAuthCapability extends ExternalAuthCapability {
	/**
	 * Return the internal code for a specific card type.
	 * @param cardType the card type name
	 * @return the internal payment gateway code for the card type
	 */
	String getCardTypeInternalCode(String cardType);
}

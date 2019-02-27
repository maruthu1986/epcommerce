/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.domain.payment.impl;

import com.elasticpath.plugin.payment.PaymentType;

/**
 * Direct Post Paypal express handler.
 */
public class DirectPostPayPalExpressPaymentHandler extends AbstractPaymentHandler {

	/** Serial version id. */
	public static final long serialVersionUID = 5000000001L;

	@Override
	protected PaymentType getPaymentType() {	
		return PaymentType.HOSTED_PAGE;
	}
}

/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.rel;

import com.elasticpath.rest.resource.commons.paymentmethod.rel.PaymentMethodCommonsConstants;

/**
 * Payment info rel names.
 */
public final class PaymentMethodRels {

	/**
	 * The payment information REL.
	 */
	public static final String PAYMENTMETHODS_REL = "paymentmethods";

	/**
	 * The payment information REV.
	 */
	public static final String PAYMENTMETHODS_REV = PAYMENTMETHODS_REL;

	/**
	 * The payment method rel.
	 */
	public static final String PAYMENTMETHOD_REL = "paymentmethod";

	/**
	 * The payment method info rel.
	 */
	public static final String PAYMENTMETHODINFO_REL = "paymentmethodinfo";

	/**
	 * The payment method info rev.
	 */
	public static final String PAYMENTMETHODINFO_REV = PAYMENTMETHODINFO_REL;

	/**
	 * The order REV.
	 */
	public static final String ORDER_REV = PaymentMethodCommonsConstants.ORDER_REL;

	/**
	 * The profile REL.
	 */
	public static final String PROFILE_REL = "profile";

	/**
	 * The profile REV.
	 */
	public static final String PROFILE_REV = PROFILE_REL;

	/**
	 * The default payment information REL.
	 */
	public static final String DEFAULT_REL = "default";
}

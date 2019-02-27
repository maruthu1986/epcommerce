/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment;



/**
 * Property tester that indicates whether certain features are enabled.
 */
public class FulfillmentFeatureEnablementPropertyTester  {


	/** Whether to enable creation of orders through the CM Client. */
	public static final boolean ENABLE_CREATE_ORDER = false;

	/** Property for checking create order enablement. */
	public static final String PROPERTY_ENABLE_CREATE_ORDER = "enableCreateOrder"; //$NON-NLS-1$

	/**
	 *
	 * @param receiver receiver
	 * @param property property
	 * @param args args
	 * @param expectedValue expectedValue
	 * @return boolean
	 */
	public boolean test(final Object receiver, final String property, final Object[] args, final Object expectedValue) {
		return PROPERTY_ENABLE_CREATE_ORDER.equals(property) && ENABLE_CREATE_ORDER;
	}
}

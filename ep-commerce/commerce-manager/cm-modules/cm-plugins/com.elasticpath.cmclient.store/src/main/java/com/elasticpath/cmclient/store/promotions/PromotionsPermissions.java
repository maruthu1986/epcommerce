/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.store.promotions;

/**
 * Defines the permissions for promotions.
 */
public class PromotionsPermissions {

	/**
	 * Permission to change/manage promotions, in addition to the ability to create/detele
	 * promotions.
	 */
	public static final String PROMOTION_MANAGE = "PROMOTION_MANAGE"; //$NON-NLS-1$

	/**
	 * Permission to change/manage coupons. Required for coupon management.
	 * Can be added on top of {@link #PROMOTION_MANAGE} to give full control.
	 */
	public static final String COUPONS_MANAGE = "COUPONS_MANAGE"; //$NON-NLS-1$
}

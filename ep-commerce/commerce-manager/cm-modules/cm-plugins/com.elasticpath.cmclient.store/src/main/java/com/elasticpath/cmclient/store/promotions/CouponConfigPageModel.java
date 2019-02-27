/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.store.promotions;

import com.elasticpath.cmclient.core.promotions.CouponCollectionModel;
import com.elasticpath.domain.rules.CouponConfig;

/**
 * Model for CouponConfigurationPage.
 */
public class CouponConfigPageModel {
	private final CouponConfig couponConfig;
	
	private final CouponCollectionModel couponUsageCollectionModel;
	
	/**
	 * Normal constructor.
	 * @param couponConfig The coupon config.
	 * @param couponCollectionModel The coupon collection model.
	 */
	public CouponConfigPageModel(final CouponConfig couponConfig, final CouponCollectionModel couponCollectionModel) {
		this.couponConfig = couponConfig;
		this.couponUsageCollectionModel = couponCollectionModel;
	}
	
	/**
	 * 
	 * @return The coupon config
	 */
	public CouponConfig getCouponConfig() {
		return couponConfig;
	}
	
	/**
	 * @return the CouponCollectionModel 
	 */
	public CouponCollectionModel getCouponUsageCollectionModel() {
		return couponUsageCollectionModel;
	}
}

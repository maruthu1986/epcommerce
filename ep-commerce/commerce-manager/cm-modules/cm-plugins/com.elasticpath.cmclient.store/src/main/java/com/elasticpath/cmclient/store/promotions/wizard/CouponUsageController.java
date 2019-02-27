/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.promotions.wizard;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.promotions.CouponCollectionModel;
import com.elasticpath.common.dto.CouponModelDto;
import com.elasticpath.common.dto.CouponUsageModelDto;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.CouponUsage;
import com.elasticpath.service.rules.CouponService;
import com.elasticpath.service.rules.CouponUsageService;

/**
 * Updates the database with the {@code CouponUsage} changes.
 */
public class CouponUsageController {

	private final Map<String, Coupon> couponMap = new HashMap<>();

	/**
	 * Updates the database with the contents of {@code model}.
	 * 
	 * @param model The model to use.
	 */
	public void updateDatabase(final CouponCollectionModel model) {
		for (CouponModelDto couponUsageModel : model.getObjectsToAdd()) {
			Coupon addedCoupon = getPersistedCoupon(couponUsageModel.getCouponCode(), model.getCouponConfig());

			// If emailAddress is null then this is a model for a coupon with no couponUsage
			if (couponUsageModel instanceof CouponUsageModelDto) {
				CouponUsage newCouponUsage = getNewCouponUsage();
				newCouponUsage.setCoupon(addedCoupon);
				newCouponUsage.setCustomerEmailAddress(((CouponUsageModelDto) couponUsageModel).getEmailAddress());
				newCouponUsage.setUseCount(0);
				newCouponUsage.setActiveInCart(true);
				getCouponUsageService().add(newCouponUsage);
			}
		}
	}

	private Coupon getPersistedCoupon(final String couponCode, final CouponConfig couponConfig) {
		
		Coupon persistedCoupon = couponMap.get(couponCode);
		if (persistedCoupon == null) {
			Coupon newCoupon = getNewCoupon();
			newCoupon.setCouponCode(couponCode);
			newCoupon.setCouponConfig(couponConfig);
			persistedCoupon = getCouponService().add(newCoupon);
			couponMap.put(couponCode, persistedCoupon);
		}
		
		return persistedCoupon;
	}

	/**
	 * 
	 * @return A new coupon - extracted for unit testing.
	 */
	Coupon getNewCoupon() {
		return ServiceLocator.getService(ContextIdNames.COUPON);
	}
	
	/**
	 * 
	 * @return A new coupon usage - extracted for unit testing.
	 */
	CouponUsage getNewCouponUsage() {
		return ServiceLocator.getService(ContextIdNames.COUPON_USAGE);
	}
	
	/**
	 * Get Coupon Service.
	 * 
	 * @return the coupon service
	 */
	public CouponService getCouponService() {
		return ServiceLocator.getService(ContextIdNames.COUPON_SERVICE);
	}
	
	/**
	 * Get coupon usage service.
	 *  
	 * @return the coupon usage service 
	 */
	public CouponUsageService getCouponUsageService() {
		return ServiceLocator.getService(ContextIdNames.COUPON_USAGE_SERVICE);
	}

}

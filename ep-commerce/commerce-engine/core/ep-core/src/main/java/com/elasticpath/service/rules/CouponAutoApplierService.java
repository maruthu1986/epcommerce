/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.rules;

import java.util.Set;

import com.elasticpath.domain.store.Store;

/**
 * Service methods to handle Coupon Auto Application.
 */
public interface CouponAutoApplierService {
	

	/**
	 * Get coupons to remove during coupon auto application.
	 *
	 * @param existingCoupons existing coupons.
	 * @param store store
	 * @param customerEmailAddress email
	 * @return list of coupon codes to remove from existing coupons.
	 */
	Set<String> filterValidCouponsForCustomer(Set<String> existingCoupons, Store store, String customerEmailAddress);
	
	/**
	 * Get coupons to auto apply.
	 * @param store the store.
	 * @param customerEmailAddress customer email address
	 *
	 * @return list of coupon codes to auto apply.
	 */
	Set<String> retrieveCouponsApplicableToAutoApply(Store store, String customerEmailAddress);

}

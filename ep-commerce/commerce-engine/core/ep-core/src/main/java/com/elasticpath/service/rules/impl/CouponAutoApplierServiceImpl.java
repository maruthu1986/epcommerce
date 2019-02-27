/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.rules.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.domain.coupon.specifications.PotentialCouponUse;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.CouponUsage;
import com.elasticpath.domain.rules.CouponUsageType;
import com.elasticpath.domain.specifications.Specification;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.rules.CouponAutoApplierService;
import com.elasticpath.service.rules.CouponService;
import com.elasticpath.service.rules.CouponUsageService;

/**
 * Service methods for auto applying coupons.
 */
public class CouponAutoApplierServiceImpl implements CouponAutoApplierService {

	private CouponService couponService;

	private CouponUsageService couponUsageService;

	private Specification<PotentialCouponUse> validCouponUseSpecification;

	@Override
	public Set<String> filterValidCouponsForCustomer(final Set<String> existingCoupons, final Store store,
			final String customerEmailAddress) {
		Set<String> userSpecificCoupons = getUserSpecificCoupons(existingCoupons);
		Set<String> invalidCoupons = getInvalidCoupons(existingCoupons, store.getCode(), customerEmailAddress);

		Set<String> toRemove = new HashSet<>();
		toRemove.addAll(userSpecificCoupons);
		toRemove.addAll(invalidCoupons);
		return toRemove;
	}

	@Override
	public Set<String> retrieveCouponsApplicableToAutoApply(final Store store, final String customerEmailAddress) {
		return getValidCustomerEligibleCouponCodes(store, customerEmailAddress);
	}

	/**
	 * Get valid customer email specific coupon codes.
	 *
	 * @param store store
	 * @param customerEmailAddress customer address, or null if none.
	 * @return List of coupon codes corresponding to valid customer email specific coupons.
	 */
	protected Set<String> getValidCustomerEligibleCouponCodes(final Store store, final String customerEmailAddress) {
		// user specific coupons are only applicable if the user has an email address
		Set<String> userSpecificCoupons = new HashSet<>();
		if (!StringUtils.isEmpty(customerEmailAddress)) {
			final Collection<CouponUsage> eligibleUsages =
					couponUsageService.findEligibleUsagesByEmailAddress(customerEmailAddress, store.getUidPk());
			for (CouponUsage usage : eligibleUsages) {
				final String couponCode = usage.getCoupon().getCouponCode();
				Coupon coupon = couponService.findByCouponCode(couponCode);
				PotentialCouponUse potentialCouponUse = new PotentialCouponUse(coupon, store.getCode(), customerEmailAddress);
				if (usage.isActiveInCart() && getValidCouponUseSpecification().isSatisfiedBy(potentialCouponUse).isSuccess()) {
					userSpecificCoupons.add(couponCode);
				}
			}
		}
		return userSpecificCoupons;
	}

	/**
	 * Return coupons codes for coupons which are invalid in the list of coupons.
	 *
	 * @param coupons coupons to filter invalid coupons out of.
	 * @param storeCode store code
	 * @param customerEmailAddress customer address, or null if none
	 * @return coupons which are invalid in the list of coupons.
	 */
	protected Set<String> getInvalidCoupons(final Set<String> coupons, final String storeCode, final String customerEmailAddress) {
		final Set<String> couponsToRemove = new HashSet<>();
		for (String couponCode : coupons) {
			Coupon coupon = couponService.findByCouponCode(couponCode);
			PotentialCouponUse potentialCouponUse = new PotentialCouponUse(coupon, storeCode, customerEmailAddress);
			if (!getValidCouponUseSpecification().isSatisfiedBy(potentialCouponUse).isSuccess()) {
				couponsToRemove.add(couponCode);
			}
		}
		return couponsToRemove;
	}

	/**
	 * Return coupon codes for coupons which are user specific.
	 *
	 * @param coupons Coupons to filter through.
	 * @return coupons in the list of coupons passed in which are user specific.
	 */
	protected Set<String> getUserSpecificCoupons(final Set<String> coupons) {
		final Set<String> toRemove = new HashSet<>();
		for (String couponCode : coupons) {
			Coupon coupon = couponService.findByCouponCode(couponCode);
			if (coupon != null && isUserSpecificCoupon(coupon)) {
				toRemove.add(couponCode);
			}
		}
		return toRemove;
	}

	private boolean isUserSpecificCoupon(final Coupon coupon) {
		return CouponUsageType.LIMIT_PER_SPECIFIED_USER.equals(coupon.getCouponConfig().getUsageType());
	}

	public CouponService getCouponService() {
		return couponService;
	}

	public void setCouponService(final CouponService couponService) {
		this.couponService = couponService;
	}

	public CouponUsageService getCouponUsageService() {
		return couponUsageService;
	}

	public void setCouponUsageService(final CouponUsageService couponUsageService) {
		this.couponUsageService = couponUsageService;
	}

	public Specification<PotentialCouponUse> getValidCouponUseSpecification() {
		return validCouponUseSpecification;
	}

	public void setValidCouponUseSpecification(final Specification<PotentialCouponUse> validCouponUseSpecification) {
		this.validCouponUseSpecification = validCouponUseSpecification;
	}

}
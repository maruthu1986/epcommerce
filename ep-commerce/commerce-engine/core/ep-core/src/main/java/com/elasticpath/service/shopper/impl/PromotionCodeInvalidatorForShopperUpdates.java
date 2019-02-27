/*
 * Copyright (c) Elastic Path Software Inc., 2011
 */

package com.elasticpath.service.shopper.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.CouponUsageType;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.customer.CustomerSessionShopperUpdateHandler;
import com.elasticpath.service.rules.CouponService;

/**
 * Invalidates the ShoppingCart promotions when the Shopper changes.
 */
public class PromotionCodeInvalidatorForShopperUpdates implements CustomerSessionShopperUpdateHandler {

	private final CouponService couponService;

	/**
	 * Alternate constructor.
	 * @param couponService the couponService
	 */
	public PromotionCodeInvalidatorForShopperUpdates(final CouponService couponService) {
		this.couponService = couponService;
	}

	/**
	 * Removes promotions from the ShoppingCart when there is a change in the Shopper.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void invalidateShopper(final CustomerSession customerSession, final Shopper invalidShopper) {

		final Shopper currentShopper = customerSession.getShopper();

		// If the invalidated shopping context was for an anonymous session, do not change ANY of the promotions on
		// login because it might result in someone not converting due to the price increase. There cannot be any
		// user specific coupons applied to the anonymous sessions anyways.
		boolean wasAnonymousSession = isAnonymousShopper(invalidShopper);
		if (wasAnonymousSession || currentShopper.equals(invalidShopper)) {
			return;
		}

		final ShoppingCart shoppingCart = currentShopper.getCurrentShoppingCart();
		final Set<String> couponCodes = shoppingCart.getPromotionCodes();

		// Remove all coupons to ensure that we have cleared out any and all promotions that may be linked to the old CustomerSession.
		shoppingCart.removePromotionCodes(couponCodes);

		applyPublicCoupons(shoppingCart, couponCodes);

	}

	private void applyPublicCoupons(final ShoppingCart shoppingCart, final Collection<String> couponCodes) {
		final Map<String, Coupon> couponCodeToCoupon = couponService.findCouponsForCodes(couponCodes);
		final List<String> publicCoupons = new ArrayList<>();

		// If the coupon is a public coupon (ie. Applies to all CustomerSessions) then re-add it. The remove/add will then create a new
		// usage record linked to the correct/current user account.
		couponCodeToCoupon.values().stream().filter(coupon -> !CouponUsageType.LIMIT_PER_SPECIFIED_USER
				.equals(coupon.getCouponConfig().getUsageType()))
				.forEach(coupon -> publicCoupons.add(coupon.getCouponCode()));

		shoppingCart.applyPromotionCodes(publicCoupons);
	}

	private boolean isAnonymousShopper(final Shopper invalidShopper) {
		if (invalidShopper.getCustomer() == null) {
			return true;
		}

		final Customer customer = invalidShopper.getCustomer();
		return customer.isAnonymous();
	}

}

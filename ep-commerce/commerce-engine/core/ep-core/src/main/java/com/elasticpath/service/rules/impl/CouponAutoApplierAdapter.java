/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.rules.impl;

import java.util.Collection;
import java.util.Set;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.rules.CartOrderCouponAutoApplier;
import com.elasticpath.service.rules.CouponAutoApplierService;
import com.elasticpath.service.rules.ShoppingCartCouponAutoApplier;

/**
 * Adapt the coupon auto applier algorithm to different interfaces.
 */
public class CouponAutoApplierAdapter implements ShoppingCartCouponAutoApplier, CartOrderCouponAutoApplier {

	private CouponAutoApplierService couponAutoApplierService;

	/**
	 * Coupons interface to manage coupons for various implementations.
	 */
	private interface Coupons {
		/**
		 * Add a coupon code.
		 *
		 * @param couponCode coupon code to add.
		 */
		boolean addAll(Collection<String> couponCode);

		/**
		 * Remove a coupon code.
		 *
		 * @param couponCode coupon code to remove.
		 */
		boolean removeAll(Collection<String> couponCode);
		
		/**
		 * Get all coupons.
		 *
		 * @return coupons
		 */
		Set<String> getAll();
	}

	/**
	 * Manage shopping cart coupons.
	 */
	private static class ShoppingCartCoupons implements Coupons {
		private final ShoppingCart shoppingCart;

		ShoppingCartCoupons(final ShoppingCart shoppingCart) {
			this.shoppingCart = shoppingCart;
		}
		
		@Override
		public boolean addAll(final Collection<String> couponCodes) {
			return shoppingCart.applyPromotionCodes(couponCodes);
		}

		@Override
		public boolean removeAll(final Collection<String> couponCodes) {
			return shoppingCart.removePromotionCodes(couponCodes);
		}

		@Override
		public Set<String> getAll() {
			return shoppingCart.getPromotionCodes();
		}
	}

	/**
	 * Manage cart order coupons.
	 */
	private static class CartOrderCoupons implements Coupons {
		private final CartOrder cartOrder;

		CartOrderCoupons(final CartOrder cartOrder) {
			this.cartOrder = cartOrder;
		}

		@Override
		public boolean addAll(final Collection<String> couponCodes) {
			return cartOrder.addCoupons(couponCodes);
		}

		@Override
		public boolean removeAll(final Collection<String> couponCodes) {
			return cartOrder.removeCoupons(couponCodes);
		}

		@Override
		public Set<String> getAll() {
			return cartOrder.getCouponCodes();
		}
	}

	@Override
	public boolean filterAndAutoApplyCoupons(final CartOrder cartOrder, final Store store, final String customerEmailAddress) {
		return filterAndAutoApplyCoupons(new CartOrderCoupons(cartOrder), store, customerEmailAddress);
	}

	@Override
	public boolean filterAndAutoApplyCoupons(final ShoppingCart shoppingCart) {
		Store store = shoppingCart.getStore();
		String customerEmailAddress = shoppingCart.getShopper().getCustomer().getEmail();
		return filterAndAutoApplyCoupons(new ShoppingCartCoupons(shoppingCart), store, customerEmailAddress);
	}

	/**
	 * Filter and auto apply coupons.
	 *
	 * @param coupons coupons
	 * @param store store
	 * @param customerEmailAddress customer email address
	 * @return true if coupons are updated, false otherwise.
	 */
	protected boolean filterAndAutoApplyCoupons(final Coupons coupons, final Store store, final String customerEmailAddress) {
		final Set<String> couponCodesToApply = getCouponsToApply(store, customerEmailAddress);
		final Set<String> couponCodesToRemove = getCouponsToRemove(coupons.getAll(), store, customerEmailAddress);

		boolean areCouponsUpdated = coupons.removeAll(couponCodesToRemove);
		areCouponsUpdated |= coupons.addAll(couponCodesToApply);

		return areCouponsUpdated;
	}

	private Set<String> getCouponsToApply(final Store store, final String customerEmailAddress) {
		return couponAutoApplierService.retrieveCouponsApplicableToAutoApply(store, customerEmailAddress);
	}

	private Set<String> getCouponsToRemove(final Set<String> existingCoupons, final Store store, final String customerEmailAddress) {
		return couponAutoApplierService.filterValidCouponsForCustomer(existingCoupons, store, customerEmailAddress);
	}

	public CouponAutoApplierService getCouponAutoApplierService() {
		return couponAutoApplierService;
	}

	public void setCouponAutoApplierService(final CouponAutoApplierService couponAutoApplierService) {
		this.couponAutoApplierService = couponAutoApplierService;
	}

}

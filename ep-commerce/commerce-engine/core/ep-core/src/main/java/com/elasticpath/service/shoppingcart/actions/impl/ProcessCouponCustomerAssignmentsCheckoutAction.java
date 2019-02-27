/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.shoppingcart.actions.impl;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.shoppingcart.PromotionRecordContainer;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.service.rules.CouponUsageService;
import com.elasticpath.service.shoppingcart.actions.CheckoutActionContext;
import com.elasticpath.service.shoppingcart.actions.ReversibleCheckoutAction;

/**
 * CheckoutAction to assign coupons to customers who are entitled to receive a coupon as a result of a promotion.
 */
public class ProcessCouponCustomerAssignmentsCheckoutAction implements ReversibleCheckoutAction {

	private CouponUsageService couponUsageService;

	@Override
	public void execute(final CheckoutActionContext context) throws EpSystemException {
		ShoppingCartPricingSnapshot shoppingCartPricingSnapshot = context.getShoppingCartTaxSnapshot().getShoppingCartPricingSnapshot();
		final PromotionRecordContainer promotionRecordContainer = shoppingCartPricingSnapshot.getPromotionRecordContainer();
		couponUsageService.processCouponCustomerAssignments(promotionRecordContainer.getAppliedRules(), context.getCustomer().getEmail());
	}

	@Override
	public void rollback(final CheckoutActionContext context)
	throws EpSystemException {
		// NO OP
	}

	protected CouponUsageService getCouponUsageService() {
		return couponUsageService;
	}

	public void setCouponUsageService(final CouponUsageService couponUsageService) {
		this.couponUsageService = couponUsageService;
	}
}
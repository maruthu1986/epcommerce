/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.shoppingcart.actions.impl;

import java.util.ArrayList;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.shoppingcart.PromotionRecordContainer;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.rules.CouponUsageService;
import com.elasticpath.service.shoppingcart.actions.CheckoutActionContext;
import com.elasticpath.service.shoppingcart.actions.ReversibleCheckoutAction;

/**
 * CheckoutAction to update limited usage numbers on coupons and promotions.
 */
public class UpdateLimitedUsageNumbersCheckoutAction implements ReversibleCheckoutAction {

	private OrderService orderService;

	private CouponUsageService couponUsageService; 

	@Override
	public void execute(final CheckoutActionContext context) throws EpSystemException {
		final ShoppingCart shoppingCart = context.getShoppingCart();
		ShoppingCartPricingSnapshot shoppingCartPricingSnapshot = context.getShoppingCartTaxSnapshot().getShoppingCartPricingSnapshot();
		final PromotionRecordContainer promotionRecordContainer = shoppingCartPricingSnapshot.getPromotionRecordContainer();
		orderService.updateLimitedUsagePromotionCurrentNumbers(promotionRecordContainer.getAppliedRules(),
			new ArrayList<>(promotionRecordContainer.getLimitedUsagePromotionRuleCodes().keySet()));
		couponUsageService.updateLimitedUsageCouponCurrentNumbers(shoppingCart,
																shoppingCartPricingSnapshot,
																context.getOrder().getAppliedRules());
	}

	@Override
	public void rollback(final CheckoutActionContext context) throws EpSystemException {
		// NO OP
	}

	protected OrderService getOrderService() {
		return orderService;
	}

	public void setOrderService(final OrderService orderService) {
		this.orderService = orderService;
	}

	protected CouponUsageService getCouponUsageService() {
		return couponUsageService;
	}

	public void setCouponUsageService(final CouponUsageService couponUsageService) {
		this.couponUsageService = couponUsageService;
	}
}
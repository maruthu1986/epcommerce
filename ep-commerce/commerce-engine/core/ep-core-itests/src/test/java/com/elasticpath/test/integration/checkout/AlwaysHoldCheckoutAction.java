/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.test.integration.checkout;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.shoppingcart.actions.CheckoutActionContext;
import com.elasticpath.service.shoppingcart.actions.ReversibleCheckoutAction;

/**
 * Utility check out action that always puts a checked out shopping cart's order on hold.
 */
public class AlwaysHoldCheckoutAction implements ReversibleCheckoutAction {

	private OrderService orderService;

	@Override
	public void execute(CheckoutActionContext context) throws EpSystemException {
		orderService.holdOrder(context.getOrder());
	}

	@Override
	public void rollback(CheckoutActionContext context)
			throws EpSystemException {
		orderService.releaseOrder(context.getOrder());
	}

	public void setOrderService(final OrderService orderService) {
		this.orderService = orderService;
	}
}

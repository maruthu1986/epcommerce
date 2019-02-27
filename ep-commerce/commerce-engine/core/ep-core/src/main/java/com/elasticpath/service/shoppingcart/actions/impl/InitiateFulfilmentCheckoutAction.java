/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.shoppingcart.actions.impl;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.shoppingcart.actions.CheckoutActionContext;
import com.elasticpath.service.shoppingcart.actions.ReversibleCheckoutAction;

/**
 * Checkout Action that initiates the fulfilment process for the order being checked out.
 */
public class InitiateFulfilmentCheckoutAction implements ReversibleCheckoutAction {

	private OrderService orderService;

	@Override
	public void execute(final CheckoutActionContext context) throws EpSystemException {
		final Order order = context.getOrder();
		if (isOrderInAFulfillableState(order)) {
			final Order updatedOrder = getOrderService().releaseOrder(order);

			updatedOrder.setModifiedBy(order.getModifiedBy());
			context.setOrder(updatedOrder);
		}
	}

	/**
	 * Checks if a given Order is in a state that allows fulfillment to be started.
	 * @param order the order we want to check.
	 * @return true if the fulfillment can be initiated.
	 */
	protected boolean isOrderInAFulfillableState(final Order order) {
		return !OrderStatus.AWAITING_EXCHANGE.equals(order.getStatus())
				&& !OrderStatus.ONHOLD.equals(order.getStatus());
	}

	@Override
	public void rollback(final CheckoutActionContext context) throws EpSystemException {
		// no-op
	}

	protected OrderService getOrderService() {
		return orderService;
	}

	public void setOrderService(final OrderService orderService) {
		this.orderService = orderService;
	}

}

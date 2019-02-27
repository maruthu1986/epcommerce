/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.shoppingcart.actions.impl;

import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.shoppingcart.OrderFactory;
import com.elasticpath.service.shoppingcart.actions.CheckoutActionContext;
import com.elasticpath.service.shoppingcart.actions.ReversibleCheckoutAction;

/**
 * CheckoutAction to create a new order from the shopping cart.
 */
public class CreateNewOrderCheckoutAction implements ReversibleCheckoutAction {
	private static final Logger LOG = Logger.getLogger(CreateNewOrderCheckoutAction.class);

	private OrderFactory orderFactory;

	private OrderService orderService;

	@Override
	public void execute(final CheckoutActionContext context) throws EpSystemException {
		final Order newOrder = orderFactory.createAndPersistNewEmptyOrder(
				context.getCustomer(),
				context.getCustomerSession(),
				context.getShoppingCart(),
				context.isOrderExchange(),
				context.isAwaitExchangeCompletion());

		context.setOrder(populateOrder(context, newOrder));
	}

	/**
	 * Given an empty order, populates the empty order with the appropriate values
	 * from the CheckoutActionContext.
	 *
	 * @param context the {@link CheckoutActionContext}
	 * @param emptyOrder an empty {@link Order}
	 * @return the populated Order.
	 */
	protected Order populateOrder(final CheckoutActionContext context, final Order emptyOrder) {
		if (context.isOrderExchange()) {
			return orderFactory.fillInNewExchangeOrderFromShoppingCart(
					emptyOrder,
					context.getCustomer(),
					context.getCustomerSession(),
					context.getShoppingCart(),
					context.getShoppingCartTaxSnapshot(),
					context.isAwaitExchangeCompletion(),
					context.getExchange());
		}

		return orderFactory.fillInNewOrderFromShoppingCart(
					emptyOrder,
					context.getCustomer(),
					context.getCustomerSession(),
					context.getShoppingCart(),
					context.getShoppingCartTaxSnapshot());
	}

	@Override
	public void rollback(final CheckoutActionContext context) throws EpSystemException {
		Order order = context.getOrder();
		if (order == null) {
			LOG.error("Order not found in checkout action context");
		} else {
			try {
				order.failOrder();
				Collection<OrderPayment> orderPaymentList = context.getOrderPaymentList();
				if (CollectionUtils.isNotEmpty(orderPaymentList)) {
					order.setOrderPayments(new HashSet<>(orderPaymentList));
				}
				if (order.isPersisted()) {
					order = orderService.update(order);
				} else {
					order = orderService.add(order);
				}
				context.setOrder(order);
				if (LOG.isDebugEnabled()) {
					LOG.debug("failing order: " + order.getOrderNumber());
				}
			} catch (final Exception e) {
				LOG.error("Can't set the order status to failing " + order, e);
			}
		}
	}

	protected OrderFactory getOrderFactory() {
		return orderFactory;
	}

	public void setOrderFactory(final OrderFactory orderFactory) {
		this.orderFactory = orderFactory;
	}

	protected OrderService getOrderService() {
		return orderService;
	}

	public void setOrderService(final OrderService orderService) {
		this.orderService = orderService;
	}

}
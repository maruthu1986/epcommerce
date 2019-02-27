/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.shoppingcart.actions.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.payment.PaymentService;
import com.elasticpath.service.shoppingcart.actions.CheckoutActionContext;
import com.elasticpath.service.shoppingcart.actions.ReversibleCheckoutAction;

/**
 * CheckoutAction to capture order payments.
 */
public class CapturePaymentsCheckoutAction implements ReversibleCheckoutAction {

	private PaymentService paymentService;
	private OrderService orderService;

	@Override
	public void execute(final CheckoutActionContext context) throws EpSystemException {
		final Order order = context.getOrder();

		if (OrderStatus.ONHOLD.equals(order.getStatus())) {
			return;
		}

		final Collection<OrderPayment> originalOrderPayments = new ArrayList<>(context.getOrderPaymentList());

		orderService.captureImmediatelyShippableShipments(order);

		final Order updatedOrder = orderService.update(order);
		updatedOrder.setModifiedBy(order.getModifiedBy());

		context.setOrder(updatedOrder);
		context.setOrderPaymentList(updatedOrder.getOrderPayments());

		context.preserveTransientOrderPayment(originalOrderPayments);
	}


	@Override
	public void rollback(final CheckoutActionContext context) throws EpSystemException {
		if (context.getOrder() != null) {
			
			Set<OrderPayment> orderPayments = new HashSet<>(context.getOrder().getOrderPayments());
			
			CollectionUtils.filter(orderPayments, new Predicate() {

				@Override
				public boolean evaluate(final Object object) {
					return ((OrderPayment) object).getTransactionType().equals(OrderPayment.CAPTURE_TRANSACTION);
				}
			});
			
			paymentService.rollBackPayments(orderPayments);
		}
	}

	public void setPaymentService(final PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	public void setOrderService(final OrderService orderService) {
		this.orderService = orderService;
	}

}

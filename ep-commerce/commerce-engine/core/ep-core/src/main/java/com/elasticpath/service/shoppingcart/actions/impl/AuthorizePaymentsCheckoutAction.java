/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.shoppingcart.actions.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.service.payment.PaymentResult;
import com.elasticpath.service.payment.PaymentService;
import com.elasticpath.service.shoppingcart.actions.CheckoutActionContext;
import com.elasticpath.service.shoppingcart.actions.ReversibleCheckoutAction;

/**
 * CheckoutAction to authorize order payments (pre-authorizations for all shipments).
 */
public class AuthorizePaymentsCheckoutAction implements ReversibleCheckoutAction {
	private static final Logger LOG = Logger.getLogger(AuthorizePaymentsCheckoutAction.class);

	private PaymentService paymentService;

	@Override
	public void execute(final CheckoutActionContext context) throws EpSystemException {
		final List<OrderPayment> allPayments;
		if (context.getOrderPaymentList() == null) {
			allPayments = new ArrayList<>();
		} else {
			allPayments = new ArrayList<>(context.getOrderPaymentList());
		}

		final PaymentResult orderPaymentResult = paymentService.initializePayments(context.getOrder(),
				context.getOrderPaymentTemplate(), context.getShoppingCart().getAppliedGiftCertificates());
		if (orderPaymentResult.getResultCode() != PaymentResult.CODE_OK) {
			LOG.debug("Payment service reported failed payments.", orderPaymentResult.getCause());
			if (orderPaymentResult.getCause() != null) {
				throw orderPaymentResult.getCause();
			}
			throw new EpServiceException("Payment service reported failed payments.");
		}
		allPayments.addAll(orderPaymentResult.getProcessedPayments());

		context.setOrderPaymentList(allPayments);
	}

	@Override
	public void rollback(final CheckoutActionContext context) throws EpSystemException {
		if (context.getOrder() != null) {

			Set<OrderPayment> orderPayments = new HashSet<>(context.getOrder().getOrderPayments());
			
			CollectionUtils.filter(orderPayments, new Predicate() {

				@Override
				public boolean evaluate(final Object object) {
					return ((OrderPayment) object).getTransactionType().equals(OrderPayment.AUTHORIZATION_TRANSACTION);
				}
			});
			
			paymentService.rollBackPayments(orderPayments);
		}
	}

	protected PaymentService getPaymentService() {
		return paymentService;
	}

	public void setPaymentService(final PaymentService paymentService) {
		this.paymentService = paymentService;
	}
}

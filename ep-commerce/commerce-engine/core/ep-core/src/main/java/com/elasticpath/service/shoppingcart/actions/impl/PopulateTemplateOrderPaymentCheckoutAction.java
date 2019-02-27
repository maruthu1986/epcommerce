/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.shoppingcart.actions.impl;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.service.shoppingcart.actions.CheckoutActionContext;
import com.elasticpath.service.shoppingcart.actions.ReversibleCheckoutAction;

/**
 * CheckoutAction to populate the template orderPayment object using order details.
 */
public class PopulateTemplateOrderPaymentCheckoutAction implements ReversibleCheckoutAction {

	@Override
	public void execute(final CheckoutActionContext context) throws EpSystemException {
		final OrderPayment templateOrderPayment = context.getOrderPaymentTemplate();
		final Order order = context.getOrder();

		// sets some properties of the order payment object
		// haven't they been set yet?
		if (StringUtils.isBlank(templateOrderPayment.getReferenceId())) {
			templateOrderPayment.setReferenceId(order.getOrderNumber());
		}
		templateOrderPayment.setCurrencyCode(order.getCurrency().getCurrencyCode());
		templateOrderPayment.setEmail(order.getCustomer().getEmail());
		templateOrderPayment.setIpAddress(context.getCustomerIpAddress());
	}

	@Override
	public void rollback(final CheckoutActionContext context) throws EpSystemException {
		// NO OP
	}
}

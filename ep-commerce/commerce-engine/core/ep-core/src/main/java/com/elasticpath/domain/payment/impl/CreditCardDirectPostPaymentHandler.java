/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.payment.impl;

import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.plugin.payment.PaymentType;

/**
 * Credit card direct post payment handler.
 */
public class CreditCardDirectPostPaymentHandler extends AbstractPaymentHandler {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	@Override
	protected PaymentType getPaymentType() {	
		return PaymentType.CREDITCARD_DIRECT_POST;
	}

	@Override
	public Collection<OrderPayment> generateAuthorizeOrderPayments(final OrderPayment templateOrderPayment, final Order order) {
		Collection<OrderPayment> shipmentPayments = new HashSet<>();
		shipmentPayments.add(createAuthOrderPayment(order, templateOrderPayment));
		return shipmentPayments;
	}

	@Override
	public Collection<OrderPayment> generateAuthorizeShipmentPayments(final OrderPayment templateOrderPayment,
																		final OrderShipment orderShipment,
																		final Collection<OrderPayment> allAuthPayments) {
		return null;
	}

	/**
	 * @param order the order to authorize
	 * @param templateOrderPayment the template order payment
	 * @return OrderPayment
	 */
	protected OrderPayment createAuthOrderPayment(final Order order, final OrderPayment templateOrderPayment) {

		OrderPayment orderPayment = getNewOrderPayment();
		orderPayment.setPaymentMethod(templateOrderPayment.getPaymentMethod());
		orderPayment.copyTransactionFollowOnInfo(templateOrderPayment);
		orderPayment.setGatewayToken(templateOrderPayment.getGatewayToken());
		orderPayment.setGiftCertificate(templateOrderPayment.getGiftCertificate());
		orderPayment.setAmount(templateOrderPayment.getAmount());
		orderPayment.setTransactionType(OrderPayment.AUTHORIZATION_TRANSACTION);
		orderPayment.setCreatedDate(getTimeService().getCurrentTime());
		orderPayment.setOrder(order);
		orderPayment.setOrderShipment(null);
		if (StringUtils.isNotBlank(templateOrderPayment.getReferenceId())) {
			orderPayment.setReferenceId(templateOrderPayment.getReferenceId());
		} else {
			orderPayment.setReferenceId(order.getOrderNumber());
		}
		orderPayment.setIpAddress(order.getIpAddress());
		orderPayment.setEmail(getEmail(order, templateOrderPayment.getEmail()));
		return orderPayment;
	}
}

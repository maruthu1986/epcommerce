/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.payment.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.service.payment.PaymentServiceException;
import com.elasticpath.service.payment.impl.OrderPaymentHelper;

/**
 * Paypal express handler.
 *
 * @deprecated Use DirectPostPayPalExpressPaymentHandler instead.
 */
@Deprecated
public class PayPalExpressPaymentHandler extends AbstractPaymentHandler {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	private static final BigDecimal EXCEED_FACTOR = BigDecimal.valueOf(1.15);

	/**
	 * Initialize the order payments.
	 * For Paypal, we need make an order payment before we can process other payments.
	 * Also if we need change the payment template for different payment methods, do it here.
	 *
	 * @param order the order
	 * @param templateOrderPayment the order payment template
	 * @return a collection of order payments created
	 */
	@Override
	public Collection<OrderPayment> generateAuthorizeOrderPayments(final OrderPayment templateOrderPayment, final Order order) {
		List<OrderPayment> orderPayments = new ArrayList<>(1);
		OrderPayment orderPayment = createOrderPayment(order, templateOrderPayment, order.getTotal());
		orderPayments.add(orderPayment);

		return orderPayments;
	}

	@Override
	protected Collection<OrderPayment> getPreAuthorizedPayments(final OrderPayment templateOrderPayment,
			final OrderShipment orderShipment, final BigDecimal amount) {
		if (!canAuthorizePartly(orderShipment)) {
			List<OrderPayment> orderPayments = new ArrayList<>(1);
			OrderPayment authorizePayment = createAuthOrderPayment(orderShipment, templateOrderPayment, amount);
			orderPayments.add(authorizePayment);
			return orderPayments;
		}

		return new ArrayList<>(0);

	}

	@Override
	public boolean canAuthorizePartly(final OrderShipment orderShipment) {
		for (OrderPayment orderPayment : orderShipment.getOrder().getOrderPayments()) {
			if (PaymentType.PAYPAL_EXPRESS.equals(orderPayment.getPaymentMethod())
					&& OrderPayment.AUTHORIZATION_TRANSACTION.equals(orderPayment.getTransactionType())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param orderShipment the order shipment
	 * @param templateOrderPayment the template order payment
	 * @param amount the money amount this payment should be
	 * @return OrderPayment
	 */
	@Override
	protected OrderPayment createAuthOrderPayment(final OrderShipment orderShipment,
												  final OrderPayment templateOrderPayment,
												  final BigDecimal amount) {

		if (amount.compareTo(BigDecimal.ZERO) > 0) {
			OrderPayment orderPayment = getNewOrderPayment();
			orderPayment.setPaymentMethod(templateOrderPayment.getPaymentMethod());
			orderPayment.copyTransactionFollowOnInfo(templateOrderPayment);
			orderPayment.setGatewayToken(templateOrderPayment.getGatewayToken());
			orderPayment.setGiftCertificate(templateOrderPayment.getGiftCertificate());
			orderPayment.setAmount(amount);
			orderPayment.setTransactionType(OrderPayment.AUTHORIZATION_TRANSACTION);
			orderPayment.setCreatedDate(getTimeService().getCurrentTime());
			// This needs to be set to null to mark this payment as being at an
			// order level to allow the creation of a capture
			// payment for an electronic shipment during checkout to work.
			// This is different to the other handlers which set the shipment
			// on the order payment at this point.
			orderPayment.setOrder(orderShipment.getOrder());
			orderPayment.setOrderShipment(null);
			if (StringUtils.isNotBlank(templateOrderPayment.getReferenceId())) {
				orderPayment.setReferenceId(templateOrderPayment.getReferenceId());
			} else {
				orderPayment.setReferenceId(orderShipment.getOrder().getOrderNumber());
			}

			orderPayment.setIpAddress(orderShipment.getOrder().getIpAddress());
			OrderPayment orderAuthPayment = OrderPaymentHelper.findActiveOrderAuthorizationPayment(
					orderShipment.getOrder());
			if (orderAuthPayment != null) {
				orderPayment.setAuthorizationCode(orderAuthPayment.getAuthorizationCode());
			}
			return orderPayment;
		}

		throw new PaymentServiceException("Can not create an authorization payment for less or equal 0");
	}

	/**
	 * @param orderShipment the order shipment
	 * @param amount the amount to be captured
	 * @return a collection of OrderPayment objects
	 */
	@Override
	protected OrderPayment createCapturePayment(final OrderShipment orderShipment, final BigDecimal amount) {
		OrderPayment authorizationPayment = OrderPaymentHelper.findActiveConventionalAuthorizationPayment(orderShipment);
		if (authorizationPayment == null) {
			throw new PaymentServiceException("Expected exactly one order payment.");
		}

		OrderPayment capturePayment = getNewOrderPayment();
		capturePayment.setPaymentMethod(authorizationPayment.getPaymentMethod());
		capturePayment.copyTransactionFollowOnInfo(authorizationPayment);
		capturePayment.setGatewayToken(authorizationPayment.getGatewayToken());
		capturePayment.setGiftCertificate(authorizationPayment.getGiftCertificate());
		capturePayment.setTransactionType(OrderPayment.CAPTURE_TRANSACTION);
		capturePayment.setCreatedDate(getTimeService().getCurrentTime());
		capturePayment.setAuthorizationCode(authorizationPayment.getAuthorizationCode());
		capturePayment.setOrder(orderShipment.getOrder());
		capturePayment.setOrderShipment(orderShipment);

		// set the total amount of the capture payment to be the order shipment total
		capturePayment.setAmount(amount);

		return capturePayment;
	}

	@Override
	protected PaymentType getPaymentType() {
		return PaymentType.PAYPAL_EXPRESS;
	}


	/**
	 *
	 *
	 * @param orderPayment the order payment
	 * @param amount       the amount
	 * @return boolean
	 */
	@Override
	public boolean canCapture(final OrderPayment orderPayment, final BigDecimal amount) {
		return EXCEED_FACTOR.multiply(orderPayment.getAmount()).compareTo(amount) >= 0;
	}
}

/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.domain.payment.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.service.payment.PaymentServiceException;
import com.elasticpath.service.payment.impl.OrderPaymentHelper;

/**
 * Implementation of {@link com.elasticpath.domain.payment.PaymentHandler} for tokenized payments.
 */
public class TokenPaymentHandler extends AbstractPaymentHandler {
	/** Serial version id. */
	public static final long serialVersionUID = 5000000001L;
	
	@Override
	protected Collection<OrderPayment> getPreAuthorizedPayments(final OrderPayment templateOrderPayment, 
			final OrderShipment orderShipment, final BigDecimal amount) {

		return Collections.singletonList(createAuthOrderPayment(orderShipment, templateOrderPayment, amount));
	}

	@Override
	protected Collection<OrderPayment> getCapturePayments(final OrderPayment authOrderPayment, 
			final OrderShipment orderShipment, final BigDecimal amount) {

		OrderPayment authorizationPayment = OrderPaymentHelper.findActiveConventionalAuthorizationPayment(orderShipment);
		if (authorizationPayment == null) {
			throw new PaymentServiceException("No authorization payment found");
		}		
		
		return Collections.singletonList(createCapturePaymentFromAuthorization(authorizationPayment, orderShipment, amount));
	}

	@Override
	protected PaymentType getPaymentType() {
		return PaymentType.PAYMENT_TOKEN;
	}
	
	/**
	 * Creates an authorization {@link OrderPayment} using the submitted OrderPayment as a template.
	 * @param orderShipment the {@link OrderShipment} for this {@link OrderPayment}  
	 * @param templateOrderPayment the {@link OrderPayment} used as a template
	 * @param amount the amount to authorize
	 * @return the authorization {@link OrderPayment} 
	 */
	@Override
	protected OrderPayment createAuthOrderPayment(final OrderShipment orderShipment, final OrderPayment templateOrderPayment,
			final BigDecimal amount) {
		if (amount.compareTo(BigDecimal.ZERO) > 0) {
			OrderPayment orderPayment = getNewOrderPayment();
			orderPayment.setPaymentMethod(templateOrderPayment.getPaymentMethod());
			orderPayment.setAmount(amount);
			orderPayment.setCurrencyCode(templateOrderPayment.getCurrencyCode());
			orderPayment.setReferenceId(orderShipment.getShipmentNumber());
			orderPayment.setTransactionType(OrderPayment.AUTHORIZATION_TRANSACTION);
			orderPayment.setOrder(orderShipment.getOrder());
			orderPayment.setOrderShipment(orderShipment);
			orderPayment.usePaymentToken(templateOrderPayment.extractPaymentToken());
			orderPayment.setCreatedDate(getTimeService().getCurrentTime());
			orderPayment.setPaymentMethod(getPaymentType());

			orderPayment.setIpAddress(orderShipment.getOrder().getIpAddress());

			String email = getEmailFromOrderPayment(orderShipment, templateOrderPayment);
			orderPayment.setEmail(email);

			return orderPayment;
		}

		throw new PaymentServiceException("Can not create an authorization payment for less than or equal to 0");
	}

	private String getEmailFromOrderPayment(final OrderShipment orderShipment, final OrderPayment templateOrderPayment) {
		String email = templateOrderPayment.getEmail();
		if (StringUtils.isBlank(email)) {
			Order order = orderShipment.getOrder();

			if (order.getCustomer() != null) {
				email = order.getCustomer().getEmail();
			}
		}
		return email;
	}

	/**
	 * Creates a capture {@link OrderPayment} using the submitted OrderPayment as a template.
	 * @param orderShipment the order shipment
	 * @param amount the amount to capture
	 * @return the {@link OrderPayment} to capture
	 */
	private OrderPayment createCapturePaymentFromAuthorization(final OrderPayment authorizationPayment,
			final OrderShipment orderShipment, final BigDecimal amount) {
		OrderPayment capturePayment = getNewOrderPayment();
		capturePayment.setPaymentMethod(authorizationPayment.getPaymentMethod());
		capturePayment.setTransactionType(OrderPayment.CAPTURE_TRANSACTION);
		capturePayment.setCreatedDate(getTimeService().getCurrentTime());
		capturePayment.copyTransactionFollowOnInfo(authorizationPayment);
		capturePayment.setOrder(orderShipment.getOrder());
		capturePayment.setOrderShipment(orderShipment);
		capturePayment.setAmount(amount);
		capturePayment.usePaymentToken(authorizationPayment.extractPaymentToken());
		// for some reason the last modified entity listener is not firing when this is cascade persisted from order
		capturePayment.copyLastModifiedDate(authorizationPayment);
		return capturePayment;
	}

}

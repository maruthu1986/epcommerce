/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.shoppingcart.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.misc.CheckoutResults;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.domain.store.Store;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.plugin.payment.dto.PaymentOptionFormDescriptor;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.payment.PaymentService;
import com.elasticpath.service.shoppingcart.CheckoutService;
import com.elasticpath.service.shoppingcart.ExternalAuthCheckoutService;
import com.elasticpath.service.shoppingcart.ExternalAuthUrls;
import com.elasticpath.service.shoppingcart.OrderSkuFactory;

/**
 * Provides functionality for external authentication checkouts.
 */
public class ExternalAuthCheckoutServiceImpl implements ExternalAuthCheckoutService {
	
	private CheckoutService checkoutService;
	private PaymentService paymentService;
	private TimeService timeService;
	private OrderSkuFactory orderSkuFactory;
	private BeanFactory beanFactory;

	@Override
	public PaymentOptionFormDescriptor createPaymentOptionFormDescriptor(
			final CustomerSession customerSession,
			final ShoppingCart shoppingCart,
			final ShoppingCartTaxSnapshot pricingSnapshot,
			final PaymentType paymentType,
			final ExternalAuthUrls externalAuthUrls,
			final PaymentGateway paymentGateway) {
		final OrderPayment orderPaymentTemplate = getBeanFactory().getBean(ContextIdNames.ORDER_PAYMENT);
		orderPaymentTemplate.setAmount(pricingSnapshot.getTotal());
		orderPaymentTemplate.setCurrencyCode(customerSession.getCurrency().getCurrencyCode());
		orderPaymentTemplate.setEmail(shoppingCart.getShopper().getCustomer().getEmail());
		orderPaymentTemplate.setReferenceId(generatePaymentReferenceNumber(shoppingCart));
		orderPaymentTemplate.setIpAddress(customerSession.getIpAddress());
		orderPaymentTemplate.setOrderShipment(createTemplateOrderShipmentFromShoppingCart(shoppingCart, pricingSnapshot, customerSession));

		String completeRedirectExternalAuthUrl = null;
		if (externalAuthUrls.getRedirectUrl() != null) {
			completeRedirectExternalAuthUrl = externalAuthUrls.getRedirectUrl() + "?paymentType=" + paymentType.getName();
		}
		String completeFinishExternalAuthUrl = externalAuthUrls.getFinishUrl() + "?paymentType=" + paymentType.getName();
		return paymentGateway.buildExternalAuthRequest(
				orderPaymentTemplate,
				shoppingCart.getBillingAddress(),
				completeRedirectExternalAuthUrl,
				completeFinishExternalAuthUrl,
				externalAuthUrls.getCancelUrl());
	}

	private OrderShipment createTemplateOrderShipmentFromShoppingCart(final ShoppingCart shoppingCart,
																		final ShoppingCartTaxSnapshot pricingSnapshot,
																		final CustomerSession customerSession) {
		OrderShipment orderShipment = getBeanFactory().getBean(ContextIdNames.TEMPLATE_ORDER_SHIPMENT);
		orderShipment.setShipmentNumber(generatePaymentReferenceNumber(shoppingCart));

		Collection<OrderSku> orderSkus = orderSkuFactory.createOrderSkus(shoppingCart.getRootShoppingItems(),
																		 pricingSnapshot,
																		 customerSession.getLocale());

		for (OrderSku orderSku : orderSkus) {
			orderShipment.addShipmentOrderSku(orderSku);
		}

		return orderShipment;
	}

	/**
	 * Generate a payment reference number, since the order number is not available.
	 * @param shoppingCart the shopping cart
	 * @return a string representing the reference number for the payment gateway
	 */
	protected String generatePaymentReferenceNumber(final ShoppingCart shoppingCart) {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
		return shoppingCart.getShopper().getCustomer().getUidPk() + "-" + dateFormat.format(timeService.getCurrentTime());
	}

	@Override
	public CheckoutResults checkoutAfterExternalAuth(final ShoppingCart shoppingCart,
														final ShoppingCartTaxSnapshot pricingSnapshot,
														final CustomerSession customerSession,
														final PaymentType paymentType,
														final Map<String, String> responseMap) {
		final PaymentGateway paymentGateway = paymentService.findPaymentGateway(shoppingCart.getStore(), paymentType.getPaymentGatewayType());
		
		final OrderPayment orderPaymentTemplate = paymentGateway.handleExternalAuthResponse(paymentType, responseMap);
		orderPaymentTemplate.setOrderShipment(createTemplateOrderShipmentFromShoppingCart(shoppingCart, pricingSnapshot, customerSession));
		return checkoutService.checkout(shoppingCart, pricingSnapshot, customerSession, orderPaymentTemplate, true);
	}

	@Override
	public PaymentOptionFormDescriptor prepareForRedirect(
			final Store store, final PaymentType paymentType, final Map<String, String> responseMap) {
		final PaymentGateway paymentGateway = paymentService.findPaymentGateway(store, paymentType.getPaymentGatewayType());
		return paymentGateway.prepareForRedirect(paymentType, responseMap);
	}
	
	protected CheckoutService getCheckoutService() {
		return checkoutService;
	}

	public void setCheckoutService(final CheckoutService checkoutService) {
		this.checkoutService = checkoutService;
	}

	protected PaymentService getPaymentService() {
		return paymentService;
	}

	public void setPaymentService(final PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}

	public void setOrderSkuFactory(final OrderSkuFactory orderSkuFactory) {
		this.orderSkuFactory = orderSkuFactory;
	}

	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

}

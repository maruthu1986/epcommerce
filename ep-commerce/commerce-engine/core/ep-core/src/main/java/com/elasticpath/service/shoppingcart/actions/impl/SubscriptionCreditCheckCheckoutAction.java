/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.shoppingcart.actions.impl;

import java.math.BigDecimal;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderPaymentStatus;
import com.elasticpath.domain.payment.CreditCardPaymentGateway;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.domain.store.Store;
import com.elasticpath.plugin.payment.PaymentGatewayType;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.payment.PaymentServiceException;
import com.elasticpath.service.shoppingcart.actions.CheckoutActionContext;
import com.elasticpath.service.shoppingcart.actions.ReversibleCheckoutAction;
import com.elasticpath.service.store.StoreService;

/**
 * CheckoutAction to trigger preCheckout events registered through checkoutEventHandler.
 */
public class SubscriptionCreditCheckCheckoutAction implements ReversibleCheckoutAction {

	private BeanFactory beanFactory;
	private StoreService storeService;
	private TimeService timeService;

	@Override
	public void execute(final CheckoutActionContext context) {
		
		final ShoppingCart shoppingCart = context.getShoppingCart();
		final Order order = context.getOrder();
		final OrderPayment orderPaymentTemplate = context.getOrderPaymentTemplate();
		ShoppingCartTaxSnapshot shoppingCartTaxSnapshot = context.getShoppingCartTaxSnapshot();

		if (BigDecimal.ZERO.compareTo(shoppingCartTaxSnapshot.getTotal()) == 0 && shoppingCart.hasRecurringPricedShoppingItems()) {
			final OrderPayment authorizationOrderPayment = createOrderPaymentForRecurringChargePurchaseOnly(order,
					orderPaymentTemplate, OrderPayment.AUTHORIZATION_TRANSACTION);

			final PaymentGateway paymentGateway = findPaymentGateway(order, authorizationOrderPayment.getPaymentMethod().getPaymentGatewayType());

			paymentGateway.preAuthorize(authorizationOrderPayment, order.getBillingAddress());
			authorizationOrderPayment.setStatus(OrderPaymentStatus.APPROVED);
			order.addOrderPayment(authorizationOrderPayment);

			paymentGateway.reversePreAuthorization(authorizationOrderPayment);
			OrderPayment reversalOrderPayment = createOrderPaymentForRecurringChargePurchaseOnly(order, orderPaymentTemplate,
					OrderPayment.REVERSE_AUTHORIZATION);
			reversalOrderPayment.setAuthorizationCode(authorizationOrderPayment.getAuthorizationCode());
			reversalOrderPayment.setStatus(OrderPaymentStatus.APPROVED);
			reversalOrderPayment.setOrder(order);
			order.addOrderPayment(reversalOrderPayment);
		}
	}

	private PaymentGateway findPaymentGateway(final Order order, final PaymentGatewayType paymentGatewayType) {
		final Store store = getStoreService().findStoreWithCode(order.getStoreCode());
		PaymentGateway paymentGateway = store.getPaymentGatewayMap().get(paymentGatewayType);
		if (paymentGateway == null) {
			throw new PaymentServiceException("No payment gateway is defined for payment gateway type: " + paymentGatewayType);
		}

		if (PaymentGatewayType.CREDITCARD.equals(paymentGateway.getPaymentGatewayType())) {
			((CreditCardPaymentGateway) paymentGateway).setValidateCvv2(store.isCreditCardCvv2Enabled());
		}

		return paymentGateway;
	}

	private OrderPayment createOrderPaymentForRecurringChargePurchaseOnly(final Order order,
																			final OrderPayment templateOrderPayment,
																			final String transactionType) {
		OrderPayment orderPayment = getBeanFactory().getBean(ContextIdNames.ORDER_PAYMENT);
		orderPayment.copyTransactionFollowOnInfo(templateOrderPayment);
		orderPayment.setPaymentForSubscriptions(true);
		orderPayment.setGatewayToken(templateOrderPayment.getGatewayToken());
		orderPayment.setGiftCertificate(templateOrderPayment.getGiftCertificate());
		orderPayment.setAmount(BigDecimal.ONE);
		orderPayment.setTransactionType(transactionType);
		orderPayment.setCreatedDate(timeService.getCurrentTime());
		orderPayment.usePaymentToken(templateOrderPayment.extractPaymentToken());
		orderPayment.setOrder(order);
		return orderPayment;
	}

	@Override
	public void rollback(final CheckoutActionContext context) throws EpSystemException {
		//do nothing, it is ok
	}

	/**
	 * Sets the bean factory.
	 * @param beanFactory the bean factory
	 * */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}

	protected StoreService getStoreService() {
		return storeService;
	}

	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}
}

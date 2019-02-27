/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.repositories;

import io.reactivex.Single;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.misc.CheckoutResults;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.plugin.payment.dto.PaymentMethod;

/**
 * Integration Repository for Purchase interactions with CE.
 */
public interface PurchaseRepository {

	/**
	 * Checkout. Successful execution result does not imply that checkout was successful just that it completed without error.
	 *
	 * @param shoppingCart Cart to checkout.
	 * @param taxSnapshot the taxed pricing snapshot of the given shopping cart
	 * @param customerSession the customer session
	 * @param orderPayment payment means.
	 * @return the result of the checkout.
	 */
	Single<CheckoutResults> checkout(ShoppingCart shoppingCart, ShoppingCartTaxSnapshot taxSnapshot,
									 CustomerSession customerSession, OrderPayment orderPayment);

	/**
	 * Provides and order Payment for a given payment method.
	 * @param paymentMethod payment method.
	 * @return order payment.
	 */
	Single<OrderPayment> getOrderPaymentFromPaymentMethod(PaymentMethod paymentMethod);

	/**
	 * Creates a new OrderPayment entity, this method does no persistence.
	 * @return order payment.
	 */
	Single<OrderPayment> createNewOrderPaymentEntity();

}

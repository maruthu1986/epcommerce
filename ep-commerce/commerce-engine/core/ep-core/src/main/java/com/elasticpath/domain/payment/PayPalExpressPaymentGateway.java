/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.domain.payment;

import java.util.Map;

import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.shoppingcart.ShoppingCart;

/**
 * Provides services for integrating with PayPal Express Payment.
 * @deprecated Use DirectPostPaypalExpressPaymentGatewayPluginImpl (which doesn't require this class) instead.
 */
@Deprecated
public interface PayPalExpressPaymentGateway extends PaymentGateway {

	/**
	 * Start the ExpressCheckout for Authorization using the EC Mark method. Redirect customer to paypal site to log in and select fund source.
	 *
	 * @param shoppingCart the shopping cart
	 * @param returnUrl the url to return from paypal upon completion the payment action.
	 * @param cancelUrl the url to return to if the user decide to cancel the payment action.
	 * @return the token string.
	 */
	String setExpressMarkCheckout(ShoppingCart shoppingCart, String returnUrl, String cancelUrl);

	/**
	 * Start the ExpressCheckout for Authorization using the EC Shortcut method. Redirect customer to paypal site to log in and select fund source.
	 *
	 * @param shoppingCart the shopping cart
	 * @param returnUrl the url to return from paypal upon completion the payment action.
	 * @param cancelUrl the url to return to if the user decide to cancel the payment action.
	 * @return the token string.
	 */
	String setExpressShortcutCheckout(ShoppingCart shoppingCart, String returnUrl, String cancelUrl);

	/**
	 * Get the payerinfo before start the payment.
	 *
	 * @param token the token from paypal when setting up express checkout.
	 * @return Map of the returned key-value pairs.
	 */
	Map<String, String> getExpressCheckoutDetails(String token);

	/**
	 * Pre-authorize a payment on an existing order.
	 *
	 * @param orderPayment the payment to be captured
	 */
	void authorizeOrder(OrderPayment orderPayment);

	/**
	 * Verify sufficient funds exist for a payment, but don't put a hold on them.
	 *
	 * @param payment the payment to be verified
	 * @param billingAddress the name and address of the person being billed
	 */
	void order(OrderPayment payment, Address billingAddress);
}

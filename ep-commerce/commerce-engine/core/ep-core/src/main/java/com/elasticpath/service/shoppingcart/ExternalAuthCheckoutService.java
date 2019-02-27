/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.shoppingcart;

import java.util.Map;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.misc.CheckoutResults;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.domain.store.Store;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.plugin.payment.dto.PaymentOptionFormDescriptor;

/**
 * Provides functionality for external authentication checkouts.
 */
public interface ExternalAuthCheckoutService {

	/**
	 * Obtain a PaymentOptionFormDescriptor which can be used to generate an HTML form that can post directly to the payment gateway.
	 *
	 * @param customerSession the customer session to checkout
	 * @param shoppingCart the shopping cart to checkout
	 * @param pricingSnapshot the shopping cart pricing snapshot
	 * @param paymentType the payment type to be used to checkout
	 * @param externalAuthUrls the external urls that the gateway should call
	 * @param paymentGateway the payment gateway that will be used to process the payment
	 * @return a payment HTML form
	 */
	PaymentOptionFormDescriptor createPaymentOptionFormDescriptor(
			CustomerSession customerSession, ShoppingCart shoppingCart, ShoppingCartTaxSnapshot pricingSnapshot, PaymentType paymentType,
			ExternalAuthUrls externalAuthUrls, PaymentGateway paymentGateway);

	/**
	 * Checkout after receiving a successful response from the external authentication service.
	 *
	 * @param shoppingCart the shopping cart to checkout
	 * @param pricingSnapshot the shopping cart pricing snapshot
	 * @param customerSession the shopper's customer session
	 * @param paymentType the payment type to be used to checkout
	 * @param responseMap the response map from the external authentication service
	 * @return results of the checkout
	 */
	CheckoutResults checkoutAfterExternalAuth(ShoppingCart shoppingCart,
											ShoppingCartTaxSnapshot pricingSnapshot,
											CustomerSession customerSession,
											PaymentType paymentType,
											Map<String, String> responseMap);
	
	/**
	 * Prepares for redirecting to the real payment gateway hosted order page.
	 * 
	 * @param store the store
	 * @param paymentType the payment type to be used to checkout
	 * @param responseMap the responseMap
	 * @return PaymentOptionFormDescriptor
	 */
	PaymentOptionFormDescriptor prepareForRedirect(Store store, PaymentType paymentType, Map<String, String> responseMap);
}

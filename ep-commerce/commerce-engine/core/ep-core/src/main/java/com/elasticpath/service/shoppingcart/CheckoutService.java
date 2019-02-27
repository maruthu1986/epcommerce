/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.shoppingcart;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.misc.CheckoutResults;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;

/**
 * Provides checkout-related services.
 */
public interface CheckoutService {

	/**
	 * Processes an order for the items in the specified shopping cart. This will create an order, execute all configured
	 * checkout actions, commit the order and invalidate the cart.
	 *
	 * @param shoppingCart the {@link com.elasticpath.domain.shoppingcart.ShoppingCart}
	 * @param pricingSnapshot {@link com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot}
	 * @param customerSession the {@link com.elasticpath.domain.customer.CustomerSession}
	 * @param orderPayment the orderPayment representing the payment detail information.
	 * This OrderPayment simply keeps track of how the customer will be paying; it does not necessarily represent all
	 * of the payments on the order. For example, if paying only by Gift Certificate then the OrderPayment will
	 * have the payment type set to GiftCertificate, but there will be no information on the gift certificates
	 * being used. That information is contained within the ShoppingCart object.
	 * @return results of the checkout
	 * @throws com.elasticpath.commons.exception.InvalidBusinessStateException for all checkout errors to be displayed to user
	 * @throws com.elasticpath.base.exception.EpServiceException for other checkout processing errors
	 * @deprecated use checkout(shoppingCart, customerSession, orderPayment, throwExceptions)
	 */
	@Deprecated
	CheckoutResults checkout(ShoppingCart shoppingCart, ShoppingCartTaxSnapshot pricingSnapshot, CustomerSession customerSession,
							OrderPayment orderPayment);

	/**
	 * Processes an order for the items in the specified shopping cart. This will create an order, execute all configured
	 * checkout actions, commit the order and invalidate the cart.
	 *
	 * @param shoppingCart the {@link com.elasticpath.domain.shoppingcart.ShoppingCart}
	 * @param pricingSnapshot {@link com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot}
	 * @param customerSession the {@link com.elasticpath.domain.customer.CustomerSession}
	 * @param orderPayment the orderPayment representing the payment detail information.
	 * This OrderPayment simply keeps track of how the customer will be paying; it does not necessarily represent all
	 * of the payments on the order. For example, if paying only by Gift Certificate then the OrderPayment will
	 * have the payment type set to GiftCertificate, but there will be no information on the gift certificates
	 * being used. That information is contained within the ShoppingCart object.
	 * @param throwExceptions whether to throw exceptions or just return them in the results.
	 * @return results of the checkout
	 * @throws com.elasticpath.commons.exception.InvalidBusinessStateException for all checkout errors to be displayed to user
	 * @throws com.elasticpath.base.exception.EpServiceException for other checkout processing errors
	 */
	CheckoutResults checkout(ShoppingCart shoppingCart, ShoppingCartTaxSnapshot pricingSnapshot, CustomerSession customerSession,
							OrderPayment orderPayment, boolean throwExceptions);

	/**
	 * Retrieve the valid shipping options based on the given shoppingCart, and if the current shipping option is not valid it sets the default one
	 * (as per {@link com.elasticpath.service.shipping.ShippingOptionService#getDefaultShippingOption(java.util.List)},
	 * if any) on the shopping cart.
	 *
	 * @param shoppingCart the current shopping cart.
	 */
	void retrieveShippingOption(ShoppingCart shoppingCart);

	/**
	 * Processes an order for the items in the specified exchange shopping cart.
	 * This will create an order, update customer account information.
	 * Exchange shopping cart is temporary entity that's why it wont be updated to the DB.
	 * Order Payment must be either null which means that no order payments required now or or be fully populated and contain amount to be charged.
	 *
	 * @param exchange the exchange, order exchange is being created for.
	 * @param orderPayment the orderPayment representing the payment detail information. Amount must be specified.
	 * If null, no order processing will be done.
	 * @param awaitExchangeCompletion specifies if physical return required for exchange.
	 * @throws com.elasticpath.commons.exception.InvalidBusinessStateException for all checkout errors to be displayed to user
	 * @throws com.elasticpath.base.exception.EpServiceException for other checkout processing errors
	 */
	void checkoutExchangeOrder(OrderReturn exchange, OrderPayment orderPayment, boolean awaitExchangeCompletion);
}

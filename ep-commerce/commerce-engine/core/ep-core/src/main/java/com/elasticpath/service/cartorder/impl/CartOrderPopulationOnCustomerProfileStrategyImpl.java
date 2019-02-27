/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.cartorder.impl;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.service.cartorder.CartOrderPopulationStrategy;
import com.elasticpath.service.cartorder.CartOrderShippingService;
import com.elasticpath.service.rules.CartOrderCouponAutoApplier;

/**
 * This strategy creates a <code>CartOrder</code>. It fills <code>CartOrder</code>'s billingAddressGuid based on the customer's
 * default billing address, and its paymentMethodGuid property based on the customer's default credit card info.
 */
public class CartOrderPopulationOnCustomerProfileStrategyImpl implements CartOrderPopulationStrategy {

	/** The prototype bean factory. */
	private BeanFactory prototypeBeanFactory;
	
	private CartOrderShippingService cartOrderShippingService;

	private CartOrderCouponAutoApplier cartOrderCouponAutoApplier;

	/**
	 * Creates the <code>CartOrder</code> object, fills the billingAddressGuid property if the customer has a default billing address,
	 * the paymentMethodGuid if the customer has a preferred credit card.
	 *
	 * @param shoppingCart the cart guid
	 * @return the cart order
	 */
	@Override
	public CartOrder createCartOrder(final ShoppingCart shoppingCart) {
		CartOrder cartOrder = prototypeBeanFactory.getBean(ContextIdNames.CART_ORDER);
		cartOrder.setShoppingCartGuid(shoppingCart.getGuid());

		Customer customer = shoppingCart.getShopper().getCustomer();

		// set the default billing address
		CustomerAddress billingAddress = customer.getPreferredBillingAddress();
		if (null != billingAddress) {
			cartOrder.setBillingAddressGuid(billingAddress.getGuid());
		}

		// set the default shipping address
		CustomerAddress shippingAddress = customer.getPreferredShippingAddress();

		if (null != shippingAddress) {
			String shippingAddressGuid = shippingAddress.getGuid();
			cartOrderShippingService.updateCartOrderShippingAddress(shippingAddressGuid, shoppingCart, cartOrder);
		}

		cartOrder = populateDefaultPaymentMethodOnCartOrder(cartOrder, customer);

		return populateCouponsFromProfile(cartOrder, shoppingCart.getStore(), customer.getEmail());
	}

	private CartOrder populateCouponsFromProfile(final CartOrder cartOrder, final Store store, final String customerEmail) {
		cartOrderCouponAutoApplier.filterAndAutoApplyCoupons(cartOrder, store, customerEmail);
		return cartOrder;
	}

	private CartOrder populateDefaultPaymentMethodOnCartOrder(final CartOrder cartOrder, final Customer customer) {
		PaymentMethod defaultPaymentMethod = customer.getPaymentMethods().getDefault();
		
		if (defaultPaymentMethod != null) {
			cartOrder.usePaymentMethod(defaultPaymentMethod);
		}
		return cartOrder;
	}

	/**
	 * Gets the prototype bean factory.
	 *
	 * @return the prototype bean factory
	 */
	protected BeanFactory getPrototypeBeanFactory() {
		return prototypeBeanFactory;
	}

	/**
	 * Sets the prototype bean factory.
	 *
	 * @param prototypeBeanFactory the prototype bean factory
	 */
	public void setPrototypeBeanFactory(final BeanFactory prototypeBeanFactory) {
		this.prototypeBeanFactory = prototypeBeanFactory;
	}

	protected CartOrderShippingService getCartOrderShippingService() {
		return cartOrderShippingService;
	}

	public void setCartOrderShippingService(final CartOrderShippingService cartOrderShippingService) {
		this.cartOrderShippingService = cartOrderShippingService;
	}

	public CartOrderCouponAutoApplier getCartOrderCouponAutoApplier() {
		return cartOrderCouponAutoApplier;
	}

	public void setCartOrderCouponAutoApplier(final CartOrderCouponAutoApplier cartOrderCouponAutoApplier) {
		this.cartOrderCouponAutoApplier = cartOrderCouponAutoApplier;
	}
}

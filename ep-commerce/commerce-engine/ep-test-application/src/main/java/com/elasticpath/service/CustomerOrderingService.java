/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service;

import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.shoppingcart.CheckoutService;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * Interface that provides some methods that deals with business operations during product ordering.
 */
public interface CustomerOrderingService {

	/**
	 * Handles changing shipping address for the shopping cart. Shipping options are reinitialized. If no shipping option could be
	 * found for the shipping address then exception is thrown. If billing address wan't specified then the shipping address will be used as billing
	 * address.
	 * 
	 * @param shopper the shopper
	 * @param shippingAddress shipping address to be selected.
	 */
	void selectShippingAddress(Shopper shopper, CustomerAddress shippingAddress);

	/**
	 * Handles changing billing address for the shopping cart.
	 * 
	 * @param shopper shopper to be updated.
	 * @param billingAddress billing address to be selected.
	 */
	void selectBillingAddress(Shopper shopper, CustomerAddress billingAddress);

	/**
	 * Selects the shipping option and update the shippingCost correspondingly. If thes shipping option selected is not one of the available
	 * shipping options in the shopping cart, then {@link com.elasticpath.domain.EpDomainException} will be thrown.
	 * 
	 * @param shoppingCart shopping cart to be updated.
	 * @param shippingOption - the {@link ShippingOption} to select.
	 * @return updated shopping cart.
	 */
	ShoppingCart selectShippingOption(ShoppingCart shoppingCart, ShippingOption shippingOption);

	/**
	 * Set the checkout service.
	 * 
	 * @param checkoutService the customer service.
	 */
	void setCheckoutService(CheckoutService checkoutService);

	/**
	 * Set the customer service.
	 * 
	 * @param customerService the customer service.
	 */
	void setCustomerService(CustomerService customerService);

}

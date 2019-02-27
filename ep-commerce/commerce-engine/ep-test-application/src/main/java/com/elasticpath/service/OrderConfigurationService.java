/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.service;

import java.util.List;
import java.util.Map;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.shopper.ShopperService;

/**
 * Service to help configure the order creation for <code>OrderFitFixture</code>.
 */
public interface OrderConfigurationService {

	/**
	 * Creates the shopping cart for given customer with given sku.
	 * Shopping cart needs to be associated with a store, which can be different from the customer's store.
	 *
	 * @param store the store to create the shopping cart in
	 * @param customer the customer
	 * @param skuMap the sku to add to shopping cart
	 * @return new shopping cart
	 */
	ShoppingCart createShoppingCart(Store store, Customer customer, Map<ProductSku, Integer> skuMap);

	/**
	 * Selects the customer billing and shipping addresses for given shopping cart.
	 *
	 * @param shopper the shopper
	 * @param streetShippingAddress the street of shipping address
	 * @param streetBillingAddress the street of billing address
	 */
	void selectCustomerAddressesToShoppingCart(Shopper shopper, String streetShippingAddress,
			String streetBillingAddress);

	/**
	 * Selects the shipping option in given shopping cart.
	 *
	 * @param shoppingCart the shoppign cart
	 * @param shippingOptionName the shipping option name to use
	 * @return modified shopping cart
	 */
	ShoppingCart selectShippingOption(ShoppingCart shoppingCart, String shippingOptionName);

	/**
	 * Creates the order payment based on payment token value.
	 *
	 * @param customer   the customer
	 * @param tokenValue the token value
	 * @return the order payment
	 */
	OrderPayment createOrderPayment(Customer customer, String tokenValue);

	/**
	 * Gets the list of orders by customer email.
	 *
	 * @param customerEmail customer email address
	 * @return the list of orders
	 */
	List<Order> getCustomerOrders(String customerEmail);

	/**
	 * Gets the shopper service.
	 *
	 * @param shopperService the shopper service
	 */
	void setShopperService(ShopperService shopperService);

	/**
	 * Gets the shopper service.
	 *
	 * @return the shopper service
	 */
	ShopperService getShopperService();

}

/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.shoppingcart;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;

/**
 * Creates Orders from ShoppingCarts.
 */
public interface  OrderFactory {

	/**
	 * Creates an {@code Order} from the items in a {@code ShoppingCart}.
	 *
	 * @param customer the customer
	 * @param customerSession the customer session
	 * @param shoppingCart the shopping cart
	 * @param isOrderExchange whether this order is created as a result of an Exchange
	 * @param awaitExchangeCompletion whether the order should wait for completion of the exchange before being fulfilled
	 * @return the Order that's created
	 */
	Order createAndPersistNewEmptyOrder(Customer customer, CustomerSession customerSession, ShoppingCart shoppingCart,
										boolean isOrderExchange, boolean awaitExchangeCompletion);

	/**
	 * Fill in the new {@code Order} by the items in a {@code ShoppingCart}.
	 *
	 * @param newOrder the new order created
	 * @param customer the customer
	 * @param customerSession the customer session
	 * @param shoppingCart the shopping cart
	 * @param pricingSnapshot the shopping cart pricing snapshot
	 * @return a new order, populated from the shopping cart.
	 */
	Order fillInNewOrderFromShoppingCart(Order newOrder,
										Customer customer,
										CustomerSession customerSession,
										ShoppingCart shoppingCart,
										ShoppingCartTaxSnapshot pricingSnapshot);

	/**
	 * Fill in the new Exchange {@code Order} by the items in a {@code ShoppingCart}.
	 *
	 * @param newOrder the new order created
	 * @param customer the customer
	 * @param customerSession the customer session
	 * @param shoppingCart the shopping cart
	 * @param pricingSnapshot the shopping cart pricing snapshot
	 * @param awaitExchangeCompletion whether the order should wait for completion of the exchange before being fulfilled
	 * @param exchange the applicable {@code OrderReturn}, if any
	 * @return a new exchange order, populated from the shopping cart.
	 */
	Order fillInNewExchangeOrderFromShoppingCart(Order newOrder,
												Customer customer,
												CustomerSession customerSession,
												ShoppingCart shoppingCart,
												ShoppingCartTaxSnapshot pricingSnapshot,
												boolean awaitExchangeCompletion,
												OrderReturn exchange);

}

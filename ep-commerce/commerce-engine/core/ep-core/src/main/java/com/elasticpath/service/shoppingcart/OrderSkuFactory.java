/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.service.shoppingcart;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItem;

/**
 * Implementations know how to build an {@code OrderSku} from a {@code ShoppingItem}, {@code Customer}, and {@code Store}.
 */
public interface OrderSkuFactory {

	/**
	 * @param cartItems collection of shopping items
	 * @param taxSnapshot the tax-aware pricing snapshot for the cart containing the shopping items
	 * @param locale the locale in which the OrderSku is being purchased
	 * @return collection of OrderSkus
	 */
	Collection<OrderSku> createOrderSkus(Collection<ShoppingItem> cartItems, ShoppingCartTaxSnapshot taxSnapshot, Locale locale);

	/**
	 * Creates an {@link OrderSku} from {@code sku} and {@code quantity}.
	 *
	 * @param sku The sku for which to create the order sku.
	 * @param price the price of the order sku.
	 * @param quantity the quantity
	 * @param ordering the order in which this order sku occurs in the containing {@link com.elasticpath.domain.order.Order Order}
	 * @param itemFields map which contains customizable fields.
	 * @return a new order sku instance
	 */
	OrderSku createOrderSku(ProductSku sku, Price price, int quantity, int ordering, Map<String, String> itemFields);

}

/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.rules;

import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Map;

import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;

/**
 * Executes rules-engine rules on objects passed as parameters to this class.
 */
public interface EpRuleEngine {

	/**
	 * Executes promotion rules for the given products.
	 *
	 * @param products the product list whose promotion price is to be computed
	 * @param activeCurrency the active currency of the shopping context
	 * @param store the {@link Store} the rules will be fired against. The rules will be fired on the catalog related to that store.
	 * @param prices the map of product code to its list of prices
	 */
	void fireCatalogPromotionRules(Collection<? extends Product> products,
									Currency activeCurrency, Store store, Map<String, List<Price>> prices);

	/**
	 * Executes order promotion rules on the specified shopping cart. Only rules affecting
	 * cart item discounts are fired by this method. To fire cart subtotal discount rules call
	 * fireOrderPromotionSubtotalRules.
	 *
	 * @param shoppingCart the cart to which promotion rules are to be applied.
	 * @param customerSession the Customer Session for which promotion rules are to be evaluated.
	 */
	void fireOrderPromotionRules(ShoppingCart shoppingCart, CustomerSession customerSession);

	/**
	 * Executes order promotion rules on the specified shopping cart. Only rules affecting
	 * cart subtotal discounts are fired by this method. To fire cart item discount rules call
	 * fireOrderPromotionRules.
	 *
	 * @param shoppingCart the cart to which promotion rules are to be applied.
	 * @param customerSession the Customer Session for which promotion rules are to be evaluated.
	 */
	void fireOrderPromotionSubtotalRules(ShoppingCart shoppingCart, CustomerSession customerSession);
}

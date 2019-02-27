/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.common.pricing.service;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.shoppingcart.ShoppingItem;

/**
 * Factory to create bundle shopping item price builder.
 *
 */
public interface BundleShoppingItemPriceBuilderFactory {

	/**
	 * Creates a concrete bundle shopping item price builder, according to its bundle type.
	 * @param bundleShoppingItem shopping item, which is a bundle.
	 * @param priceLookupFacade PriceLookupFacade
	 * @param beanFactory BeanFactory
	 * @return ShoppingItemBundlePriceBuilder
	 */
	BundleShoppingItemPriceBuilder createBundleShoppingItemPriceBuilder(
			ShoppingItem bundleShoppingItem,
			PriceLookupFacade priceLookupFacade,
			BeanFactory beanFactory);

}
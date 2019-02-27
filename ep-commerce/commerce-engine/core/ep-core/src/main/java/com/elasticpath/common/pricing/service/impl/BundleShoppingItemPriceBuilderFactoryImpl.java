/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.common.pricing.service.impl;

import com.elasticpath.common.pricing.service.BundleShoppingItemPriceBuilder;
import com.elasticpath.common.pricing.service.BundleShoppingItemPriceBuilderFactory;
import com.elasticpath.common.pricing.service.PriceLookupFacade;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.service.catalog.BundleIdentifier;
import com.elasticpath.service.catalog.ProductSkuLookup;

/**
 * Implementation of BundleShoppingItemPriceBuilderFactory.
 * 
 *
 */
public class BundleShoppingItemPriceBuilderFactoryImpl implements BundleShoppingItemPriceBuilderFactory {
	private BundleIdentifier bundleIdentifier;
	private ProductSkuLookup productSkuLookup;

	@Override
	public BundleShoppingItemPriceBuilder createBundleShoppingItemPriceBuilder(
			final ShoppingItem bundleShoppingItem,
			final PriceLookupFacade priceLookupFacade,
			final BeanFactory beanFactory) {
		if (!bundleShoppingItem.isBundle(getProductSkuLookup())) {
			return null;
		}

		final ProductSku bundleSku = getProductSkuLookup().findByGuid(bundleShoppingItem.getSkuGuid());
		ProductBundle bundle = (ProductBundle) bundleSku.getProduct();

		if (bundle.isCalculated()) {
			CalculatedBundleShoppingItemPriceBuilder priceBuilder = new CalculatedBundleShoppingItemPriceBuilder(
					priceLookupFacade, getProductSkuLookup(), beanFactory);
			priceBuilder.setBundleIdentifier(getBundleIdentifier());
			return priceBuilder;
		}

		return new AssignedBundleShoppingItemPriceBuilder(priceLookupFacade, getProductSkuLookup());
	}

	/**
	 * @return the BundleIdentifier instance
	 */
	protected BundleIdentifier getBundleIdentifier() {
		return bundleIdentifier;
	}
	
	/**
	 * Set the {@link BundleIdentifier} instance.
	 * @param bundleIdentifier the bundleIdentifier instance to set
	 */
	public void setBundleIdentifier(final BundleIdentifier bundleIdentifier) {
		this.bundleIdentifier = bundleIdentifier;
	}

	protected ProductSkuLookup getProductSkuLookup() {
		return productSkuLookup;
	}

	public void setProductSkuLookup(final ProductSkuLookup productSkuLookup) {
		this.productSkuLookup = productSkuLookup;
	}
}

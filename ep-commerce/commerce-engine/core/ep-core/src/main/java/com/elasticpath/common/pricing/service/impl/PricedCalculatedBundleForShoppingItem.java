/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
/**
 * 
 */
package com.elasticpath.common.pricing.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.pricing.PriceProvider;
import com.elasticpath.service.pricing.impl.PricedCalculatedBundle;

/**
 * A calculated product bundle that has been priced as a shopping item.
 */
public class PricedCalculatedBundleForShoppingItem extends PricedCalculatedBundle {
	private final ShoppingItem shoppingItemBundle;
	private final ProductSkuLookup productSkuLookup;

	/**
	 *
	 * @param shoppingItemBundle ShoppingItem bundle.
	 * @param bundle product bundle.
	 * @param beanFactory bean factory.
	 * @param priceProvider price provider.
	 * @param productSkuLookup a product sku lookup
	 */
	public PricedCalculatedBundleForShoppingItem(
			final ShoppingItem shoppingItemBundle,
			final ProductBundle bundle,
			final BeanFactory beanFactory,
			final PriceProvider priceProvider,
			final ProductSkuLookup productSkuLookup) {
		
		super(bundle, beanFactory, priceProvider);
		this.shoppingItemBundle = shoppingItemBundle;
		this.productSkuLookup = productSkuLookup;
	}

	@Override
	protected List<Pair<BundleConstituent, Price>> getSelectedConstituentsAndPrices(
			final List<BundleConstituent> constituents, 
			final int itemsToSelect) {
		
		List<Pair<BundleConstituent, Price>> prices = new ArrayList<>(itemsToSelect);
		Collection<BundleConstituent> selectedConstituents = new ArrayList<>();
		for (int i = 0; i < constituents.size(); ++i) {
			if (isSelected(constituents.get(i))) {
				selectedConstituents.add(constituents.get(i));
			}
		}
		
		for (BundleConstituent item : selectedConstituents) {
			Price price = getConstituentPrice(item);
			if (price == null) {
				return null;
			}
			prices.add(new Pair<>(item, price));
		}
		return prices;
	}

	private boolean isSelected(final BundleConstituent bundleConstituent) {
		for (ShoppingItem item : shoppingItemBundle.getBundleItems(getProductSkuLookup())) {
			if (item.getOrdering() == bundleConstituent.getOrdering()) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected Price getConstituentPrice(final BundleConstituent constituent) {
		
		ShoppingItem item = findChildShoppingItem(constituent);
		ProductSku sku = getProductSkuLookup().findByGuid(item.getSkuGuid());
		return getPriceProvider().getProductSkuPrice(sku);
		
	}

	private ShoppingItem findChildShoppingItem(final BundleConstituent constituent) {

		for (ShoppingItem item : shoppingItemBundle.getBundleItems(getProductSkuLookup())) {
			if (item.getOrdering() == constituent.getOrdering()) {
				return item;
			}
		}

		throw new EpSystemException("Can't find the matching bundle item: " + constituent.getConstituent().getProductSku().getSkuCode());
	}

	protected ProductSkuLookup getProductSkuLookup() {
		return productSkuLookup;
	}
}

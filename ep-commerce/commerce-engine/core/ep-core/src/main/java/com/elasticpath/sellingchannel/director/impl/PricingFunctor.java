/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.sellingchannel.director.impl;

import com.elasticpath.common.pricing.service.PriceLookupFacade;
import com.elasticpath.commons.tree.Functor;
import com.elasticpath.commons.tree.impl.ProductPriceMemento;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalog.ProductSkuLookup;

/**
 * Sets the looked up price to the tree of {@link ShoppingItem}s.
 */
public class PricingFunctor implements Functor<ShoppingItem, ProductPriceMemento> {

	private final PriceLookupFacade facade;
	private final Store store;
	private final Shopper shopper;
	private final ProductSkuLookup productSkuLookup;

	/**
	 * @param facade {@link com.elasticpath.common.pricing.service.PriceLookupFacade}
	 * @param productSkuLookup a {@link com.elasticpath.service.catalog.ProductSkuLookup}
	 * @param store {@link com.elasticpath.domain.store.Store}
	 * @param shopper {@link com.elasticpath.domain.shopper.Shopper}
	 */
	public PricingFunctor(final PriceLookupFacade facade, final ProductSkuLookup productSkuLookup, final Store store,
							final Shopper shopper) {
		this.facade = facade;
		this.productSkuLookup = productSkuLookup;
		this.store = store;
		this.shopper = shopper;
	}

	@Override
	public ProductPriceMemento processNode(
			final ShoppingItem sourceNode,
			final ShoppingItem parentNode,
			final ProductPriceMemento traversalMemento,
			final int level) {
		ProductPriceMemento memento = traversalMemento;
		if (memento == null) {
			memento = new ProductPriceMemento();
		}

		// don't price nested bundles
		if (sourceNode.isBundle(getProductSkuLookup()) && parentNode != null) {
			return memento;
		}

		final Price price = facade.getShoppingItemPrice(sourceNode, store, shopper);
		sourceNode.setPrice(sourceNode.getQuantity(), price);

		final ProductSku productSku = getProductSkuLookup().findByGuid(sourceNode.getSkuGuid());
		memento.add(productSku, price);
		return memento;
	}

	protected ProductSkuLookup getProductSkuLookup() {
		return productSkuLookup;
	}
}

/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.order.impl;

import org.apache.commons.collections.Predicate;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.service.catalog.ProductSkuLookup;

/** 
 * A {@link Predicate} that evaluates whether a {@link ShoppingItem} is a digital asset, not shippable, and not downloadable. 
 */
public class ShoppingItemNonShipmentPredicate implements Predicate {

	private final ProductSkuLookup productSkuLookup;

	/**
	 * ShoppingItemNonShipmentPredicate constructor.
	 * @param productSkuLookup a product sku lookup
	 */
	public ShoppingItemNonShipmentPredicate(final ProductSkuLookup productSkuLookup) {
		this.productSkuLookup = productSkuLookup;
	}

	/**
	 * Checks whether the item is part of the "none" shipment. 
	 *
	 * @param shoppingItem the shopping item
	 * @return true if the item is of "none" shipment type
	 */
	public boolean evaluate(final ShoppingItem shoppingItem) {
		ProductSku sku = getProductSkuLookup().findByGuid(shoppingItem.getSkuGuid());
		return !sku.isShippable() && sku.isDigital() && !sku.isDownloadable();
	}

	@Override
	public boolean evaluate(final Object object) {
		return evaluate((ShoppingItem) object);
	}

	protected ProductSkuLookup getProductSkuLookup() {
		return productSkuLookup;
	}
}

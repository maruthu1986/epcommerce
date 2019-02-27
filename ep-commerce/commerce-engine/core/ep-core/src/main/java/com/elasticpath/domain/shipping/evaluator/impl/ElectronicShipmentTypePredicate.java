/*
 * Copyright (c) Elastic Path Software Inc., 2012.
 */
package com.elasticpath.domain.shipping.evaluator.impl;

import org.apache.commons.collections.Predicate;

import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.service.catalog.ProductSkuLookup;

/**
 * A {@link Predicate} that evaluates whether a {@link ShoppingItem} is an electronic shipment type.
 */
public class ElectronicShipmentTypePredicate implements Predicate {

	private final ProductSkuLookup productSkuLookup;

	/**
	 * Constructs the predicate.
	 *
	 * @param productSkuLookup a product sku lookup
	 */
	public ElectronicShipmentTypePredicate(final ProductSkuLookup productSkuLookup) {
		this.productSkuLookup = productSkuLookup;
	}

	/**
	 * Evaluate that the object is a ShoppingItem and that it is an electronic shipment type.
	 *
	 * @param obj the object to be evaluated, expecting a {@link ShoppingItem}
	 * @return true if the object is a shopping item and is an electronic shipment type
	 */
	@Override
	public boolean evaluate(final Object obj) {
		return !((ShoppingItem) obj).isShippable(productSkuLookup);
	}

}

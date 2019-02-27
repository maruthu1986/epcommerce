/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.shoppingcart.impl;

import java.util.Objects;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.NullPredicate;

import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.service.catalog.ProductSkuLookup;

/**
 * Predicate that checks if a given object matches a specific shopping item.
 */
public class MatchingShoppingItemPredicate implements Predicate {

	private final ShoppingItem comparisonItem;
	private final ProductSkuLookup productSkuLookup;

	/**
	 * Instantiates a new matching shopping item predicate.
	 *
	 * @param comparisonItem the comparison item
	 * @param productSkuLookup the product sku lookup
	 */
	public MatchingShoppingItemPredicate(final ShoppingItem comparisonItem, final ProductSkuLookup productSkuLookup) {
		this.comparisonItem = comparisonItem;
		this.productSkuLookup = productSkuLookup;
	}

	/**
	 * Gets the single instance of MatchingShoppingItemPredicate.
	 *
	 * @param comparisonItem the comparison item
	 * @param productSkuLookup a product sku lookup
	 * @return single instance of MatchingShoppingItemPredicate
	 */
	public static Predicate getInstance(final ShoppingItem comparisonItem, final ProductSkuLookup productSkuLookup) {
		if (comparisonItem == null) {
			return NullPredicate.INSTANCE;
		}
		return new MatchingShoppingItemPredicate(comparisonItem, productSkuLookup);
	}

	@Override
	public boolean evaluate(final Object object) {
		if (!(object instanceof ShoppingItem)) {
			return false;
		}

		ShoppingItem shoppingItem = (ShoppingItem) object;

		return !shoppingItem.isGiftCertificate(getProductSkuLookup()) && itemsAreEqual(comparisonItem, shoppingItem);

	}

	private boolean itemsAreEqual(final ShoppingItem shoppingItem, final ShoppingItem existingItem) {
		if (existingItem.isMultiSku(productSkuLookup)) {
			return shoppingItem.isSameMultiSkuItem(productSkuLookup, existingItem);
		} else if (shoppingItem.isConfigurable(productSkuLookup)) {
			return shoppingItem.isSameConfigurableItem(productSkuLookup, existingItem);
		}
		return Objects.equals(shoppingItem.getSkuGuid(), existingItem.getSkuGuid());
	}

	protected ProductSkuLookup getProductSkuLookup() {
		return productSkuLookup;
	}
}

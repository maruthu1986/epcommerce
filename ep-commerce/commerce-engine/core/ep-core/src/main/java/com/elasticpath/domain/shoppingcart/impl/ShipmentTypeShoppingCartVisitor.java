/*
 * Copyright (c) Elastic Path Software Inc., 2011.
 */
package com.elasticpath.domain.shoppingcart.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.elasticpath.domain.shoppingcart.ShipmentTypeCollections;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartVisitor;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.order.impl.ShoppingItemHasRecurringPricePredicate;

/**
 * This class is used to determine which of three lists a shopping cart's items belong to: service, physical, or electronic.
 */
public class ShipmentTypeShoppingCartVisitor implements ShoppingCartVisitor, ShipmentTypeCollections {
	
	private final List<ShoppingItem> electronicSkus;
	private final List<ShoppingItem> physicalSkus;
	private final List<ShoppingItem> serviceSkus;
	private final ShoppingItemHasRecurringPricePredicate recurringPricePredicate;
	private final ProductSkuLookup productSkuLookup;

	/**
	 * Constructor.
	 * @param predicate Used to determine if an item has a recurring price.
	 * @param productSkuLookup a product sku lookup
	 */
	public ShipmentTypeShoppingCartVisitor(final ShoppingItemHasRecurringPricePredicate predicate, final ProductSkuLookup productSkuLookup) {
		this.electronicSkus = new ArrayList<>();
		this.physicalSkus = new ArrayList<>();
		this.serviceSkus = new ArrayList<>();
		this.recurringPricePredicate = predicate;
		this.productSkuLookup = productSkuLookup;
	}
	
	@Override
	public List<ShoppingItem> getElectronicSkus() {
		return Collections.unmodifiableList(electronicSkus);
	}
	
	@Override
	public List<ShoppingItem> getPhysicalSkus() {
		return Collections.unmodifiableList(physicalSkus);
	}
	
	@Override
	public List<ShoppingItem> getServiceSkus() {
		return Collections.unmodifiableList(serviceSkus);
	}
	
	/**
	 * Does nothing.
	 * {@inheritDoc}
	 */
	@Override
	public void visit(final ShoppingCart cart) {
		// Does nothing.
	}

	/**
	 * If the given item is a bundle then it is ignored.
	 * Otherwise the item is added to one of the three lists: service, physical, or electronic.
	 * {@inheritDoc}
	 */
	@Override
	public void visit(final ShoppingItem item, final ShoppingItemPricingSnapshot pricingSnapshot) {
		if (item.isBundle(getProductSkuLookup())) {
			return;
		}

		if (recurringPricePredicate.test(pricingSnapshot)) {
			serviceSkus.add(item);
		} else if (item.isShippable(getProductSkuLookup())) {
			physicalSkus.add(item);
		} else {
			electronicSkus.add(item);
		}
	}

	protected ProductSkuLookup getProductSkuLookup() {
		return productSkuLookup;
	}
}

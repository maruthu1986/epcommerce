/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers.visitors.extensibility.impl;

import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.service.shipping.ShippableItemPricing;
import com.elasticpath.service.shipping.transformers.visitors.PricedShippableItemPopulatorVisitor;
import com.elasticpath.shipping.connectivity.dto.builder.populators.PricedShippableItemBuilderPopulator;

/**
 * An abstract class that can be used to populate any project-specific extension to
 * {@link com.elasticpath.shipping.connectivity.dto.PricedShippableItem} using a custom populator class.
 *
 * Subtypes of this class are designed to injectable into
 * {@link com.elasticpath.service.shipping.transformers.impl.PricedShippableItemTransformerImpl} (see
 * {@link com.elasticpath.service.shipping.transformers.impl.PricedShippableItemTransformerImpl#getPricedVisitors()}).
 *
 * This allows the Transformer above to be composed with additional population visitors to populate any fields added by the extension to
 * {@link com.elasticpath.shipping.connectivity.dto.PricedShippableItem} without needing to modify it.
 *
 * Composition is used here because subclassing and decorating the standard Transformer above to populate any extension fields is a lot more tricky
 * due to the class hierarchy of {@link com.elasticpath.shipping.connectivity.dto.ShippableItem},
 * {@link com.elasticpath.shipping.connectivity.dto.PricedShippableItem}, their potential corresponding extension sub-types.
 *
 * Therefore an injectable Visitor pattern was used in the Transformer above to simplify project-specific populators, and this abstract class
 * further simplifies custom populators by hiding the casting and providing a subtype-specific extension point to implement. Whenever casting is
 * involved it's not perfect but it's surprisingly hard to provide a perfect extensibility model here.
 *
 * @param <P> type of extension Populator used by sub-classes to populate the extension fields.
 */
public abstract class AbstractExtPricedShippableItemPopulatorVisitorImpl<P extends PricedShippableItemBuilderPopulator>
		implements PricedShippableItemPopulatorVisitor {
	@Override
	@SuppressWarnings("unchecked")
	public void accept(final ShoppingItem shoppingItem, final ShippableItemPricing shippableItemPricing,
					   final PricedShippableItemBuilderPopulator populator) {
		populateExtension(shoppingItem, shippableItemPricing, (P) populator);
	}

	/**
	 * Extension point for sub-classes to implement to populate any additional fields provided by the extension to
	 * {@link com.elasticpath.shipping.connectivity.dto.PricedShippableItem}.
	 *
	 * It can also override previously set values on the populator as necessary.
	 *
	 * @param shoppingItem the shopping item being used to generate the corresponding priced shippable item.
	 * @param shippableItemPricing the pricing information being used to generate the corresponding priced shippable item.
	 * @param populator the populater used to populate the shippable item being generated.
	 */
	protected abstract void populateExtension(ShoppingItem shoppingItem, ShippableItemPricing shippableItemPricing, P populator);
}

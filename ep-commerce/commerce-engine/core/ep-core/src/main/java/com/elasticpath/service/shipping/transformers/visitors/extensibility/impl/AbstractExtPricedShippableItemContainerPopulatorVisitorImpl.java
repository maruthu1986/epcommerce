/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers.visitors.extensibility.impl;

import java.util.Collection;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.shipping.transformers.visitors.PricedShippableItemContainerPopulatorVisitor;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItem;
import com.elasticpath.shipping.connectivity.dto.builder.populators.PricedShippableItemContainerBuilderPopulator;

/**
 * An abstract class that can be used to populate any project-specific extension to
 * {@link com.elasticpath.shipping.connectivity.dto.PricedShippableItemContainer} using a custom populator class.
 *
 * Subtypes of this class are designed to injectable into
 * {@link com.elasticpath.service.shipping.transformers.impl.BaseShippableItemContainerTransformerImpl} which in particular services
 * {@link com.elasticpath.service.shipping.transformers.impl.PricedShippableItemContainerTransformerImpl}
 * (see {@link com.elasticpath.service.shipping.transformers.impl.BaseShippableItemContainerTransformerImpl#getItemSpecificVisitors()}).
 *
 * This allows the Transformer above to be composed with additional population visitors to populate any fields added by the extension to
 * {@link com.elasticpath.shipping.connectivity.dto.PricedShippableItemContainer} without needing to modify them.
 *
 * Composition is used here because subclassing and decorating the standard Transformers above to populate any extension fields is a lot more tricky
 * due to the class hierarchy of {@link com.elasticpath.shipping.connectivity.dto.ShippableItemContainer},
 * {@link com.elasticpath.shipping.connectivity.dto.PricedShippableItemContainer}, and their potential corresponding extension subtypes.
 * As well as the fact they use a generic type to describe and protect what shippable items they contain.
 *
 * Therefore an injectable Visitor pattern was used in the Transformers above to simplify project-specific populators, and this abstract class
 * further simplifies custom populators by hiding the casting and providing a subtype-specific extension point to implement. Whenever casting is
 * involved it's not perfect but it's surprisingly hard to provide a perfect extensibility model here.
 *
 * @param <I> type of extension {@link PricedShippableItem} that the container being created contains.
 * @param <P> type of extension Populator used by sub-classes to populate the extension fields.
 */
public abstract class AbstractExtPricedShippableItemContainerPopulatorVisitorImpl<I extends PricedShippableItem,
																				  P extends PricedShippableItemContainerBuilderPopulator<?>>
		implements PricedShippableItemContainerPopulatorVisitor {
	@Override
	@SuppressWarnings("unchecked")
	public void accept(final ShoppingCart shoppingCart, final Collection<PricedShippableItem> shippableItems,
					   final PricedShippableItemContainerBuilderPopulator<PricedShippableItem> populator) {
		populateExtension(shoppingCart, (Collection<I>) shippableItems, (P) populator);
	}

	/**
	 * Extension point for sub-classes to implement to populate any additional fields provided by the extension to
	 * {@link com.elasticpath.shipping.connectivity.dto.PricedShippableItemContainer}.
	 *
	 * It can also override previously set values on the populator as necessary.
	 *
	 * @param shoppingCart the shopping cart being used to generate the shippable item container.
	 * @param shippableItems the shippable items being used to generate the shippable item container.
	 * @param populator the populater used to populate the shippable item container being generated.
	 */
	protected abstract void populateExtension(ShoppingCart shoppingCart, Collection<I> shippableItems, P populator);
}

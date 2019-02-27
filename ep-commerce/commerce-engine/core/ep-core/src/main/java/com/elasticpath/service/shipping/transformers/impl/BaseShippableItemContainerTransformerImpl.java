/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers.impl;

import static com.elasticpath.base.util.StreamUtils.toImmutableList;
import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.shipping.transformers.BaseShippableItemContainerTransformer;
import com.elasticpath.service.shipping.transformers.visitors.ShippableItemContainerPopulatorVisitor;
import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.ShippableItemContainer;
import com.elasticpath.shipping.connectivity.dto.builder.Builder;
import com.elasticpath.shipping.connectivity.dto.builder.populators.BaseShippableItemContainerBuilderPopulator;

/**
 * Generic implementation adapting {@link ShoppingCart} and {@link ShippableItem} collection to extension of {@link ShippableItemContainer}.
 *
 * @param <I> interface type of the container being transformed into, extension of {@link ShippableItemContainer}.
 * @param <E> element type that the generated transformer contains, extension of {@link ShippableItem}.
 * @param <P> the of Populator used by injected visitors to populate the Builder.
 * @param <B> type of Builder used to construct the container being transformed into.
 */
public class BaseShippableItemContainerTransformerImpl<I extends ShippableItemContainer<E>,
													   E extends ShippableItem,
													   P extends BaseShippableItemContainerBuilderPopulator,
													   B extends Builder<I, P>>
	implements BaseShippableItemContainerTransformer<I, E>  {

	private Supplier<B> supplier;
	private List<ShippableItemContainerPopulatorVisitor<ShippableItem, BaseShippableItemContainerBuilderPopulator>> baseVisitors;
	private List<ShippableItemContainerPopulatorVisitor<E, P>> itemSpecificVisitors;

	/**
	 * Adapt {@link ShoppingCart} and {@link ShippableItem} to extension of {@link ShippableItemContainer} with populated fields.
	 *
	 * @param shoppingCart   the shopping cart
	 * @param shippableItems the shippable items
	 * @return the extension of shippable item container.
	 */
	@Override
	public I apply(final ShoppingCart shoppingCart, final Stream<E> shippableItems) {
		requireNonNull(shoppingCart, "Shopping Cart is required.");
		requireNonNull(shippableItems, "Shippable Items Stream is required, but can be empty.");

		final B builder = getSupplier().get();
		final P populator = builder.getPopulator();

		// Enforce an immutable list so visitors can't add/remove elements just read
		final Collection<E> shippableItemsCollection = shippableItems.collect(toImmutableList());

		visitPopulator(populator, shoppingCart, shippableItemsCollection);

		return builder.build();
	}

	/**
	 * Visits the given Populator with the given shopping cart and items. This method invokes each Visitor provided by both
	 * {@link #getBaseVisitors()} and {@link #getItemSpecificVisitors()}.
	 *
	 * @param populator the Populator to visit.
	 * @param shoppingCart the shopping cart to pass to the visitors.
	 * @param shippableItems the shippable items to pass to the visitors.
	 */
	@SuppressWarnings("unchecked")
	protected void visitPopulator(final P populator, final ShoppingCart shoppingCart, final Collection<E> shippableItems) {
		Optional.ofNullable(getBaseVisitors())
				.ifPresent(visitors -> visitors.forEach(consumer -> consumer.accept(shoppingCart,
																					(Collection<ShippableItem>) shippableItems,
																					populator)));

		Optional.ofNullable(getItemSpecificVisitors())
				.ifPresent(visitors -> visitors.forEach(consumer -> consumer.accept(shoppingCart, shippableItems, populator)));
	}

	protected Supplier<B> getSupplier() {
		return this.supplier;
	}

	public void setSupplier(final Supplier<B> supplier) {
		this.supplier = supplier;
	}

	protected List<ShippableItemContainerPopulatorVisitor<ShippableItem, BaseShippableItemContainerBuilderPopulator>> getBaseVisitors() {
		return this.baseVisitors;
	}

	public void setBaseVisitors(final List<ShippableItemContainerPopulatorVisitor<ShippableItem,
										   BaseShippableItemContainerBuilderPopulator>> baseVisitors) {
		this.baseVisitors = baseVisitors;
	}

	protected List<ShippableItemContainerPopulatorVisitor<E, P>> getItemSpecificVisitors() {
		return this.itemSpecificVisitors;
	}

	public void setItemSpecificVisitors(final List<ShippableItemContainerPopulatorVisitor<E, P>> itemSpecificVisitors) {
		this.itemSpecificVisitors = itemSpecificVisitors;
	}
}

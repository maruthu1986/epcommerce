/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers.impl;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Currency;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.service.shipping.ShippableItemsPricing;
import com.elasticpath.service.shipping.impl.ShippableItemsPricingImpl;
import com.elasticpath.service.shipping.transformers.PricedShippableItemContainerFromOrderShipmentTransformer;
import com.elasticpath.service.shipping.transformers.PricedShippableItemsTransformer;
import com.elasticpath.service.shipping.transformers.ShippingAddressTransformer;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItem;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItemContainer;
import com.elasticpath.shipping.connectivity.dto.builder.PricedShippableItemContainerBuilder;

/**
 * Implementation of {@link PricedShippableItemContainerFromOrderShipmentTransformer}.
 */
public class PricedShippableItemContainerFromOrderShipmentTransformerImpl
		implements PricedShippableItemContainerFromOrderShipmentTransformer<PricedShippableItem> {

	private Supplier<PricedShippableItemContainerBuilder> supplier;
	private ShippingAddressTransformer shippingAddressTransformer;
	private PricedShippableItemsTransformer pricedShippableItemsTransformer;

	@Override
	public PricedShippableItemContainer<PricedShippableItem> apply(final PhysicalOrderShipment orderShipment) {
		final Order order = orderShipment.getOrder();
		requireNonNull(order, format("The top-level Order must be set on the PhysicalOrderShipment passed in. Shipment number: %s.",
									 orderShipment.getShipmentNumber()));

		final PricedShippableItemContainerBuilder builder = getSupplier().get();

		builder.getPopulator()
				.withShippableItems(createShippableItems(orderShipment))
				.withDestinationAddress(shippingAddressTransformer.apply(orderShipment.getShipmentAddress()))
				.withStoreCode(order.getStoreCode())
				.withLocale(order.getLocale())
				.withCurrency(order.getCurrency());

		return builder.build();
	}

	/**
	 * Returns a collection of {@link PricedShippableItem} for all {@link OrderSku} objects in the given {@link PhysicalOrderShipment}.
	 * Delegates to a {@link PricedShippableItemsTransformer} to do this transformation.
	 *
	 * @param orderShipment the shipment to generate the corresponding {@link PricedShippableItem} collection.
	 * @return a collection of {@link PricedShippableItem} for all {@link OrderSku} objects in the given {@link PhysicalOrderShipment}.
	 */
	protected Collection<PricedShippableItem> createShippableItems(final PhysicalOrderShipment orderShipment) {
		final ShippableItemsPricing shippableItemsPricing = createShippableItemsPricing(orderShipment);

		return getPricedShippableItemsTransformer()
				.apply(orderShipment.getShipmentOrderSkus(), shippableItemsPricing)
				.collect(Collectors.toList());
	}

	/**
	 * Factory method to create a {@link ShippableItemsPricing} object for the given {@link PhysicalOrderShipment}.
	 *
	 * @param orderShipment the order shipment to create a corresponding {@link ShippableItemsPricing} object.
	 * @return a {@link ShippableItemsPricing} object for the given {@link PhysicalOrderShipment}.
	 */
	protected ShippableItemsPricing createShippableItemsPricing(final PhysicalOrderShipment orderShipment) {
		final Currency currency = orderShipment.getOrder().getCurrency();
		final Money subtotalDiscount = orderShipment.getSubtotalDiscountMoney();
		final Function<ShoppingItem, ShoppingItemPricingSnapshot> pricingFunction = orderSku -> getShoppingItemPricingSnapshot((OrderSku) orderSku);

		return new ShippableItemsPricingImpl(currency, subtotalDiscount, pricingFunction);
	}

	/**
	 * Returns the corresponding {@link ShoppingItemPricingSnapshot} for the given {@link OrderSku}.
	 * This currently just casts the given {@link OrderSku} as a {@link ShoppingItemPricingSnapshot} since it currently directly implements
	 * that interface.
	 *
	 * @param orderSku the {@link OrderSku} to get the corresponding {@link ShoppingItemPricingSnapshot}.
	 * @return the {@link OrderSku} cast as a {@link ShoppingItemPricingSnapshot}.
	 */
	@SuppressWarnings("unchecked")
	protected ShoppingItemPricingSnapshot getShoppingItemPricingSnapshot(final OrderSku orderSku) {
		return (ShoppingItemPricingSnapshot) orderSku;
	}

	protected Supplier<PricedShippableItemContainerBuilder> getSupplier() {
		return this.supplier;
	}

	public void setSupplier(final Supplier<PricedShippableItemContainerBuilder> supplier) {
		this.supplier = supplier;
	}

	protected ShippingAddressTransformer getShippingAddressTransformer() {
		return this.shippingAddressTransformer;
	}

	public void setShippingAddressTransformer(final ShippingAddressTransformer shippingAddressTransformer) {
		this.shippingAddressTransformer = shippingAddressTransformer;
	}

	protected PricedShippableItemsTransformer getPricedShippableItemsTransformer() {
		return this.pricedShippableItemsTransformer;
	}

	public void setPricedShippableItemsTransformer(final PricedShippableItemsTransformer pricedShippableItemsTransformer) {
		this.pricedShippableItemsTransformer = pricedShippableItemsTransformer;
	}
}

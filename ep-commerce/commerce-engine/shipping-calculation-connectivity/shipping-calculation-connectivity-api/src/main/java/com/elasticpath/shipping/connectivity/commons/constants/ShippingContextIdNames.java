/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */

package com.elasticpath.shipping.connectivity.commons.constants;

/**
 * Shipping Calculation bean id constants.
 */
public final class ShippingContextIdNames {

	/**
	 * Bean id for {@link com.elasticpath.shipping.connectivity.dto.impl.ShippingOptionImpl}.
	 */
	public static final String SHIPPING_OPTION = "shippingOption";

	/**
	 * Bean id for {@link com.elasticpath.shipping.connectivity.dto.impl.ShippingCalculationResultImpl}.
	 */
	public static final String SHIPPING_CALCULATION_RESULT = "shippingCalculationResult";

	/**
	 * Bean id for {@link com.elasticpath.shipping.connectivity.dto.impl.ShippingAddressImpl}.
	 */
	public static final String SHIPPING_ADDRESS = "shippingAddress";

	/**
	 * Bean id for {@link com.elasticpath.shipping.connectivity.dto.impl.ShippableItemImpl}.
	 */
	public static final String UNPRICED_SHIPPABLE_ITEM = "unpricedShippableItem";

	/**
	 * Bean id for {@link com.elasticpath.shipping.connectivity.dto.impl.PricedShippableItemImpl}.
	 */
	public static final String PRICED_SHIPPABLE_ITEM = "pricedShippableItem";

	/**
	 * Bean id for {@link com.elasticpath.shipping.connectivity.dto.impl.ShippableItemContainerImpl}.
	 */
	public static final String UNPRICED_SHIPPABLE_ITEM_CONTAINER = "unpricedShippableItemContainer";

	/**
	 * Bean id for {@link com.elasticpath.shipping.connectivity.dto.impl.PricedShippableItemContainerImpl}.
	 */
	public static final String PRICED_SHIPPABLE_ITEM_CONTAINER = "pricedShippableItemContainer";

	/**
	 * Bean id for {@link com.elasticpath.shipping.connectivity.dto.builder.impl.ShippingOptionBuilderImpl}.
	 */
	public static final String SHIPPING_OPTION_BUILDER = "shippingOptionBuilder";

	/**
	 * Bean id for {@link com.elasticpath.shipping.connectivity.dto.builder.impl.ShippingCalculationResultBuilderImpl}.
	 */
	public static final String SHIPPING_CALCULATION_RESULT_BUILDER = "shippingCalculationResultBuilder";

	/**
	 * Bean id for {@link com.elasticpath.shipping.connectivity.dto.builder.impl.ShippingAddressBuilderImpl}.
	 */
	public static final String SHIPPING_ADDRESS_BUILDER = "shippingAddressBuilder";

	/**
	 * Bean id for {@link com.elasticpath.shipping.connectivity.dto.builder.impl.ShippableItemBuilderImpl}.
	 */
	public static final String UNPRICED_SHIPPABLE_ITEM_BUILDER = "unpricedShippableItemBuilder";

	/**
	 * Bean id for {@link com.elasticpath.shipping.connectivity.dto.builder.impl.PricedShippableItemBuilderImpl}.
	 */
	public static final String PRICED_SHIPPABLE_ITEM_BUILDER = "pricedShippableItemBuilder";

	/**
	 * Bean id for {@link  com.elasticpath.shipping.connectivity.dto.builder.impl.ShippableItemContainerBuilderImpl}.
	 */
	public static final String UNPRICED_SHIPPABLE_ITEM_CONTAINER_BUILDER = "unpricedShippableItemContainerBuilder";

	/**
	 * Bean id for {@link  com.elasticpath.shipping.connectivity.dto.builder.impl.PricedShippableItemContainerBuilderImpl}.
	 */
	public static final String PRICED_SHIPPABLE_ITEM_CONTAINER_BUILDER = "pricedShippableItemContainerBuilder";

	/**
	 * Bean id for supplier of {@link com.elasticpath.shipping.connectivity.dto.builder.impl.ShippingOptionBuilderImpl}.
	 */
	public static final String SHIPPING_OPTION_BUILDER_SUPPLIER = "shippingOptionBuilderSupplier";

	/**
	 * Bean id for supplier of {@link com.elasticpath.shipping.connectivity.dto.builder.impl.ShippingCalculationResultBuilderImpl}.
	 */
	public static final String SHIPPING_CALCULATION_RESULT_BUILDER_SUPPLIER = "shippingCalculationResultBuilderSupplier";

	/**
	 * Bean id for supplier of {@link com.elasticpath.shipping.connectivity.dto.builder.impl.ShippingAddressBuilderImpl}.
	 */
	public static final String SHIPPING_ADDRESS_BUILDER_SUPPLIER = "shippingAddressBuilderSupplier";

	/**
	 * Bean id for supplier of {@link com.elasticpath.shipping.connectivity.dto.builder.impl.ShippableItemBuilderImpl}.
	 */
	public static final String UNPRICED_SHIPPABLE_ITEM_BUILDER_SUPPLIER = "unpricedShippableItemBuilderSupplier";

	/**
	 * Bean id for supplier of {@link com.elasticpath.shipping.connectivity.dto.builder.impl.PricedShippableItemBuilderImpl}.
	 */
	public static final String PRICED_SHIPPABLE_ITEM_BUILDER_SUPPLIER = "pricedShippableItemBuilderSupplier";

	/**
	 * Bean id for supplier of {@link  com.elasticpath.shipping.connectivity.dto.builder.impl.ShippableItemContainerBuilderImpl}.
	 */
	public static final String UNPRICED_SHIPPABLE_ITEM_CONTAINER_BUILDER_SUPPLIER = "unpricedShippableItemContainerBuilderSupplier";

	/**
	 * Bean id for supplier of {@link  com.elasticpath.shipping.connectivity.dto.builder.impl.PricedShippableItemContainerBuilderImpl}.
	 */
	public static final String PRICED_SHIPPABLE_ITEM_CONTAINER_BUILDER_SUPPLIER = "pricedShippableItemContainerBuilderSupplier";

	/**
	 * bean id for {@link com.elasticpath.shipping.connectivity.service.impl.ShippingCalculationServiceImpl}.
	 */
	public static final String SHIPPING_CALCULATION_SERVICE = "shippingCalculationService";

	/**
	 * bean id alias for {@link com.elasticpath.shipping.connectivity.service.selector.impl.StaticUnpricedShippingCalculationPluginSelectorImpl}.
	 */
	public static final String UNPRICED_SHIPPING_CALCULATION_PLUGIN_SELECTOR = "unpricedShippingCalculationPluginSelector";

	/**
	 * bean id alias for {@link com.elasticpath.shipping.connectivity.service.selector.impl.StaticPricedShippingCalculationPluginSelectorImpl}.
	 */
	public static final String PRICED_SHIPPING_CALCULATION_PLUGIN_SELECTOR = "pricedShippingCalculationPluginSelector";

	/**
	 * bean id alias for {@link java.util.List} of unpriced shipping calculation plugin.
	 */
	public static final String UNPRICED_SHIPPING_CALCULATION_PLUGIN_LIST = "unpricedShippingCalculationPluginList";

	/**
	 * bean id alias {@link java.util.List} of priced shipping calculation plugin.
	 */
	public static final String PRICED_SHIPPING_CALCULATION_PLUGIN_LIST = "pricedShippingCalculationPluginList";

	/**
	 * bean id alias for {@link com.elasticpath.shipping.connectivity.service.impl.ShippingCalculationServiceImpl}.
	 */
	public static final String NON_CACHING_SHIPPING_CALCULATION_SERVICE = "nonCachingShippingCalculationService";

	private ShippingContextIdNames() {
		// to hide the implicit public one
		// do nothing
	}

}

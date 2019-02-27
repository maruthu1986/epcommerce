/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.dto.builder.populators;

import java.math.BigDecimal;

/**
 * Interface defining population methods for building a {@link com.elasticpath.shipping.connectivity.dto.ShippableItem}.
 */
public interface ShippableItemBuilderPopulator {

	/**
	 * Sets the guid of sku.
	 *
	 * @param skuGuid the sku guid.
	 * @return this populator.
	 */
	ShippableItemBuilderPopulator withSkuGuid(String skuGuid);

	/**
	 * @return currently populated sku guid.
	 */
	String getSkuGuid();

	/**
	 * Sets the quantity of item.
	 *
	 * @param quantity the quantity of item.
	 * @return this populator.
	 */
	ShippableItemBuilderPopulator withQuantity(int quantity);

	/**
	 * @return currently populated item quantity.
	 */
	int getQuantity();

	/**
	 * Sets the weight of sku.
	 *
	 * @param weight the sku weight
	 * @return this populator.
	 */
	ShippableItemBuilderPopulator withWeight(BigDecimal weight);

	/**
	 * @return currently populated sku weight.
	 */
	BigDecimal getWeight();

	/**
	 * Sets the height of the sku.
	 *
	 * @param height the sku height
	 * @return this populator.
	 */
	ShippableItemBuilderPopulator withHeight(BigDecimal height);

	/**
	 * @return currently populated sku height.
	 */
	BigDecimal getHeight();

	/**
	 * Sets the width of the sku.
	 *
	 * @param width the sku width
	 * @return this populator.
	 */
	ShippableItemBuilderPopulator withWidth(BigDecimal width);

	/**
	 * @return currently populated sku width.
	 */
	BigDecimal getWidth();

	/**
	 * Sets the length of sku.
	 *
	 * @param length the sku length
	 * @return this populator.
	 */
	ShippableItemBuilderPopulator withLength(BigDecimal length);

	/**
	 * @return currently populated sku length.
	 */
	BigDecimal getLength();
}

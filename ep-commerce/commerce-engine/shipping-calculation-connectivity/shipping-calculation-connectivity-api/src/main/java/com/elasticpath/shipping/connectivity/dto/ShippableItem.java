/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.dto;

import java.math.BigDecimal;

/**
 * Interface defining fundamental information of an item which is also able to ship.
 */
public interface ShippableItem {

	/**
	 * Gets the sku guid.
	 *
	 * @return the sku guid.
	 */
	String getSkuGuid();

	/**
	 * Gets the weight of the item.
	 *
	 * @return the weight.
	 */
	BigDecimal getWeight();

	/**
	 * Gets the Height of the item.
	 *
	 * @return the height.
	 */
	BigDecimal getHeight();

	/**
	 * Gets the width of the item.
	 *
	 * @return the width.
	 */
	BigDecimal getWidth();

	/**
	 * Gets the length of the item.
	 *
	 * @return the length.
	 */
	BigDecimal getLength();

	/**
	 * Gets the quantity of the item.
	 *
	 * @return the quantity.
	 */
	int getQuantity();
}

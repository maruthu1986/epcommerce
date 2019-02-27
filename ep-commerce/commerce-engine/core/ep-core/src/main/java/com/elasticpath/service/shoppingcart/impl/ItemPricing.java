/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.service.shoppingcart.impl;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Keeps line item price and discount amounts.
 */
public class ItemPricing {
	private final BigDecimal price;

	private final BigDecimal discount;

	private final int quantity;

	/**
	 * @param price line item price
	 * @param discount line item discount
	 * @param quantity the quantity
	 */
	public ItemPricing(final BigDecimal price, final BigDecimal discount, final int quantity) {
		this.price = price;
		this.discount = discount;
		this.quantity = quantity;
	}

	/**
	 * @return the line price
	 */
	public BigDecimal getPrice() {
		return price;
	}

	/**
	 * @return the line discount
	 */
	public BigDecimal getDiscount() {
		return discount;
	}

	/**
	 * @return the quantity
	 */
	public int getQuantity() {
		return quantity;
	}

	@Override
	public int hashCode() {
		return Objects.hash(price, discount, quantity);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		ItemPricing other = (ItemPricing) obj;

		return Objects.equals(price, other.price)
			&& Objects.equals(discount, other.discount)
			&& quantity == other.quantity;
	}
}
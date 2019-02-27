/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.dto.impl;

import java.io.Serializable;
import java.util.Objects;

import com.elasticpath.money.Money;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItem;

/**
 * Implements {@link PricedShippableItem} to represent shippable item with price.
 */
public class PricedShippableItemImpl extends ShippableItemImpl implements PricedShippableItem, Serializable {

	private static final long serialVersionUID = 5000000001L;

	private Money unitPrice;
	private Money totalPrice;

	@Override
	public Money getUnitPrice() {
		return this.unitPrice;
	}

	public void setUnitPrice(final Money unitPrice) {
		this.unitPrice = unitPrice;
	}

	@Override
	public Money getTotalPrice() {
		return this.totalPrice;
	}

	public void setTotalPrice(final Money totalPrice) {
		this.totalPrice = totalPrice;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof PricedShippableItemImpl)) {
			return false;
		}

		final PricedShippableItemImpl other = (PricedShippableItemImpl) obj;
		return Objects.equals(getUnitPrice(), other.getUnitPrice())
				&& Objects.equals(getTotalPrice(), other.getTotalPrice())
				&& super.equals(obj);
	}

	@Override
	public int hashCode() {
		return Objects.hash(getUnitPrice(), getTotalPrice(), super.hashCode());
	}
}

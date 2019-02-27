/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.price.impl;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.elasticpath.money.Money;

/**
 * Transfer object for the lineitem price amounts.
 */
public class MoneyWrapper {
	
	private Money listPrice, purchasePrice;

	/**
	 * Getter.
	 *
	 * @return list price
	 */
	public Money getListPrice() {
		return listPrice;
	}

	/**
	 * Setter.
	 *
	 * @param listPrice the list price
	 * @return this instance
	 */
	public MoneyWrapper setListPrice(final Money listPrice) {
		this.listPrice = listPrice;
		return this;
	}

	/**
	 * Getter.
	 *
	 * @return purchase price.
	 */
	public Money getPurchasePrice() {
		return purchasePrice;
	}

	/**
	 * Setter.
	 *
	 * @param purchasePrice purchase price
	 * @return this instance
	 */
	public MoneyWrapper setPurchasePrice(final Money purchasePrice) {
		this.purchasePrice = purchasePrice;
		return this;
	}

	@Override
	public boolean equals(final Object that) {
		return EqualsBuilder.reflectionEquals(this, that);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}

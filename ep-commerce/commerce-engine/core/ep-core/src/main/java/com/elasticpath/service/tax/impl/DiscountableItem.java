/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.tax.impl;

import java.math.BigDecimal;

/**
 * Discountable item attributes required by sorting algorithm.
 *
 */
public class DiscountableItem {
	private final String guid;
	private final BigDecimal amount;
	private final String skuCode;

	/**
	 * @param guid guid
	 * @param skuCode skuCode
	 * @param amount amount
	 */
	public DiscountableItem(final String guid, final String skuCode, final BigDecimal amount) {
		this.guid = guid;
		this.amount = amount;
		this.skuCode = skuCode;
	}

	/**
	 * @return the guid
	 */
	public String getGuid() {
		return guid;
	}

	/**
	 * @return the amount
	 */
	public BigDecimal getAmount() {
		return amount;
	}

	/**
	 * @return the skuCode
	 */
	public String getSkuCode() {
		return skuCode;
	}
}
/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.common.pricing.service.impl;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Objects;

import com.elasticpath.common.pricing.service.BaseAmountFilterExt;

/**
 *
 * Implementation of the BaseAmountFilterExt.
 *
 */
public class BaseAmountFilterExtImpl extends BaseAmountFilterImpl implements
	BaseAmountFilterExt {
	
	private BigDecimal highestPrice;
	
	private BigDecimal lowestPrice;
	
	private Locale locale;

	private int startIndex;

	private int limit;
	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 20091127L;

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}

		return (other instanceof BaseAmountFilterExtImpl) && super.equals(other);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode());
	}

	@Override
	public BigDecimal getHighestPrice() {
		return highestPrice;
	}

	@Override	
	public BigDecimal getLowestPrice() {
		return lowestPrice;
	}

	@Override	
	public void setHighestPrice(final BigDecimal highestPrice) {
		this.highestPrice = highestPrice;
	}
	
	@Override
	public void setLowestPrice(final BigDecimal lowestPrice) {
		this.lowestPrice = lowestPrice;
	}

	@Override	
	public Locale getLocale() {
		return locale;
	}

	@Override	
	public void setLocale(final Locale locale) {
		this.locale = locale;
	}

	@Override	
	public int getLimit() {
		return limit;
	}

	@Override	
	public void setLimit(final int limit) {
		this.limit = limit;
	}

	@Override
	public int getStartIndex() {
		return startIndex;
	}

	@Override
	public void setStartIndex(final int startIndex) {
		this.startIndex = startIndex;
	}
}

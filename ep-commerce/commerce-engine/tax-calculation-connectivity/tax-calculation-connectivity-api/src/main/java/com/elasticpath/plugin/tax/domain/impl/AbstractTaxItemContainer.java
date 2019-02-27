/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.domain.impl;

import java.io.Serializable;
import java.util.Currency;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.elasticpath.plugin.tax.domain.TaxAddress;
import com.elasticpath.plugin.tax.domain.TaxItemContainer;

/**
 *  Abstract class to hold common methods for {@link TaxItemContainer}.
 */
public abstract class AbstractTaxItemContainer implements TaxItemContainer, Serializable {

	private static final long serialVersionUID = 50000000001L;

	private boolean taxInclusive;
	private TaxAddress originAddress;
	private TaxAddress destinationAddress;
	private String storeCode;
	private Currency currency;

	@Override
	public boolean isTaxInclusive() {
		return taxInclusive;
	}


	public void setTaxInclusive(final boolean taxInclusive) {
		this.taxInclusive = taxInclusive;
	}

	@Override
	public TaxAddress getOriginAddress() {
		return originAddress;
	}

	public void setOriginAddress(final TaxAddress originAddress) {
		this.originAddress = originAddress;
	}

	@Override
	public TaxAddress getDestinationAddress() {
		return destinationAddress;
	}

	public void setDestinationAddress(final TaxAddress destinationAddress) {
		this.destinationAddress = destinationAddress;
	}

	@Override
	public String getStoreCode() {
		return storeCode;
	}

	public void setStoreCode(final String storeCode) {
		this.storeCode = storeCode;
	}

	@Override
	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(final Currency currency) {
		this.currency = currency;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}

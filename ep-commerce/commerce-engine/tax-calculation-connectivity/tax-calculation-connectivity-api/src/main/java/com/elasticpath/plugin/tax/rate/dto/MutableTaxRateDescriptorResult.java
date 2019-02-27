/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.rate.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.elasticpath.plugin.tax.rate.TaxRateDescriptor;
import com.elasticpath.plugin.tax.rate.TaxRateDescriptorResult;

/**
 * A mutable {@link TaxRateDescriptorResult} implementation.
 */
public class MutableTaxRateDescriptorResult implements TaxRateDescriptorResult, Serializable {
	
	private static final long serialVersionUID = 50000000001L;

	private boolean taxInclusive;

	private final List<TaxRateDescriptor> taxRateDescriptors = new ArrayList<>();

	private BigDecimal sumOfTaxRates = BigDecimal.ZERO;

	@Override
	public boolean isTaxInclusive() {
		return taxInclusive;
	}

	@Override
	public List<TaxRateDescriptor> getTaxRateDescriptors() {
		return taxRateDescriptors;
	}

	/**
	 * Adds a new tax rate.
	 * 
	 * @param taxRateDescriptor the tax rate to add
	 */
	public void addTaxRateDescriptor(final TaxRateDescriptor taxRateDescriptor) {
		taxRateDescriptors.add(taxRateDescriptor);
		
		if (taxRateDescriptor instanceof MutableTaxRateDescriptor) {
			((MutableTaxRateDescriptor) taxRateDescriptor).setTaxRateDescriptorResult(this);
			sumOfTaxRates = sumOfTaxRates.add(taxRateDescriptor.getTaxRateValue());
		}
	}

	public BigDecimal getSumOfTaxRates() {
		return sumOfTaxRates;
	}

	public void setTaxInclusive(final boolean taxInclusive) {
		this.taxInclusive = taxInclusive;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
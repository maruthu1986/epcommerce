/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.rate.dto;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import com.elasticpath.plugin.tax.rate.TaxRateDescriptor;
import com.elasticpath.plugin.tax.rate.TaxRateDescriptorResult;

/**
 * Implementation of {@link TaxRateDescriptorResult} that is returned when taxes are not applicable or cannot be determined.
 * For example, this is returned for gift certificates (taxes not applicable) and when either the tax jurisdiction
 * or tax region cannot be resolved.
 */
public class NoTaxRateDescriptorResult implements TaxRateDescriptorResult, Serializable {
	
	private static final long serialVersionUID = 50000000001L;
	
	private boolean taxInclusive;

	private final TaxRateDescriptor taxRateDescriptor = new NoTaxRateDescriptor();
	
	/**
	 * No argument constructor.
	 */
	public NoTaxRateDescriptorResult() {
		super();
	}

	/**
	 * Default constructor.
	 * 
	 * @param taxInclusive tax inclusive or not
	 */
	public NoTaxRateDescriptorResult(final boolean taxInclusive) {
		this.taxInclusive = taxInclusive;
	}

	@Override
	public boolean isTaxInclusive() {
		return taxInclusive;
	}

	@Override
	public List<TaxRateDescriptor> getTaxRateDescriptors() {
		return Arrays.<TaxRateDescriptor>asList(taxRateDescriptor);
	}

}

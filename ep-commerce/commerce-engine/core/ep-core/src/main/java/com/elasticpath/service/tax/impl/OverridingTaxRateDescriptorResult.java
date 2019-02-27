/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.service.tax.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.elasticpath.plugin.tax.rate.TaxRateDescriptor;
import com.elasticpath.plugin.tax.rate.TaxRateDescriptorResult;

/**
 * Tax rate result with overriding tax rates.
 */
public class OverridingTaxRateDescriptorResult implements TaxRateDescriptorResult {

	private final List<TaxRateDescriptor> overrideTaxRateDescriptors;
	private final TaxRateDescriptorResult delegate;

	/**
	 * Constructor.
	 *
	 * @param taxRateDescriptorResult tax rate descriptor result
	 * @param overrideTaxRateDescriptors the tax rate descriptor to override the original tax rate descriptors
	 */
	public OverridingTaxRateDescriptorResult(final TaxRateDescriptorResult taxRateDescriptorResult, 
												final Collection<TaxRateDescriptor> overrideTaxRateDescriptors) {
		this.delegate = taxRateDescriptorResult;
		this.overrideTaxRateDescriptors = new ArrayList<>(overrideTaxRateDescriptors);
	}

	@Override
	public boolean isTaxInclusive() {
		return delegate.isTaxInclusive();
	}

	@Override
	public List<TaxRateDescriptor> getTaxRateDescriptors() {
		return overrideTaxRateDescriptors;
	}
}

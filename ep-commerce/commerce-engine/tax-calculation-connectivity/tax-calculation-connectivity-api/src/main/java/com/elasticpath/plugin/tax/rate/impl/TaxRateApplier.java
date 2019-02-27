/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.rate.impl;

import java.io.Serializable;

import com.elasticpath.plugin.tax.domain.TaxedItem;
import com.elasticpath.plugin.tax.rate.TaxRateDescriptor;
import com.elasticpath.plugin.tax.rate.dto.AppliedTaxValue;

/**
 * Tax rate applier interface.
 */
public interface TaxRateApplier extends Serializable {

	/**
	 * Applies a tax rate to a {@link TaxedItem}. 
	 * 
	 * @param taxRateDescriptor tax rate descriptor to apply
	 * @param taxedItem the taxed item on which to apply the tax rate
	 * @return the applied tax value
	 */
	AppliedTaxValue applyTaxRate(TaxRateDescriptor taxRateDescriptor, TaxedItem taxedItem);
}

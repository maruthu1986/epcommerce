/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.rate.impl;

import java.math.BigDecimal;

import com.elasticpath.plugin.tax.common.TaxCalculationConstants;
import com.elasticpath.plugin.tax.domain.TaxedItem;
import com.elasticpath.plugin.tax.rate.TaxRateDescriptor;
import com.elasticpath.plugin.tax.rate.dto.AppliedTaxValue;

/**
 * Tax exclusive rate applier which applies tax rates for tax exclusive jurisdictions.
 */
public class TaxExclusiveRateApplier implements TaxRateApplier {

	private static final long serialVersionUID = 4033617169878998686L;

	/**
	 * Calculates tax value and before tax value based on the tax rate.
	 * 
	 * @param taxRateDescriptor the tax rate descriptor
	 * @param item the item on which to apply taxes
	 * @return the applied tax value
	 */
	@Override
	public AppliedTaxValue applyTaxRate(final TaxRateDescriptor taxRateDescriptor, final TaxedItem item) {
		BigDecimal taxAmount = calculateTax(item.getTaxablePrice(), taxRateDescriptor.getTaxRateValue());

		return new AppliedTaxValue(item.getPrice(), taxAmount, BigDecimal.ZERO);
	}

	/**
	 * Simple tax calculation, multiplies amount by tax rate and returns result.
	 * 
	 * @param amount the amount (it is an error for this to be null)
	 * @param decimalTaxRate the decimal tax rate. i.e. 0.05 for 5% tax rate.
	 * @return result the calculated taxes
	 */
	protected BigDecimal calculateTax(final BigDecimal amount, final BigDecimal decimalTaxRate) {
		return amount.multiply(decimalTaxRate).setScale(TaxCalculationConstants.DEFAULT_DECIMAL_SCALE, 
														TaxCalculationConstants.DEFAULT_ROUNDING_MODE);
	}

}

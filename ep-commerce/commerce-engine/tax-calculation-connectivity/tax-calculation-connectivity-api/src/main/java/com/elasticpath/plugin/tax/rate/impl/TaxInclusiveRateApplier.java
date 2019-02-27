/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.rate.impl;

import java.math.BigDecimal;

import com.elasticpath.plugin.tax.common.TaxCalculationConstants;
import com.elasticpath.plugin.tax.domain.TaxedItem;
import com.elasticpath.plugin.tax.rate.TaxRateDescriptor;
import com.elasticpath.plugin.tax.rate.dto.AppliedTaxValue;
import com.elasticpath.plugin.tax.rate.dto.MutableTaxRateDescriptor;

/**
 * Tax inclusive rate applier.
 */
public class TaxInclusiveRateApplier implements TaxRateApplier {

	private static final long serialVersionUID = -3836312830770058036L;

	/**
	 * Applies a tax rate for a tax inclusive jurisdiction. The amount already contains taxes, and we
     * the tax rate to determine the amount included tax amount.
	 * 
	 * @param taxRateDescriptor the tax rate descriptor
	 * @param item the item on which to apply taxes
	 * @return the applied tax value
	 */
	@Override
	public AppliedTaxValue applyTaxRate(final TaxRateDescriptor taxRateDescriptor, final TaxedItem item) {
		
		BigDecimal taxAmount = getTaxIncludedInPrice(((MutableTaxRateDescriptor) taxRateDescriptor).getTaxRateDescriptorResult().getSumOfTaxRates(),
				taxRateDescriptor.getTaxRateValue(), item.getTaxablePrice());

		BigDecimal beforeTaxAmount = item.getPrice().subtract(taxAmount);
		return new AppliedTaxValue(beforeTaxAmount, taxAmount, taxAmount);
	}

	/**
	 * Calculates the line item tax included in the tax inclusive price: (tax rate 1 x price) / 1 + sum(all tax rates).
	 * 
	 * @param sumOfTaxRates the sum of all tax rates included in the price, such as GST + PST
	 * @param decimalTaxRate the tax rate to calculate the price amount of
	 * @param price the price of the line item, with discount already subtracted
	 * @return the tax amount included in the line item tax
	 */
	protected BigDecimal getTaxIncludedInPrice(final BigDecimal sumOfTaxRates, final BigDecimal decimalTaxRate, final BigDecimal price) {
		// 1 + sum(all tax rates)
		BigDecimal div = sumOfTaxRates.add(BigDecimal.ONE);

		// (tax rate 1 x price) / div
		return decimalTaxRate.multiply(price).divide(div, TaxCalculationConstants.DEFAULT_DIVIDE_SCALE, TaxCalculationConstants.DEFAULT_ROUNDING_MODE)
				.setScale(price.scale(), TaxCalculationConstants.DEFAULT_ROUNDING_MODE);
	}
}

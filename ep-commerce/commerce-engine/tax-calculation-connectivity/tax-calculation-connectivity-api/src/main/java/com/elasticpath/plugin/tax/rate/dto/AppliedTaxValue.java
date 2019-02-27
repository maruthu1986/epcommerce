/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.rate.dto;

import java.math.BigDecimal;

/**
 * The taxes applied on an item.
 */
public class AppliedTaxValue {
	
	private final BigDecimal beforeTaxAmount;
	private final BigDecimal taxAmount;
	private final BigDecimal includedTaxAmount;
	
	/**
	 * Default constructor.
	 * 
	 * @param beforeTaxAmount the amount before tax
	 * @param taxAmount the tax amount
	 * @param includedTaxAmount the included tax amount in the total amount
	 */
	public AppliedTaxValue(final BigDecimal beforeTaxAmount, final BigDecimal taxAmount, final BigDecimal includedTaxAmount) {
		this.taxAmount = taxAmount;
		this.beforeTaxAmount = beforeTaxAmount;
		this.includedTaxAmount = includedTaxAmount;
	}
	
	public BigDecimal getBeforeTaxAmount() {
		return beforeTaxAmount;
	}

	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	public BigDecimal getIncludedTaxAmount() {
		return includedTaxAmount;
	}
	
}

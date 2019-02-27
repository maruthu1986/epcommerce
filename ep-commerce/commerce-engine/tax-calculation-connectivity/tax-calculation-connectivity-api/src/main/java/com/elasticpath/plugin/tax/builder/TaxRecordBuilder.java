/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.builder;

import java.math.BigDecimal;

import com.elasticpath.plugin.tax.common.TaxCalculationConstants;
import com.elasticpath.plugin.tax.domain.TaxRecord;
import com.elasticpath.plugin.tax.domain.impl.MutableTaxRecord;

/**
 * Builder for {@link MutableTaxRecord}.
 */
public class TaxRecordBuilder {
	
private final MutableTaxRecord mutableTaxRecord;
	
	
	/**
	 * Constructor.
	 */
	public TaxRecordBuilder() {
		mutableTaxRecord = new MutableTaxRecord();
	}
	
	/** Gets a new builder. 
	 * 
	 * @return a new builder.
	 */
	public static TaxRecordBuilder newBuilder() {
		return new TaxRecordBuilder();
	}
	
	/** Gets the instance built by the builder.
	 * 
	 * @return the built instance
	 */
	public TaxRecord build() {
		return mutableTaxRecord;
	}

	/** Sets the tax code.
	 * 
	 * @param taxCode the given tax code
	 * @return the builder
	 */
	public TaxRecordBuilder withTaxCode(final String taxCode) {
		mutableTaxRecord.setTaxCode(taxCode);
		return this;
	}
	
	/** Sets the tax name.
	 * 
	 * @param taxName the given tax name
	 * @return the builder
	 */
	public TaxRecordBuilder withTaxName(final String taxName) {
		mutableTaxRecord.setTaxName(taxName);
		return this;
	}
	
	/** Sets the tax jurisdiction.
	 * 
	 * @param taxJurisdiction the given tax jurisdiction
	 * @return the builder
	 */
	public TaxRecordBuilder withTaxJurisdiction(final String taxJurisdiction) {
		mutableTaxRecord.setTaxJurisdiction(taxJurisdiction);
		return this;
	}
	
	/** Sets the tax region.
	 * 
	 * @param taxRegion the given tax region
	 * @return the builder
	 */
	public TaxRecordBuilder withTaxRegion(final String taxRegion) {
		mutableTaxRecord.setTaxRegion(taxRegion);
		return this;
	}
	
	/** Sets the tax rate.
	 * 
	 * @param taxRate the given tax rate
	 * @return the builder
	 */
	public TaxRecordBuilder withTaxRate(final BigDecimal taxRate) {
		mutableTaxRecord.setTaxRate(taxRate.setScale(TaxCalculationConstants.TAX_RATE_DECIMAL_SCALE, 
													TaxCalculationConstants.DEFAULT_ROUNDING_MODE));
		return this;
	}
	
	/** Sets the tax value.
	 * 
	 * @param taxProvider the given tax provider
	 * @return the builder
	 */
	public TaxRecordBuilder withTaxProvider(final String taxProvider) {
		mutableTaxRecord.setTaxProvider(taxProvider);
		return this;
	}
	
	/** Sets the tax provider.
	 * 
	 * @param taxValue the given tax value
	 * @return the builder
	 */
	public TaxRecordBuilder withTaxValue(final BigDecimal taxValue) {
		mutableTaxRecord.setTaxValue(taxValue.setScale(TaxCalculationConstants.DEFAULT_DECIMAL_SCALE, 
				 									   TaxCalculationConstants.DEFAULT_ROUNDING_MODE));
		return this;
	}
}

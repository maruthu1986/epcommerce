/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.rate.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.elasticpath.plugin.tax.domain.TaxRecord;
import com.elasticpath.plugin.tax.domain.TaxedItem;
import com.elasticpath.plugin.tax.rate.TaxRateDescriptor;

/**
 * A {@link TaxRateDescriptor} implementation that indicates taxes are not applicable or cannot be determined.
 * This is not to be confused with a zero tax rate.
 */
public class NoTaxRateDescriptor implements TaxRateDescriptor, Serializable {
	
	private static final long serialVersionUID = 50000000001L;
	
	/**
	 * Tax jurisdiction with a default value.
	 */
	private String taxJurisdiction = "NO_TAX_JURISDICTION";

	/**
	 * Tax region name with a default value.
	 */
	private String taxRegionName = "NO_TAX_REGION";
	
	/**
	 * No arguments constructor.
	 */
	public NoTaxRateDescriptor() {
		super();
	}
	
	/**
	 * Constructor with arguments.
	 * 
	 * @param taxJurisdiction the tax jurisdiction
	 * @param taxRegionName the tax region name
	 */
	public NoTaxRateDescriptor(final String taxJurisdiction, final String taxRegionName) {
		if (taxJurisdiction != null) {
			this.taxJurisdiction = taxJurisdiction;
		}
		
		if (taxRegionName != null) {
			this.taxRegionName = taxRegionName;
		}
	}
	
	@Override
	public BigDecimal getTaxRateValue() {
		return BigDecimal.ZERO;
	}

	@Override
	public AppliedTaxValue applyTo(final TaxedItem item) {
		return new AppliedTaxValue(item.getPrice(), BigDecimal.ZERO, BigDecimal.ZERO);
	}

	@Override
	public String getTaxRateName() {
		return TaxRecord.NO_TAX_RATE_TAX_NAME;
	}
	
	@Override
	public String getTaxJurisdiction() {
		return taxJurisdiction;
	}
	
	@Override
	public String getTaxRegion() {
		return taxRegionName;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof TaxRateDescriptor)) {
			return false;
		}
		TaxRateDescriptor taxRateDescriptor = (TaxRateDescriptor) obj;
		return Objects.equals(getTaxRateName(), taxRateDescriptor.getTaxRateName());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getTaxRateName());
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
		.append("taxRateName", getTaxRateName())
		.toString();
	}

}

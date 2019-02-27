/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.rate.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.elasticpath.plugin.tax.domain.TaxedItem;
import com.elasticpath.plugin.tax.rate.TaxRateDescriptor;
import com.elasticpath.plugin.tax.rate.impl.TaxRateApplier;

/**
 * A mutable {@link TaxRateDescriptor} implementation.
 */
public class MutableTaxRateDescriptor implements TaxRateDescriptor, Serializable {
	
	private static final long serialVersionUID = 50000000001L;

	private TaxRateApplier taxRateApplier;
	private String taxRateName;
	private BigDecimal taxRateValue;
	private MutableTaxRateDescriptorResult taxRateDescriptorResult;
	private String taxRegion;
	private String taxJurisdiction;
	
	@Override
	public BigDecimal getTaxRateValue() {
		return taxRateValue;
	}

	@Override
	public AppliedTaxValue applyTo(final TaxedItem item) {
		return taxRateApplier.applyTaxRate(this, item);
	}

	@Override
	public String getTaxRateName() {
		return taxRateName;
	}

	public void setId(final String taxRateName) {
		this.taxRateName = taxRateName;
	}

	public void setTaxRateApplier(final TaxRateApplier taxRateApplier) {
		this.taxRateApplier = taxRateApplier;
	}

	public void setValue(final BigDecimal value) {
		this.taxRateValue = value;
	}

	/**
	 * Sets the tax rate descriptor result.
	 * 
	 * @param taxRateDescriptorResult the tax rate descriptor result
	 */
	public void setTaxRateDescriptorResult(final MutableTaxRateDescriptorResult taxRateDescriptorResult) {
		this.taxRateDescriptorResult = taxRateDescriptorResult;
	}
	
	public MutableTaxRateDescriptorResult getTaxRateDescriptorResult() {
		return taxRateDescriptorResult;
	}
	
	@Override
	public String getTaxJurisdiction() {
		return taxJurisdiction;
	}

	@Override
	public String getTaxRegion() {
		return taxRegion;
	}

	public void setTaxRegion(final String taxRegion) {
		this.taxRegion = taxRegion;
	}

	public void setTaxJurisdiction(final String taxJurisdiction) {
		this.taxJurisdiction = taxJurisdiction;
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
		.append("id", getTaxRateName())
		.append("value", getTaxRateValue())
		.toString();
	}

}
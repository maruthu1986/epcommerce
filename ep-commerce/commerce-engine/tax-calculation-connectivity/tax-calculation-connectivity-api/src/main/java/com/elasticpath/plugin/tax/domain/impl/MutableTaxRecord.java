/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.domain.impl;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.elasticpath.plugin.tax.domain.TaxRecord;

/**
 * Mutable implementation of {@link TaxRecord}.
 */
public class MutableTaxRecord implements TaxRecord, Serializable {
	
	private static final long serialVersionUID = 50000000001L;
	
	private String taxCode;
	private String taxName;
	private String taxJurisdiction;
	private String taxRegion;
	private BigDecimal taxRate;
	private BigDecimal taxValue;
	private String taxProvider;
	
	@Override
	public String getTaxCode() {
		return taxCode;
	}

	public void setTaxCode(final String taxCode) {
		this.taxCode = taxCode;
	}

	@Override
	public String getTaxName() {
		return taxName;
	}

	public void setTaxName(final String taxName) {
		this.taxName = taxName;
	}
	
	@Override
	public String getTaxJurisdiction() {
		return taxJurisdiction;
	}

	public void setTaxJurisdiction(final String taxJurisdiction) {
		this.taxJurisdiction = taxJurisdiction;
	}
	
	@Override
	public String getTaxRegion() {
		return taxRegion;
	}

	public void setTaxRegion(final String taxRegion) {
		this.taxRegion = taxRegion;
	}

	@Override
	public BigDecimal getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(final BigDecimal taxRate) {
		this.taxRate = taxRate;
	}
	
	@Override
	public BigDecimal getTaxValue() {
		return taxValue;
	}

	public void setTaxValue(final BigDecimal taxValue) {
		this.taxValue = taxValue;
	}
	
	@Override
	public String getTaxProvider() {
		return taxProvider;
	}

	public void setTaxProvider(final String taxProvider) {
		this.taxProvider = taxProvider;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}
	
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}

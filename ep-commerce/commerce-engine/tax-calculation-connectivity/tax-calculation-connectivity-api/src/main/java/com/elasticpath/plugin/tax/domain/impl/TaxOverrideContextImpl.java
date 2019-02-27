/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.domain.impl;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.elasticpath.plugin.tax.common.TaxJournalType;
import com.elasticpath.plugin.tax.domain.TaxOverrideContext;

/**
 * Implementation of {@link TaxOverrideContext}.
 */
public class TaxOverrideContextImpl implements TaxOverrideContext, Serializable {
	
	private static final long serialVersionUID = 50000000001L;
	
	private String taxOverrideDocumentId;
	private TaxJournalType taxOverrideJournalType;
	private Date taxOverrideTransactionDate;
	
	/**
	 * Default constructor.
	 */
	public TaxOverrideContextImpl() {
		// No arguments constrcutor
	}
	
	/**
	 * Constructor with arguments.
	 *
	 * @param taxOverrideDocumentId the taxOverrideDocumentId
	 * @param taxJournalType the taxJournalType
	 */
	public TaxOverrideContextImpl(final String taxOverrideDocumentId, final TaxJournalType taxJournalType) {
		this.taxOverrideDocumentId = taxOverrideDocumentId;
		this.taxOverrideJournalType = taxJournalType;
	}
	
	@Override
	public String getTaxOverrideDocumentId() {
		return taxOverrideDocumentId;
	}

	public void setTaxOverrideDocumentId(final String taxOverrideDocumentId) {
		this.taxOverrideDocumentId = taxOverrideDocumentId;
	}

	@Override
	public TaxJournalType getTaxOverrideJournalType() {
		return taxOverrideJournalType;
	}
	
	public void setTaxOverrideJournalType(final TaxJournalType taxJournalType) {
		this.taxOverrideJournalType = taxJournalType;
	}
	
	@Override
	public Date getTaxOverrideTransactionDate() {
		return taxOverrideTransactionDate;
	}

	@Override
	public void setTaxOverrideTransactionDate(final Date taxOverrideTransactionDate) {
		this.taxOverrideTransactionDate = taxOverrideTransactionDate;
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

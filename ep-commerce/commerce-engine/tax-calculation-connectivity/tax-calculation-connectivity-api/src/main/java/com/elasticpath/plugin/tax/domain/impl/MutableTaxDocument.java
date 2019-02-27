/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.domain.impl;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.elasticpath.plugin.tax.common.TaxJournalType;
import com.elasticpath.plugin.tax.domain.TaxDocument;
import com.elasticpath.plugin.tax.domain.TaxDocumentId;
import com.elasticpath.plugin.tax.domain.TaxedItemContainer;

/**
 * Serializable and mutable {@link TaxDocument} implementation.
 */
public class MutableTaxDocument implements TaxDocument, Serializable {

	private static final long serialVersionUID = 5000000001L;
	
	private TaxedItemContainer taxedItemContainer;
	private TaxDocumentId documentId;
	private String taxProviderName;
	private TaxJournalType journalType;

	@Override
	public TaxedItemContainer getTaxedItemContainer() {
		return taxedItemContainer;
	}

	@Override
	public TaxDocumentId getDocumentId() {
		return documentId;
	}

	public void setTaxedItemContainer(final TaxedItemContainer taxedItemContainer) {
		this.taxedItemContainer = taxedItemContainer;
	}

	public void setDocumentId(final TaxDocumentId documentId) {
		this.documentId = documentId;
	}

	@Override
	public String getTaxProviderName() {
		return taxProviderName;
	}

	public void setTaxProviderName(final String taxProviderName) {
		this.taxProviderName = taxProviderName;
	}
	
	@Override
	public TaxJournalType getJournalType() {
		return journalType;
	}

	public void setJournalType(final TaxJournalType journalType) {
		this.journalType = journalType;
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

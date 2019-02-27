/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.domain.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.elasticpath.plugin.tax.common.TaxItemObjectType;
import com.elasticpath.plugin.tax.common.TaxJournalType;
import com.elasticpath.plugin.tax.common.TaxTransactionType;
import com.elasticpath.plugin.tax.domain.TaxDocumentId;
import com.elasticpath.plugin.tax.domain.TaxExemption;
import com.elasticpath.plugin.tax.domain.TaxOperationContext;
import com.elasticpath.plugin.tax.domain.TaxOverrideContext;

/**
 * Implementation of {@link TaxOperationContext}.
 */
public class TaxOperationContextImpl implements TaxOperationContext, Serializable {

	private static final long serialVersionUID = 50000000001L;

	private Currency currency;
	private String customerCode;
	private TaxJournalType journalType;
	private TaxDocumentId documentId;
	private String orderNumber;
	private TaxTransactionType transactionType;
	private TaxItemObjectType itemObjectType;
	private String shippingItemReferenceId;
	private TaxOverrideContext taxOverrideContext;
	private Map<String, String> fieldValues = new HashMap<>();
	private TaxExemption taxExemption;
	private String customerBusinessNumber;

	/**
	 * Default constructor.
	 */
	public TaxOperationContextImpl() {
		// No arguments constructor
	}

	/**
	 * Constructor with arguments.
	 *
	 * @param customerCode the customerCode
	 * @param journalType the journalType
	 * @param taxDocumentId the tax document id
	 */
	public TaxOperationContextImpl(final String customerCode,
									final TaxJournalType journalType,
									final TaxDocumentId taxDocumentId) {
		this.customerCode = customerCode;
		this.journalType = journalType;
		this.documentId = taxDocumentId;
	}

	@Override
	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(final Currency currency) {
		this.currency = currency;
	}

	@Override
	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(final String customerCode) {
		this.customerCode = customerCode;
	}

	@Override
	public TaxJournalType getJournalType() {
		return journalType;
	}

	public void setJournalType(final TaxJournalType journalType) {
		this.journalType = journalType;
	}

	@Override
	public TaxDocumentId getDocumentId() {
		return documentId;
	}

	public void setDocumentId(final TaxDocumentId documentId) {
		this.documentId = documentId;
	}

	@Override
	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(final String orderNumber) {
		this.orderNumber = orderNumber;
	}

	@Override
	public TaxTransactionType getTransactionType() {
		return transactionType;
	}

	public void setAction(final TaxTransactionType transactionType) {
		this.transactionType = transactionType;
	}

	@Override
	public TaxItemObjectType getItemObjectType() {
		return itemObjectType;
	}

	public void setItemObjectType(final TaxItemObjectType itemObjectType) {
		this.itemObjectType = itemObjectType;
	}

	@Override
	public String getShippingItemReferenceId() {
		return shippingItemReferenceId;
	}

	public void setShippingItemReferenceId(final String shippingItemReferenceId) {
		this.shippingItemReferenceId = shippingItemReferenceId;
	}

	@Override
	public TaxOverrideContext getTaxOverrideContext() {
		return taxOverrideContext;
	}

	public void setTaxOverrideContext(final TaxOverrideContext taxOverrideContext) {
		this.taxOverrideContext = taxOverrideContext;
	}

	@Override
	public TaxExemption getTaxExemption() {
		return taxExemption;
	}

	public void setTaxExemption(final TaxExemption taxExemption) {
		this.taxExemption = taxExemption;
	}

	@Override
	public String getCustomerBusinessNumber() {
		return customerBusinessNumber;
	}

	public void setCustomerBusinessNumber(final String customerBusinessNumber) {
		this.customerBusinessNumber = customerBusinessNumber;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public void setFields(final Map<String, String> fieldValues) {
		this.fieldValues = fieldValues;
	}

	@Override
	public void setFieldValue(final String name, final String value) {
		fieldValues.put(name, value);
	}

	@Override
	public String getFieldValue(final String name) {
		return fieldValues.get(name);
	}

	@Override
	public Map<String, String> getFields() {
		Map<String, String> fields = new HashMap<>();
		for (final Map.Entry<String, String> fieldEntry : fieldValues.entrySet()) {
			fields.put(fieldEntry.getKey(), fieldEntry.getValue());
		}
		return Collections.unmodifiableMap(fields);
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

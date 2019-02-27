/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.builder;

import java.util.Currency;

import com.elasticpath.plugin.tax.common.TaxItemObjectType;
import com.elasticpath.plugin.tax.common.TaxJournalType;
import com.elasticpath.plugin.tax.common.TaxTransactionType;
import com.elasticpath.plugin.tax.domain.TaxDocumentId;
import com.elasticpath.plugin.tax.domain.TaxExemption;
import com.elasticpath.plugin.tax.domain.TaxOperationContext;
import com.elasticpath.plugin.tax.domain.TaxOverrideContext;
import com.elasticpath.plugin.tax.domain.impl.TaxOperationContextImpl;

/**
 * Builder for {@link TaxOperationContextImpl}.
 */
public class TaxOperationContextBuilder {

private final TaxOperationContextImpl taxOperationContext;

	/**
	 * Constructor.
	 */
	public TaxOperationContextBuilder() {
		taxOperationContext = new TaxOperationContextImpl();
	}

	/** Gets a new builder.
	 *
	 * @return a new builder.
	 */
	public static TaxOperationContextBuilder newBuilder() {
		return new TaxOperationContextBuilder();
	}

	/** Gets the instance built by the builder.
	 *
	 * @return the built instance
	 */
	public TaxOperationContext build() {
		return taxOperationContext;
	}

	/** Sets currency.
	 *
	 * @param currency the currency
	 * @return the builder
	 */
	public TaxOperationContextBuilder withCurrency(final Currency currency) {
		taxOperationContext.setCurrency(currency);
		return this;
	}

	/** Sets tax customer code.
	 *
	 * @param customerCode the given tax customer code
	 * @return the builder
	 */
	public TaxOperationContextBuilder withCustomerCode(final String customerCode) {
		taxOperationContext.setCustomerCode(customerCode);
		return this;
	}

	/** Sets tax document id.
	 *
	 * @param documentId the given tax document id
	 * @return the builder
	 */
	public TaxOperationContextBuilder withTaxDocumentId(final TaxDocumentId documentId) {
		taxOperationContext.setDocumentId(documentId);
		return this;
	}


	/** Sets tax journal type {@link TaxJournalType}.
	 *
	 * @param journalType the given tax journal type
	 * @return the builder
	 */
	public TaxOperationContextBuilder withTaxJournalType(final TaxJournalType journalType) {
		taxOperationContext.setJournalType(journalType);
		return this;
	}

	/** Sets order naume.
	 *
	 * @param orderNumber the given order number
	 * @return the builder
	 */
	public TaxOperationContextBuilder withOrderNumber(final String orderNumber) {
		taxOperationContext.setOrderNumber(orderNumber);
		return this;
	}

	/** Sets tax operation action.
	 *
	 * @param action the given tax operation action
	 * @return the builder
	 */
	public TaxOperationContextBuilder withTaxTransactionType(final TaxTransactionType action) {
		taxOperationContext.setAction(action);
		return this;
	}

	/** Sets tax item object type.
	 *
	 * @param itemObjectType the given tax item object type
	 * @return the builder
	 */
	public TaxOperationContextBuilder withTaxItemObjectType(final TaxItemObjectType itemObjectType) {
		taxOperationContext.setItemObjectType(itemObjectType);
		return this;
	}

	/** Sets shipping item reference id.
	 *
	 * @param shippingItemReferenceId the given shipping item reference id
	 * @return the builder
	 */
	public TaxOperationContextBuilder withShippingItemReferenceId(final String shippingItemReferenceId) {
		taxOperationContext.setShippingItemReferenceId(shippingItemReferenceId);
		return this;
	}

	/** Sets tax override context.
	 *
	 * @param taxOverrideContext the given tax override context
	 * @return the builder
	 */
	public TaxOperationContextBuilder withTaxOverrideContext(final TaxOverrideContext taxOverrideContext) {
		taxOperationContext.setTaxOverrideContext(taxOverrideContext);
		return this;
	}

	/** Sets tax exemption.
	 *
	 * @param taxExemption the given tax exemption
	 * @return the builder
	 */
	public TaxOperationContextBuilder withTaxExemption(final TaxExemption taxExemption) {
		taxOperationContext.setTaxExemption(taxExemption);
		return this;
	}

	/**
	 * Sets the customer business number.
	 *
	 * @param customerBusinessNumber the customer's business number
	 * @return the builder
	 */
	public TaxOperationContextBuilder withCustomerBusinessNumber(final String customerBusinessNumber) {
		taxOperationContext.setCustomerBusinessNumber(customerBusinessNumber);
		return this;
	}
}

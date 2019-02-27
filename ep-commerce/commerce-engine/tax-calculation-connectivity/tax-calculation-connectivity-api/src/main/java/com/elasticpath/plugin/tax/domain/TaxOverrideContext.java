/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.domain;

import java.util.Date;

import com.elasticpath.plugin.tax.common.TaxJournalType;

/**
 * Interface that provides the context for tax override operations.
 *
 * For example, when determining tax rates for an order return, the EP tax provider uses this to obtain tax rates from
 * the tax journal entries for the original purchase. Avalara, however, uses just the original purchase date to calculate
 * taxes for a return.
 */
public interface TaxOverrideContext {
	
	/**
	 * Gets the id of the override tax document to be used to determine tax rates.
	 *
	 * @return the tax document id
	 */
	String getTaxOverrideDocumentId();
	
	/**
	 * Gets the {@link TaxJournalType} of the override document.
	 * 
	 * @return the tax journal type
	 */
	TaxJournalType getTaxOverrideJournalType();
	
	/**
	 * Gets the transaction date for the override document id and the journal type.
	 * 
	 * @return the transaction date
	 */
	Date getTaxOverrideTransactionDate();
	
	/**
	 * Sets the transaction date for the override document id and the journal type.
	 * 
	 * @param taxOverrideTransactionDate the tax journal transaction date
	 */
	void setTaxOverrideTransactionDate(Date taxOverrideTransactionDate);
}

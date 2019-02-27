/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.domain;

import com.elasticpath.plugin.tax.common.TaxJournalType;

/**
 * Interface defining information returned as the result of tax calculations.
 *
 * {@see com.elasticpath.plugin.tax.manager.TaxManager} which provides the interface for managing tax documents.
 */
public interface TaxDocument {

	/**
	 * Gets the taxed item container.
	 * 
	 * @return taxed item container
	 */
	TaxedItemContainer getTaxedItemContainer();
	
	/**
	 * Gets the tax document ID.
	 * 
	 * @return the document ID
	 */
	TaxDocumentId getDocumentId();
	
	/**
	 * Gets tax provider name.
	 *
	 * @return the tax provider name
	 */
	String getTaxProviderName();
	
	/**
	 * Gets the {@link TaxJournalType}.
	 * 
	 * @return the journal type
	 */
	TaxJournalType getJournalType();
}

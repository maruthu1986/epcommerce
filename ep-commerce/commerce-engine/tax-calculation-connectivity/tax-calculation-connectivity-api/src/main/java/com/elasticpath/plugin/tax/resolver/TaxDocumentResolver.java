/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.resolver;

import java.util.Date;

import com.elasticpath.plugin.tax.common.TaxJournalType;
import com.elasticpath.plugin.tax.domain.TaxDocumentId;

/**
 * Retrieves tax document related information from EP tax journal history.
 */
public interface TaxDocumentResolver extends TaxOperationResolver {
	
	/**
	 * Finds the tax provider for a {@code taxItemId} and a {@link TaxDocumentId).
	 * 
	 * @param taxItemId the item reference id
	 * @param taxDocumentId the tax document id
	 * @return tax provider name for the specified taxItemId and taxDocumentId
	 */
	String findTaxProvider(String taxItemId, TaxDocumentId taxDocumentId);

	/**
	 * Finds the purchase creation date for a {@link TaxDocumentId}.
	 * 
	 * @param taxDocumentId the tax document id
	 * @param taxJournalType the tax document type
	 * @return the purchase creation date for the specified taxItemId and taxDocumentId
	 */
	Date findCreationDate(TaxDocumentId taxDocumentId, TaxJournalType taxJournalType);
}

/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.service.tax;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.elasticpath.domain.order.TaxJournalRecord;
import com.elasticpath.plugin.tax.common.TaxJournalType;
import com.elasticpath.plugin.tax.domain.TaxAddress;
import com.elasticpath.plugin.tax.domain.TaxDocument;
import com.elasticpath.plugin.tax.domain.TaxDocumentId;
import com.elasticpath.plugin.tax.domain.TaxOperationContext;

/**
 * Provides services to retrieve {@link TaxJournalRecord} from the tax storage. 
 *
 */
public interface TaxDocumentService {

	/**
	 * Builds a {@link TaxDocument} from a tax document id, an address and a tax journal type.
	 * 
	 * @param taxDocumentId the tax document id
	 * @param destinationAddress the destination address
	 * @param originAddress the origin address
	 * @param taxJournalType the tax journal type {@link TaxJournalType}
	 * @return a tax document built using the given tax document id, address and the tax journal type
	 */
	TaxDocument buildTaxDocument(TaxDocumentId taxDocumentId,
									TaxAddress destinationAddress,
									TaxAddress originAddress,
									TaxJournalType taxJournalType);

	/**
	 * Builds a collection of {@link TaxJournalRecord} from a {@link TaxDocument}.
	 * 
	 * @param taxDocument the tax document
	 * @param taxOperationContext the tax operation context
	 * @return a list of {@link TaxJournalRecord}
	 */
	List<TaxJournalRecord> buildTaxJournalRecords(TaxDocument taxDocument, TaxOperationContext taxOperationContext);
	
	/**
	 * Adds a tax record to the persistent storage.
	 * 
	 * @param taxJournalRecord the record to add
	 * @return the tax journal record
	 */
	TaxJournalRecord add(TaxJournalRecord taxJournalRecord);

	/**
	 * Delete a tax record to related the document from the persistent storage.
	 *
	 * @param documentId the document ID, remove all tax related to it
	 */
	void deleteForDoc(TaxDocumentId documentId);
	
	/**
	 * Finds tax document records by a given tax document id and a given tax item code.
	 * 
	 * @param taxDocumentId tax document ID
	 * @param itemCode item code
	 * @return a list of tax journal records
	 */
	List<TaxJournalRecord> find(TaxDocumentId taxDocumentId, String itemCode);
	
	/**
	 * Finds a tax record from the persistent storage by id.
	 * 
	 * @param taxDocumentId the taxDocumentId ID
	 * @return the tax document or empty list if none found
	 */
	List<TaxJournalRecord> find(TaxDocumentId taxDocumentId);
	
	/**
	 * Finds a tax record from the persistent storage by order number.
	 * 
	 * @param orderNumber the order number
	 * @return the tax document or empty list if none found
	 */
	List<TaxJournalRecord> findByOrderNumber(String orderNumber);
	
	/**
	 * Finds tax document records by id and jounral type.
	 *
	 * @param taxDocumentId tax document ID
	 * @param taxJournalType item journal type {@link TaxJournalType}
	 * @return a list of tax journal records
	 */
	List<TaxJournalRecord> find(TaxDocumentId taxDocumentId, TaxJournalType taxJournalType);

	/**
	 * Gets the tax document transaction date.
	 * 
	 * @param taxDocumentId the given tax document id
	 * @param taxJournalType the tax journal type
	 * @return the tax document transaction date for the given tax document id
	 */
	Date findTaxDocumentTransactionDate(TaxDocumentId taxDocumentId, TaxJournalType taxJournalType);

	/**
	 * Save tax journals in a single transaction.
	 *
	 * @param taxJournalRecords a collection of tax journals.
	 */
	void addAll(Collection<TaxJournalRecord> taxJournalRecords);
}

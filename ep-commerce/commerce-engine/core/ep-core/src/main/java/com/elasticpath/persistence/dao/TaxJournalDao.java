/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.persistence.dao;

import java.util.List;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.order.TaxJournalRecord;
import com.elasticpath.plugin.tax.common.TaxJournalType;
import com.elasticpath.plugin.tax.domain.TaxDocumentId;

/**
 * Tax journal record DAO.
 */
public interface TaxJournalDao {

	/**
	 * Adds a tax record to the persistent storage.
	 * 
	 * @param record the record to add
	 * @return the tax journal record
	 */
	TaxJournalRecord add(TaxJournalRecord record);

	/**
	 * Finds a tax record from the persistent storage by id.
	 * 
	 * @param documentId the document ID
	 * @return the tax document or empty list if none found
	 */
	List<TaxJournalRecord> find(TaxDocumentId documentId);
	
	/**
	 * Finds a tax record from the persistent storage by order number.
	 * 
	 * @param orderNumber the order number
	 * @return the tax document or empty list if none found
	 */
	List<TaxJournalRecord> findByOrderNumber(String orderNumber);
	
	/**
	 * Finds a tax record from the persistent storage by id and journal type.
	 * 
	 * @param taxDocumentId tax document ID
	 * @param journalType item journal type {@link TaxJournalType}
	 * @return a list of tax journal records
	 */
	List<TaxJournalRecord> find(TaxDocumentId taxDocumentId, TaxJournalType journalType);

	/**
	 * Finds a tax record from the persistent storage by id and item code.
	 * 
	 * @param taxDocumentId tax document ID
	 * @param itemCode item code
	 * @return a list of tax journal records
	 */
	List<TaxJournalRecord> find(TaxDocumentId taxDocumentId, String itemCode);
	
	/**
	 * Finds a tax record from the persistent storage by id, item code and journal type.
	 *
	 * @param taxDocumentId tax document ID
	 * @param itemCode item code
	 * @param journalType item journal type {@link TaxJournalType}
	 * @return a list of tax journal records
	 */
	List<TaxJournalRecord> find(TaxDocumentId taxDocumentId, String itemCode, TaxJournalType journalType);
	
	/**
	 * Delete a tax record to the persistent storage.
	 *
	 * @param record the record to remove
	 * @throws EpServiceException - in case of any errors
	 */
	void delete(TaxJournalRecord record) throws EpServiceException;
	
	/**
	 * Delete a tax record to related the document from the persistent storage.
	 *
	 * @param documentId the document ID, remove all tax related to it
	 * @throws EpServiceException - in case of any errors
	 */
	void deleteForDoc(TaxDocumentId documentId) throws EpServiceException;
	
}

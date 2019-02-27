/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.persistence.dao.impl;

import java.util.List;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.order.TaxJournalRecord;
import com.elasticpath.persistence.api.FlushMode;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.dao.TaxJournalDao;
import com.elasticpath.plugin.tax.common.TaxJournalType;
import com.elasticpath.plugin.tax.domain.TaxDocumentId;

/**
 * Order tax record DAO.
 */
public class TaxJournalDaoImpl implements TaxJournalDao {

	private PersistenceEngine persistenceEngine;
	
	@Override
	public TaxJournalRecord add(final TaxJournalRecord record) {
		getPersistenceEngine().save(record);
		return record;
	}
	
	@Override
	public List<TaxJournalRecord> find(final TaxDocumentId documentId) {
		return getPersistenceEngine().retrieveByNamedQuery(
				"FIND_TAX_JOURNAL_RECORD_BY_DOC_ID", FlushMode.COMMIT,
				new Object[] { documentId.toString() });
	}
	
	@Override
	public List<TaxJournalRecord> findByOrderNumber(final String orderNumber) {
		return getPersistenceEngine().retrieveByNamedQuery(
				"FIND_TAX_JOURNAL_RECORD_BY_ORDER_NUMBER", FlushMode.COMMIT,
				new Object[] { orderNumber });
	}
	
	@Override
	public List<TaxJournalRecord> find(final TaxDocumentId taxDocumentId, final TaxJournalType journalType) {
		return getPersistenceEngine().retrieveByNamedQuery(
				"FIND_TAX_JOURNAL_RECORD_BY_DOC_ID_AND_JOURNAL_TYPE", FlushMode.COMMIT,
				new Object[] { taxDocumentId.toString(), journalType.toString() });
	}
	
	@Override
	public List<TaxJournalRecord> find(final TaxDocumentId taxDocumentId, final String itemCode) {
		return getPersistenceEngine().retrieveByNamedQuery(
				"FIND_TAX_JOURNAL_RECORD_BY_DOC_ID_AND_ITEM_CODE", FlushMode.COMMIT,
				new Object[] { taxDocumentId.toString(), itemCode });
	}
	
	@Override
	public List<TaxJournalRecord> find(final TaxDocumentId taxDocumentId, 
										final String itemCode,
										final TaxJournalType journalType) {
		return getPersistenceEngine().retrieveByNamedQuery(
				"FIND_TAX_JOURNAL_RECORD_BY_DOC_ID_AND_ITEM_CODE_AND_JOURNAL_TYPE", FlushMode.COMMIT,
				new Object[] { taxDocumentId.toString(), itemCode, journalType.toString() });
	}
	
	@Override
	public void delete(final TaxJournalRecord record) throws EpServiceException {
		if (record != null) {
			getPersistenceEngine().delete(record);
		}
	}
	
	@Override
	public void deleteForDoc(final TaxDocumentId documentID) throws EpServiceException {
		if (documentID != null) {
			sanityCheck();
			List<TaxJournalRecord> recordList = find(documentID, TaxJournalType.PURCHASE);
			if (recordList != null && !recordList.isEmpty()) {
				for (TaxJournalRecord record : recordList) {
					getPersistenceEngine().delete(record);
				}
			}
		}
	}
	
	/**
	 * Sanity check of this service instance.
	 * @throws EpServiceException - if something goes wrong.
	 */
	protected void sanityCheck() throws EpServiceException {
		if (getPersistenceEngine() == null) {
			throw new EpServiceException("The persistence engine is not correctly initialized.");
		}
	}

	protected PersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}

	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}

}

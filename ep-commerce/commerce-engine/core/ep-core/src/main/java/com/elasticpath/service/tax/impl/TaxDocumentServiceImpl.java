/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.service.tax.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.elasticpath.domain.order.TaxJournalRecord;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.dao.TaxJournalDao;
import com.elasticpath.plugin.tax.common.TaxJournalType;
import com.elasticpath.plugin.tax.domain.TaxAddress;
import com.elasticpath.plugin.tax.domain.TaxDocument;
import com.elasticpath.plugin.tax.domain.TaxDocumentId;
import com.elasticpath.plugin.tax.domain.TaxOperationContext;
import com.elasticpath.service.tax.TaxDocumentService;

/**
 * Implementation of {@link TaxDocumentService}.
 */
public class TaxDocumentServiceImpl implements TaxDocumentService {

	private TaxJournalDao taxJournalDao;
	
	private TaxDocumentAdapter taxDocumentAdapter;

	private PersistenceEngine persistenceEngine;
	
	@Override
	public TaxDocument buildTaxDocument(final TaxDocumentId taxDocumentId, 
										final TaxAddress destinationAddress,
										final TaxAddress originAddress,
										final TaxJournalType taxJournalType) {

		List<TaxJournalRecord> taxJournalRecords = getTaxJournalDao().find(taxDocumentId, taxJournalType);
		
		return getTaxDocumentAdapter().toTaxDocument(taxJournalRecords, destinationAddress, originAddress);
	}
	
	@Override
	public List<TaxJournalRecord> buildTaxJournalRecords(final TaxDocument taxDocument, final TaxOperationContext taxOperationContext) {
		return getTaxDocumentAdapter().toTaxJournalRecords(taxDocument, taxOperationContext);
	}
	
	@Override
	public TaxJournalRecord add(final TaxJournalRecord taxJournalRecord) {
		return getTaxJournalDao().add(taxJournalRecord);
	}
	
	@Override
	public List<TaxJournalRecord> find(final TaxDocumentId taxDocumentId, final String itemCode) {
		return getTaxJournalDao().find(taxDocumentId, itemCode);
	}

	@Override
	public List<TaxJournalRecord> find(final TaxDocumentId taxDocumentId) {
		return getTaxJournalDao().find(taxDocumentId);
	}
	
	@Override
	public List<TaxJournalRecord> findByOrderNumber(final String orderNumber) {
		return getTaxJournalDao().findByOrderNumber(orderNumber);
	}

	@Override
	public List<TaxJournalRecord> find(final TaxDocumentId taxDocumentId, final TaxJournalType taxJournalType) {
		return getTaxJournalDao().find(taxDocumentId, taxJournalType);
	}

	@Override
	public void deleteForDoc(final TaxDocumentId documentId) {
		getTaxJournalDao().deleteForDoc(documentId);
	}
	
	@Override
	public Date findTaxDocumentTransactionDate(final TaxDocumentId taxDocumentId, final TaxJournalType taxJournalType) {
		
		List<TaxJournalRecord> taxJournalRecords = getTaxJournalDao().find(taxDocumentId, taxJournalType);
		
		if (CollectionUtils.isEmpty(taxJournalRecords)) {
			return null;
		} 
		
		return taxJournalRecords.get(0).getTransactionDate();
	}
	
	public TaxJournalDao getTaxJournalDao() {
		return taxJournalDao;
	}

	public void setTaxJournalDao(final TaxJournalDao taxJournalDao) {
		this.taxJournalDao = taxJournalDao;
	}

	public TaxDocumentAdapter getTaxDocumentAdapter() {
		return taxDocumentAdapter;
	}

	public void setTaxDocumentAdapter(final TaxDocumentAdapter taxDocumentAdapter) {
		this.taxDocumentAdapter = taxDocumentAdapter;
	}

	@Override
	public void addAll(final Collection<TaxJournalRecord> taxJournalRecords) {
		for (TaxJournalRecord taxJournalRecord : taxJournalRecords) {
			persistenceEngine.save(taxJournalRecord);
		}
	}

	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}
}

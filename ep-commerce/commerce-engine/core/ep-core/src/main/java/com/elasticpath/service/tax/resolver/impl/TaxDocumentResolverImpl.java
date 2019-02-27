/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.service.tax.resolver.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.elasticpath.domain.order.TaxJournalRecord;
import com.elasticpath.plugin.tax.common.TaxJournalType;
import com.elasticpath.plugin.tax.domain.TaxDocumentId;
import com.elasticpath.plugin.tax.resolver.TaxDocumentResolver;
import com.elasticpath.service.tax.TaxDocumentService;

/**
 * The default implementation of {@link TaxDocumentResolver} for retrieving tax document related data from EP tax journal history.
 */
public class TaxDocumentResolverImpl implements TaxDocumentResolver {

	private TaxDocumentService taxDocumentService;
	
	@Override
	public String findTaxProvider(final String taxItemId, final TaxDocumentId taxDocumentId) {
		
		List<TaxJournalRecord> records = getTaxDocumentService().find(taxDocumentId, taxItemId);
		
		if (CollectionUtils.isEmpty(records)) {
			return null;
		}
		
		return records.get(0).getTaxProvider();
	}
	
	@Override
	public Date findCreationDate(final TaxDocumentId taxDocumentId, final TaxJournalType taxJournalType) {
		
		List<TaxJournalRecord> records = getTaxDocumentService().find(taxDocumentId, taxJournalType);
		
		if (CollectionUtils.isEmpty(records)) {
			return null;
		}
		
		return records.get(0).getTransactionDate();
	}
	
	public TaxDocumentService getTaxDocumentService() {
		return taxDocumentService;
	}

	public void setTaxDocumentService(final TaxDocumentService taxDocumentService) {
		this.taxDocumentService = taxDocumentService;
	}

}

/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.service.tax.impl;

import com.elasticpath.plugin.tax.domain.TaxDocument;
import com.elasticpath.plugin.tax.domain.TaxOperationContext;
import com.elasticpath.plugin.tax.manager.TaxDocumentArchiver;
import com.elasticpath.plugin.tax.resolver.TaxOperationResolvers;
import com.elasticpath.service.tax.TaxDocumentService;

/**
 * Implementation of {@link TaxDocumentArchiver} that persists the tax document to the tax journal table.
 */
public class TaxDocumentArchiverImpl implements TaxDocumentArchiver {
	
	private TaxDocumentService taxDocumentService;
	
	@Override
	public void archive(final TaxDocument document, 
						final TaxOperationContext taxOperationContext,
						final TaxOperationResolvers taxOperationResolvers) {

		getTaxDocumentService().addAll(getTaxDocumentService().buildTaxJournalRecords(document, taxOperationContext));
	}
	
	@Override
	public void delete(final TaxDocument document) {
		getTaxDocumentService().deleteForDoc(document.getDocumentId());
	}

	public TaxDocumentService getTaxDocumentService() {
		return taxDocumentService;
	}

	public void setTaxDocumentService(final TaxDocumentService taxDocumentService) {
		this.taxDocumentService = taxDocumentService;
	}

}

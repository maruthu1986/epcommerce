/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.manager.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.elasticpath.plugin.tax.TaxProviderPluginInvoker;
import com.elasticpath.plugin.tax.capability.StorageCapability;
import com.elasticpath.plugin.tax.common.TaxJournalType;
import com.elasticpath.plugin.tax.domain.TaxDocument;
import com.elasticpath.plugin.tax.domain.TaxDocumentId;
import com.elasticpath.plugin.tax.domain.TaxOperationContext;
import com.elasticpath.plugin.tax.domain.TaxableItemContainer;
import com.elasticpath.plugin.tax.domain.TaxedItemContainer;
import com.elasticpath.plugin.tax.domain.impl.MutableTaxDocument;
import com.elasticpath.plugin.tax.domain.impl.MutableTaxedItemContainer;
import com.elasticpath.plugin.tax.manager.TaxDocumentArchiver;
import com.elasticpath.plugin.tax.manager.TaxManager;
import com.elasticpath.plugin.tax.selection.TaxProviderSelector;

/**
 * The default {@link TaxManager} implementation.
 */
public class TaxManagerImpl implements TaxManager {

	private TaxProviderSelector taxProviderSelector;

	private TaxDocumentArchiver archiver;
	
	@Override
	public TaxDocument calculate(final TaxableItemContainer taxableContainer) {
		
		if (CollectionUtils.isEmpty(taxableContainer.getItems())) {
			
			return getTaxDocumentWithoutTaxItems(taxableContainer);
		}
		
		TaxOperationContext taxOperationContext = taxableContainer.getTaxOperationContext();
		
		TaxProviderPluginInvoker taxProvider = getTaxProviderSelector().findProvider(taxableContainer, taxOperationContext);

		TaxedItemContainer taxedItemContainer = taxProvider.calculateTaxes(taxableContainer);

		return create(taxedItemContainer, taxOperationContext.getDocumentId(), taxProvider.getName(), taxOperationContext.getJournalType());
	}
	
	@Override
	public void commitDocument(final TaxDocument taxDocument, final TaxOperationContext taxOperationContext) {
		
		// persists into local system, such as EP database
		getTaxDocumentArchiver().archive(taxDocument, taxOperationContext, null);

		// call provider to archive the document to the provider's external system, such as Avalara tax service
		TaxProviderPluginInvoker taxProvider = getTaxProviderSelector().findProviderByName(taxDocument.getTaxProviderName());
		
		StorageCapability capability = taxProvider.getCapability(StorageCapability.class);
		
		if (capability != null) {
			capability.archive(taxDocument, taxOperationContext);
		}
	}

	@Override
	public void deleteDocument(final TaxDocument taxDocument, final TaxOperationContext taxOperationContext) {
		getTaxDocumentArchiver().delete(taxDocument);
		
		// call provider to delete (void) the document to the provider's external system, such as Avalara tax service
		TaxProviderPluginInvoker taxProvider = getTaxProviderSelector().findProviderByName(taxDocument.getTaxProviderName());
		
		StorageCapability capability = taxProvider.getCapability(StorageCapability.class);
		
		if (capability != null) {
			capability.delete(taxDocument, taxOperationContext);
		}
	}
	
	/**
     * Creates a tax document.
     *
	 * @param taxedItemContainer the taxed item container
	 * @param taxDocumentId tax document ID. Could be null.
	 * @param taxProviderName the tax provider name
	 * @param taxJournalType the tax journal type
	 * @return the tax document
	 */
	protected TaxDocument create(final TaxedItemContainer taxedItemContainer, 
									final TaxDocumentId taxDocumentId,
									final String taxProviderName,
									final TaxJournalType taxJournalType) {
		MutableTaxDocument document = new MutableTaxDocument();
		
		document.setDocumentId(taxDocumentId);
		document.setTaxedItemContainer(taxedItemContainer);
		document.setTaxProviderName(taxProviderName);
		document.setJournalType(taxJournalType);
		
		return document;
	}
	
	private TaxDocument getTaxDocumentWithoutTaxItems(final TaxableItemContainer taxableContainer) {
		return create(getDefaultTaxedItemContainer(taxableContainer), 
						taxableContainer.getTaxOperationContext().getDocumentId(),
						StringUtils.EMPTY,
						taxableContainer.getTaxOperationContext().getJournalType());
	}
	
	private TaxedItemContainer getDefaultTaxedItemContainer(final TaxableItemContainer taxableContainer) {
		MutableTaxedItemContainer result = new MutableTaxedItemContainer();
		result.initialize(taxableContainer);
		
		return result;
	}

	public TaxDocumentArchiver getTaxDocumentArchiver() {
		return archiver;
	}

	public void setTaxDocumentArchiver(final TaxDocumentArchiver archiver) {
		this.archiver = archiver;
	}

	public TaxProviderSelector getTaxProviderSelector() {
		return taxProviderSelector;
	}

	public void setTaxProviderSelector(final TaxProviderSelector taxProviderSelector) {
		this.taxProviderSelector = taxProviderSelector;
	}
}

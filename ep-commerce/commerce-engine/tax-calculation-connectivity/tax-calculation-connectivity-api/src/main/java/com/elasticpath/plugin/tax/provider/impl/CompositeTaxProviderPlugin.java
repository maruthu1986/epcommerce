/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.provider.impl;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.MapUtils;

import com.elasticpath.plugin.tax.TaxProviderPluginInvoker;
import com.elasticpath.plugin.tax.capability.StorageCapability;
import com.elasticpath.plugin.tax.capability.TaxCalculationCapability;
import com.elasticpath.plugin.tax.capability.TaxExemptionCapability;
import com.elasticpath.plugin.tax.domain.TaxDocument;
import com.elasticpath.plugin.tax.domain.TaxOperationContext;
import com.elasticpath.plugin.tax.domain.TaxableItem;
import com.elasticpath.plugin.tax.domain.TaxableItemContainer;
import com.elasticpath.plugin.tax.domain.TaxedItem;
import com.elasticpath.plugin.tax.domain.TaxedItemContainer;
import com.elasticpath.plugin.tax.selection.impl.CompositeTaxProviderSelectionStrategy;
import com.elasticpath.plugin.tax.selection.impl.MapTaxProviderSelector;

/**
 * Composite tax provider which find multiple tax providers based on {@link CompositeTaxProviderSelectionStrategy}, 
 * groups the taxable items by the providers, and invokes the tax providers respectively
 * to calculate taxes and archive/delete tax documents for the grouped taxable items.
 */
public class CompositeTaxProviderPlugin extends AbstractCompositeTaxProviderPlugin implements TaxCalculationCapability,
		TaxExemptionCapability, StorageCapability {

	/**
	 * Composite Tax Provider name.
	 */
	public static final String PROVIDER_NAME = "CompositeTax";
	
	private CompositeTaxProviderSelectionStrategy compositeTaxProviderSelectionStrategy;
	private MapTaxProviderSelector mapTaxProviderSelector;
	
	@Override
	public String getName() {
		return PROVIDER_NAME;
	}
	
	@Override
	public TaxedItemContainer calculate(final TaxableItemContainer container) {
		
		//splits the taxable container to multiple containers for various tax provider based on selection strategy
		Map<TaxProviderPluginInvoker, List<TaxableItem>> providerItems =
				getCompositeTaxProviderSelectionStrategy().groupTaxableItems(container.getItems());
		
		Map<TaxProviderPluginInvoker, TaxableItemContainer> taxProviderContainers = populateProviderContainers(providerItems, container);
		
		// if only one provider for the return tax calculation, then return its result
		if (MapUtils.isNotEmpty(taxProviderContainers) && taxProviderContainers.size() == 1) {
			Entry<TaxProviderPluginInvoker, TaxableItemContainer> taxProviderContainer = taxProviderContainers.entrySet().iterator().next();
			return taxProviderContainer.getKey().calculateTaxes(taxProviderContainer.getValue());
		}
		
		return calculateAndMerge(container, taxProviderContainers);
	}
	
	@Override
	public void archive(final TaxDocument taxDocument, final TaxOperationContext taxOperationContext) {
		
		Map<String, List<TaxedItem>> providerItems = groupTaxItemsByTaxProvider(taxDocument.getTaxedItemContainer().getItems());
		Map<String, TaxDocument> taxProviderDocuments = 
				populateProviderDocuments(taxDocument, providerItems, taxOperationContext.getJournalType());
		
		for (Entry<String, TaxDocument> entry : taxProviderDocuments.entrySet()) {
			TaxProviderPluginInvoker taxProvider = getMapTaxProviderSelector().findProviderByKey(entry.getKey());
			
			StorageCapability capability = taxProvider.getCapability(StorageCapability.class);
			
			if (capability != null) {
				capability.archive(entry.getValue(), taxOperationContext);
			}
		}
	}
	
	@Override
	public void delete(final TaxDocument taxDocument, final TaxOperationContext taxOperationContext) {
		
		Map<String, List<TaxedItem>> providerItems = groupTaxItemsByTaxProvider(taxDocument.getTaxedItemContainer().getItems());
		Map<String, TaxDocument> taxProviderDocuments = 
				populateProviderDocuments(taxDocument, providerItems, taxOperationContext.getJournalType());
		
		for (Entry<String, TaxDocument> entry : taxProviderDocuments.entrySet()) {
			TaxProviderPluginInvoker taxProvider = getMapTaxProviderSelector().findProviderByKey(entry.getKey());
			
			StorageCapability capability = taxProvider.getCapability(StorageCapability.class);
			
			if (capability != null) {
				capability.delete(entry.getValue(), taxOperationContext);
			}
		}
	}
	
	public CompositeTaxProviderSelectionStrategy getCompositeTaxProviderSelectionStrategy() {
		return compositeTaxProviderSelectionStrategy;
	}

	public void setCompositeTaxProviderSelectionStrategy(final CompositeTaxProviderSelectionStrategy compositeTaxProviderSelectionStrategy) {
		this.compositeTaxProviderSelectionStrategy = compositeTaxProviderSelectionStrategy;
	}
	
	public MapTaxProviderSelector getMapTaxProviderSelector() {
		return mapTaxProviderSelector;
	}

	public void setMapTaxProviderSelector(final MapTaxProviderSelector mapTaxProviderSelector) {
		this.mapTaxProviderSelector = mapTaxProviderSelector;
	}
}

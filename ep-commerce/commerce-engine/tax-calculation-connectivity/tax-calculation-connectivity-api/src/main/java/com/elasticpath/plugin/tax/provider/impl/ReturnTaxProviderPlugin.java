/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.provider.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.plugin.tax.TaxProviderPluginInvoker;
import com.elasticpath.plugin.tax.capability.StorageCapability;
import com.elasticpath.plugin.tax.capability.TaxCalculationCapability;
import com.elasticpath.plugin.tax.capability.TaxExemptionCapability;
import com.elasticpath.plugin.tax.domain.TaxDocument;
import com.elasticpath.plugin.tax.domain.TaxDocumentId;
import com.elasticpath.plugin.tax.domain.TaxOperationContext;
import com.elasticpath.plugin.tax.domain.TaxableItem;
import com.elasticpath.plugin.tax.domain.TaxableItemContainer;
import com.elasticpath.plugin.tax.domain.TaxedItem;
import com.elasticpath.plugin.tax.domain.TaxedItemContainer;
import com.elasticpath.plugin.tax.domain.impl.StringTaxDocumentId;
import com.elasticpath.plugin.tax.resolver.TaxDocumentResolver;
import com.elasticpath.plugin.tax.selection.impl.MapTaxProviderSelector;

/**
 * Tax provider for return taxes, which uses {@link TaxDocumentResolver} to find multiple tax providers for the
 * returning {@link TaxableItemContainer}, groups the taxable items by the providers, and invokes the tax providers
 * respectively to calculate taxes and archive tax documents for the grouped taxable items. 
 */
public class ReturnTaxProviderPlugin extends AbstractCompositeTaxProviderPlugin implements TaxCalculationCapability,
		TaxExemptionCapability, StorageCapability {

	private static final Logger LOG = Logger.getLogger(ReturnTaxProviderPlugin.class);
	
	/**
	 * Return Tax Provider name.
	 */
	public static final String PROVIDER_NAME = "ReturnTax";
	
	private MapTaxProviderSelector mapTaxProviderSelector;
	private TaxDocumentResolver taxDocumentResolver;
	
	@Override
	public String getName() {
		return PROVIDER_NAME;
	}
	
	@Override
	public TaxedItemContainer calculate(final TaxableItemContainer container) {
		
		TaxDocumentId originalDocumentId = StringTaxDocumentId.fromString(
				container.getTaxOperationContext().getTaxOverrideContext().getTaxOverrideDocumentId());
		
		//splits the taxable container to multiple containers for various tax provider based on selection strategy
		Map<TaxProviderPluginInvoker, List<TaxableItem>> providerItems = getTaxProviderTaxItems(originalDocumentId, container.getItems());
		
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
		// do nothing
	}

	private Map<TaxProviderPluginInvoker, List<TaxableItem>> getTaxProviderTaxItems(final TaxDocumentId documentId,
			final List<? extends TaxableItem> taxItems) {
		
		Map<TaxProviderPluginInvoker, List<TaxableItem>> providerItems = new HashMap<>();
		
		for (TaxableItem taxableItem : taxItems) {
			
			String taxProviderName = findTaxProviderNameByItemAndDocumentId(taxableItem.getItemCode(), documentId);
			
			TaxProviderPluginInvoker taxProvider = getMapTaxProviderSelector().findProviderByKey(taxProviderName);
			
			if (providerItems.get(taxProvider) == null) {
				providerItems.put(taxProvider, new ArrayList<>());
			}
			providerItems.get(taxProvider).add(taxableItem);
		}
		return providerItems;
	}
	
	
	private String findTaxProviderNameByItemAndDocumentId(final String itemId, final TaxDocumentId taxDocumentId) {
		
		String providerName = getTaxDocumentResolver().findTaxProvider(itemId,  taxDocumentId);
		
		if (StringUtils.isNotBlank(providerName)) {
			return providerName;
		}
		
		LOG.error("Error in return tax: could not find the tax journal records with the document id " + taxDocumentId);
		return StringUtils.EMPTY;
	}

	public TaxDocumentResolver getTaxDocumentResolver() {
		return taxDocumentResolver;
	}

	public void setTaxDocumentResolver(final TaxDocumentResolver taxDocumentResolver) {
		this.taxDocumentResolver = taxDocumentResolver;
	}

	public MapTaxProviderSelector getMapTaxProviderSelector() {
		return mapTaxProviderSelector;
	}

	public void setMapTaxProviderSelector(final MapTaxProviderSelector mapTaxProviderSelector) {
		this.mapTaxProviderSelector = mapTaxProviderSelector;
	}

}

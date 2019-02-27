/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.provider.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.plugin.tax.TaxProviderPluginInvoker;
import com.elasticpath.plugin.tax.common.TaxJournalType;
import com.elasticpath.plugin.tax.domain.TaxDocument;
import com.elasticpath.plugin.tax.domain.TaxableItem;
import com.elasticpath.plugin.tax.domain.TaxableItemContainer;
import com.elasticpath.plugin.tax.domain.TaxedItem;
import com.elasticpath.plugin.tax.domain.TaxedItemContainer;
import com.elasticpath.plugin.tax.domain.impl.MutableTaxDocument;
import com.elasticpath.plugin.tax.domain.impl.MutableTaxableItemContainer;
import com.elasticpath.plugin.tax.domain.impl.MutableTaxedItemContainer;
import com.elasticpath.plugin.tax.spi.AbstractTaxProviderPluginSPI;

/**
 * Abstract class to hold common methods for {@link CompositeTaxProviderPlugin} and {@link ReturnTaxProviderPlugin}, 
 * which need to calculate taxes and populate tax documents using multiple tax providers.
 * 
 */
public abstract class AbstractCompositeTaxProviderPlugin extends AbstractTaxProviderPluginSPI {

	private static final Logger LOG = Logger.getLogger(AbstractCompositeTaxProviderPlugin.class);
	
	/**
	 * Splits a {@link TaxableItemContainer} into multiple containers, one per tax provider.
	 *
	 * @param providerItems the map of tax providers and their corresponding taxable items
	 * @param container the original taxable container
	 * @return a map of tax provider and the corresponding taxable container
	 */
	protected Map<TaxProviderPluginInvoker, TaxableItemContainer> populateProviderContainers(
			final Map<TaxProviderPluginInvoker, List<TaxableItem>> providerItems, final TaxableItemContainer container) {

		Map<TaxProviderPluginInvoker, TaxableItemContainer> taxProviderContainers = new HashMap<>();

		for (Entry<TaxProviderPluginInvoker, List<TaxableItem>> entry : providerItems.entrySet()) {

			MutableTaxableItemContainer taxableContainer = new MutableTaxableItemContainer();
			taxableContainer.setDestinationAddress(container.getDestinationAddress());
			taxableContainer.setOriginAddress(container.getOriginAddress());
			taxableContainer.setCurrency(container.getCurrency());
			taxableContainer.setStoreCode(container.getStoreCode());
			taxableContainer.setTaxOperationContext(container.getTaxOperationContext());

			taxableContainer.setItems(entry.getValue());

			taxProviderContainers.put(entry.getKey(), taxableContainer);

		}

		return taxProviderContainers;
	}
	
	/**
	 * Merges multiple {@link TaxableItemContainer}s (one per tax provider) back into a single container.
	 *
	 * @param container the taxable item container
	 * @param taxProviderContainers the map of tax providers and their own taxable item containers
	 * @return a merged taxed item container
	 */
	protected TaxedItemContainer calculateAndMerge(final TaxableItemContainer container,
												final Map<TaxProviderPluginInvoker, TaxableItemContainer> taxProviderContainers) {

		List<TaxedItem> rawTaxedItems = new ArrayList<>();
		MutableTaxedItemContainer result = new MutableTaxedItemContainer();
		result.initialize(container);

		for (Entry<TaxProviderPluginInvoker, TaxableItemContainer> entry : taxProviderContainers.entrySet()) {

			TaxProviderPluginInvoker taxProvider = entry.getKey();

			TaxedItemContainer taxedContainer = taxProvider.calculateTaxes(entry.getValue());

			result.setTaxInclusive(taxedContainer.isTaxInclusive());

			for (TaxedItem item : taxedContainer.getItems()) {
				rawTaxedItems.add(item);
			}
		}

		List<TaxedItem> orderedTaxedItems = orderTaxedItem(getTaxItemOrdering(container), rawTaxedItems);

		for (TaxedItem orderedTaxedItem : orderedTaxedItems) {
			result.addTaxedItem(orderedTaxedItem);
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Tax provider " + getName()
					+ "Original taxableItemContainer : " + container + "\n\tMerged taxedItemContainer " + result);
		}
		return result;
	}

	/**
	 * Preserves original order of @{link TaxableItem}s, so item order can be restored when merging
	 * {@link TaxableItemContainer}s.
	 *
	 * @param container the taxable item container
	 * @return the ordering map of the taxable item
	 */
	protected Map<String, Integer> getTaxItemOrdering(final TaxableItemContainer container) {
		Map<String, Integer> taxableItemOrders = new HashMap<>();

		int ordering = 0;
		for (TaxableItem item : container.getItems()) {
			taxableItemOrders.put(item.getItemGuid(), Integer.valueOf(ordering));
			ordering++;
		}
		return taxableItemOrders;
	}

	/**
	 * Restores order of taxed items according to their original ordering in the taxable container.
	 *
	 * @param taxItemOrdering the given ordering of each taxable tiem
	 * @param rawTaxedItems the taxed items needed to be ordered
	 * @return a list of ordered taxed item
	 */
	protected List<TaxedItem> orderTaxedItem(final Map<String, Integer> taxItemOrdering, final List<TaxedItem> rawTaxedItems) {

		List<TaxedItem> taxedItems = new ArrayList<>();

		for (int index = 0; index < rawTaxedItems.size(); index++) {
			taxedItems.add(null);
		}

		for (TaxedItem item : rawTaxedItems) {
			taxedItems.set(taxItemOrdering.get(item.getItemGuid()), item);
		}

		return taxedItems;
	}
	
	/**
	 * Populates tax documents for tax providers from the original tax document and the grouped taxed items by individual tax provider.
	 *
	 * @param taxDocument the original tax document
	 * @param providerItems the grouped taxed items by individual tax provider
	 * @param taxJournalType the taxJournal type
	 * @return a map of tax provider and corresponding tax document
	 */
	protected Map<String, TaxDocument> populateProviderDocuments(final TaxDocument taxDocument,
																final Map<String, List<TaxedItem>> providerItems,
																final TaxJournalType taxJournalType) {

		Map<String, TaxDocument> taxProviderDocuments = new HashMap<>();

		for (Entry<String, List<TaxedItem>> entry : providerItems.entrySet()) {

			MutableTaxDocument document = new MutableTaxDocument();
			document.setDocumentId(taxDocument.getDocumentId());
			document.setTaxProviderName(entry.getKey());
			document.setJournalType(taxJournalType);

			MutableTaxedItemContainer taxedItemContainer = new MutableTaxedItemContainer();
			taxedItemContainer.setDestinationAddress(taxDocument.getTaxedItemContainer().getDestinationAddress());
			taxedItemContainer.setOriginAddress(taxDocument.getTaxedItemContainer().getOriginAddress());
			taxedItemContainer.setCurrency(taxDocument.getTaxedItemContainer().getCurrency());
			taxedItemContainer.setTaxInclusive(taxDocument.getTaxedItemContainer().isTaxInclusive());
			taxedItemContainer.setStoreCode(taxDocument.getTaxedItemContainer().getStoreCode());

			for (TaxableItem item : entry.getValue()) {
				taxedItemContainer.addTaxedItem((TaxedItem) item);
			}

			document.setTaxedItemContainer(taxedItemContainer);

			taxProviderDocuments.put(entry.getKey(), document);
		}

		return taxProviderDocuments;
	}
	
	/**
	 * Groups/splits an original collection of taxed items by their tax providers.
	 *
	 * @param taxItems the original collection of taxed items
	 * @return a map of tax providers and their taxed items
	 */
	protected Map<String, List<TaxedItem>> groupTaxItemsByTaxProvider(final List<? extends TaxedItem> taxItems) {

		Map<String, List<TaxedItem>> providerItems = new HashMap<>();

		for (TaxedItem taxedItem : taxItems) {

			String taxProviderName = findTaxProviderNameByTaxRecord(taxedItem);

			if (providerItems.get(taxProviderName) == null) {
				providerItems.put(taxProviderName, new ArrayList<>());
			}
			providerItems.get(taxProviderName).add(taxedItem);
		}
		return providerItems;
	}

	/**
	 * Finds a tax provider name for a given taxed item.
	 *
	 * @param taxedItem the taxed item
	 * @return the tax provider name
	 */
	private String findTaxProviderNameByTaxRecord(final TaxedItem taxedItem) {

		if (CollectionUtils.isNotEmpty(taxedItem.getTaxRecords())) {
			String taxProvideName = taxedItem.getTaxRecords().get(0).getTaxProvider();
			
			if (StringUtils.isNotBlank(taxProvideName)) {
				return taxProvideName;
			}
		}
		return StringUtils.EMPTY;
	}

}
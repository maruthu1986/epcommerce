/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.service.tax.impl;

import java.io.Serializable;
import java.util.List;

import com.google.common.base.Optional;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.apache.commons.lang3.SerializationUtils;

import com.elasticpath.plugin.tax.domain.TaxDocument;
import com.elasticpath.plugin.tax.domain.TaxOperationContext;
import com.elasticpath.plugin.tax.domain.TaxableItem;
import com.elasticpath.plugin.tax.domain.TaxableItemContainer;
import com.elasticpath.plugin.tax.domain.TaxedItem;
import com.elasticpath.plugin.tax.manager.TaxManager;

/**
 * Caching implementation of the {@link TaxManager} that caches the tax calculation results of the delegate
 * TaxManager implementation. This is enabled by default and eliminates redundant service calls to external
 * tax services.
 */
public class CachingTaxManagerImpl implements TaxManager {

	private Cache cache;
	private TaxManager delegateTaxManager;

	/**
	 * Retrieves the tax document for a given taxable container. If the document is cached, then the cached instance
	 * is returned, otherwise the delegate tax manager is called and the result added to cache.
	 * <p>
	 * A hashCode is calculated for all the items in the container and is used as the cache key. The hashCode counts everything
	 * in the taxable container except the taxable item GUID {@link TaxableItem}, which is the shopping item GUID.
	 * For the same shopping cart, the shopping item GUID is changed all the time without affecting tax calculations.
	 * But the GUID is very important when applying the tax result back to the shopping cart. so before returning the tax document,
	 * its taxedItem's GUID needs to be replaced by the corresponding taxable item id in the supplied taxable container.
	 *
	 * @param taxableContainer the taxable container
	 *
	 * @return the tax document containing the tax calculation result for the given taxable container
	 *
	 */
	@Override
	public TaxDocument calculate(final TaxableItemContainer taxableContainer) {

		TaxDocument taxDocument = null;

		Integer containerHashCode = taxableContainer.hashCode();
		Optional<TaxDocument> cachedTaxDocument = getFromCache(containerHashCode);

		if (cachedTaxDocument.isPresent()) {
			Optional<TaxDocument> updatedTaxDocument = replaceTaxItemGuids(taxableContainer, cachedTaxDocument.get());
			taxDocument = updatedTaxDocument.orNull();
		}

		if (taxDocument == null) {
			// if the document is not in the cache, or the taxItems of taxedItemContainer and the taxableItemContainer have different order
			TaxDocument newTaxDocument = delegateTaxManager.calculate(taxableContainer);
			saveInCache(newTaxDocument, containerHashCode);
			taxDocument = newTaxDocument;
		}

		return taxDocument;
	}

	/*
	 * The taxable items have GUIDs which do not affect tax calculations, but the GUIDs play important role when applying
	 *  the tax calculation results back to shopping cart, order shipment or order return. So, it is important to replace
	 *  the taxed items' GUIDs with the correct GUIDs from the given taxableContainer.
	*/
	private Optional<TaxDocument> replaceTaxItemGuids(final TaxableItemContainer taxableContainer, final TaxDocument cachedTaxDocument) {

		if (cachedTaxDocument instanceof Serializable) {
			//deep clone of TaxDocument and all the taxable items.
			TaxDocument clonedTaxDocument = (TaxDocument) SerializationUtils.clone((Serializable) cachedTaxDocument);
			List<? extends TaxedItem> cachedTaxedItems = clonedTaxDocument.getTaxedItemContainer().getItems();

			for (int index = 0; index < cachedTaxedItems.size(); index++) {
				TaxableItem taxableItem = taxableContainer.getItems().get(index);
				TaxableItem cachedTaxableItem = cachedTaxedItems.get(index).getTaxableItem();
				//out of order, not cacheable
				if (taxableItem.hashCode() != cachedTaxableItem.hashCode()) {
					return Optional.absent();
				}

				cachedTaxableItem.setItemGuid(taxableItem.getItemGuid());
			}

			return Optional.of(clonedTaxDocument);
		}

		return Optional.absent();
	}

	@Override
	public void commitDocument(final TaxDocument document, final TaxOperationContext taxOperationContext) {
		delegateTaxManager.commitDocument(document, taxOperationContext);
	}

	@Override
	public void deleteDocument(final TaxDocument document, final TaxOperationContext taxOperationContext) {
		delegateTaxManager.deleteDocument(document, taxOperationContext);
	}

	private void saveInCache(final TaxDocument document, final Integer containerHashCode) {
		Element element = new Element(containerHashCode, document);
		getCache().put(element);
	}

	private Optional<TaxDocument> getFromCache(final Integer containerHashCode) {
			Element element = getCache().get(containerHashCode);

		if (element != null && !element.isExpired()) {
			return Optional.of((TaxDocument) element.getObjectValue());
		}
		return Optional.absent();
	}

	public Cache getCache() {
		return cache;
	}

	public void setCache(final Cache cache) {
		this.cache = cache;
	}

	public TaxManager getDelegateTaxManager() {
		return delegateTaxManager;
	}

	public void setDelegateTaxManager(final TaxManager delegateTaxManager) {
		this.delegateTaxManager = delegateTaxManager;
	}

}

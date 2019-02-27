/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.caching.core.pricing;

import java.util.List;

import com.elasticpath.cache.Cache;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.pricing.PriceListAssignment;
import com.elasticpath.service.pricing.PriceListAssignmentService;

/**
 * Caching price list assignment service.
 */
public class CachingPriceListAssignmentServiceImpl implements PriceListAssignmentService {

	private PriceListAssignmentService fallbackService;
	private Cache<CatalogAndCurrencyCodeAndHiddenCompositeKey, List<PriceListAssignment>> priceListAssignmentCache;

	@Override
	public PriceListAssignment saveOrUpdate(final PriceListAssignment plAssignment) {
		return fallbackService.saveOrUpdate(plAssignment);
	}

	@Override
	public PriceListAssignment findByGuid(final String guid) {
		return fallbackService.findByGuid(guid);
	}

	@Override
	public PriceListAssignment findByName(final String name) {
		return fallbackService.findByName(name);
	}

	@Override
	public List<PriceListAssignment> list() {
		return fallbackService.list();
	}

	@Override
	public List<PriceListAssignment> list(final boolean includeHidden) {
		return fallbackService.list(includeHidden);
	}

	@Override
	public List<PriceListAssignment> listByCatalogAndCurrencyCode(final String catalogCode, final String currencyCode) {
		return listByCatalogAndCurrencyCode(catalogCode, currencyCode, false);
	}

	@Override
	public List<PriceListAssignment> listByCatalogAndCurrencyCode(final String catalogCode, final String currencyCode, final boolean includeHidden) {
		CatalogAndCurrencyCodeAndHiddenCompositeKey key = new CatalogAndCurrencyCodeAndHiddenCompositeKey(catalogCode, currencyCode, includeHidden);
		List<PriceListAssignment> priceListAssignments = priceListAssignmentCache.get(key);

		if (priceListAssignments != null) {
			return priceListAssignments;
		}

		priceListAssignments = fallbackService.listByCatalogAndCurrencyCode(catalogCode, currencyCode, includeHidden);
		priceListAssignmentCache.put(key, priceListAssignments);
		return priceListAssignments;
	}

	@Override
	public List<PriceListAssignment> listByCatalogAndPriceListNames(final String catalogName, final String priceListName) {
		return fallbackService.listByCatalogAndPriceListNames(catalogName, priceListName);
	}

	@Override
	public List<PriceListAssignment> listByCatalogAndPriceListNames(final String catalogName, final String priceListName,
																	final boolean includeHidden) {
		return fallbackService.listByCatalogAndPriceListNames(catalogName, priceListName, includeHidden);
	}

	@Override
	public List<PriceListAssignment> listByCatalog(final Catalog catalog) {
		return fallbackService.listByCatalog(catalog);
	}

	@Override
	public List<PriceListAssignment> listByCatalog(final Catalog catalog, final boolean includeHidden) {
		return fallbackService.listByCatalog(catalog, includeHidden);
	}

	@Override
	public List<PriceListAssignment> listByCatalog(final String catalogCode) {
		return fallbackService.listByCatalog(catalogCode);
	}

	@Override
	public List<PriceListAssignment> listByCatalog(final String catalogCode, final boolean includeHidden) {
		return fallbackService.listByCatalog(catalogCode, includeHidden);
	}

	@Override
	public List<PriceListAssignment> listByPriceList(final String priceListGuid) {
		return fallbackService.listByPriceList(priceListGuid);
	}

	@Override
	public List<String> listAssignedCatalogsCodes() {
		return fallbackService.listAssignedCatalogsCodes();
	}

	@Override
	public void delete(final PriceListAssignment plAssignment) {
		fallbackService.delete(plAssignment);
	}

	public void setFallbackService(final PriceListAssignmentService fallbackService) {
		this.fallbackService = fallbackService;
	}

	public void setPriceListAssignmentCache(
			final Cache<CatalogAndCurrencyCodeAndHiddenCompositeKey, List<PriceListAssignment>> priceListAssignmentCache) {
		this.priceListAssignmentCache = priceListAssignmentCache;
	}
}

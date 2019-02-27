/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.pricing.impl;

import java.util.Currency;

import com.elasticpath.common.pricing.service.PriceListLookupService;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.pricing.PriceListStack;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.pricing.SessionPriceListLifecycle;
import com.elasticpath.tags.TagSet;

/**
 * Lazy PriceList lifecycle implementation that only updates when the
 * Shoppers personalisation/pricing context has changed.
 */
public class SessionPriceListLifecycleImpl implements SessionPriceListLifecycle {

	private PriceListLookupService priceListLookupService;

	@Override
	public void refreshPriceListStack(final CustomerSession customerSession, final Store store) {
		final Catalog catalog = store.getCatalog();
		String catalogCode = catalog.getCode();
		refreshPriceListStack(customerSession.getShopper(), catalogCode);
	}

	@Override
	public void refreshPriceListStack(final Shopper shopper, final String catalogCode) {
		if (!shopper.isPriceListStackValid()) {
			Currency currency = shopper.getCurrency();
			TagSet customerTagSet = shopper.getTagSet();
			PriceListStack priceListStack = priceListLookupService.getPriceListStack(catalogCode, currency, customerTagSet);
			shopper.setPriceListStack(priceListStack);
		}
	}

	/**
	 * Set the {@link PriceListLookupService}.
	 * @param priceListLookupService instance to set.
	 */
	public void setPriceListLookupService(final PriceListLookupService priceListLookupService) {
		this.priceListLookupService = priceListLookupService;
	}
}
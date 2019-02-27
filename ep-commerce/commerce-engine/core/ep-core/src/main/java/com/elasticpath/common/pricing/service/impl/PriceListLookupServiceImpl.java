/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.common.pricing.service.impl;

import java.util.Currency;

import com.elasticpath.common.pricing.service.PriceListLookupService;
import com.elasticpath.common.pricing.service.PriceListStackLookupStrategy;
import com.elasticpath.domain.pricing.PriceListStack;
import com.elasticpath.tags.TagSet;

/**
 *	Implementation of the Service for looking up price list descriptor GUIDs.
 */
public class PriceListLookupServiceImpl implements PriceListLookupService {
	
	private PriceListStackLookupStrategy plStackLookupStrategy;

	@Override	
	public PriceListStack getPriceListStack(final String catalogCode, final Currency currency, final TagSet tagSet) {
		return getPlStackLookupStrategy().getPriceListStack(catalogCode, currency, tagSet);
	}

	/**
	 *
	 * @param plStackLookupStrategy the plStackLookupStrategy to set
	 */
	public void setPlStackLookupStrategy(final PriceListStackLookupStrategy plStackLookupStrategy) {
		this.plStackLookupStrategy = plStackLookupStrategy;
	}

	/**
	 *
	 * @return the plStackLookupStrategy
	 */
	public PriceListStackLookupStrategy getPlStackLookupStrategy() {
		return plStackLookupStrategy;
	}
	
}

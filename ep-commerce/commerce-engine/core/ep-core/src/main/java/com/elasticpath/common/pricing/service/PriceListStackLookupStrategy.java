/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.common.pricing.service;

import java.util.Currency;

import com.elasticpath.domain.pricing.PriceListStack;
import com.elasticpath.tags.TagSet;

/**
 * Strategy for constructing {@link PriceListStack}.
 */
public interface PriceListStackLookupStrategy {

	/**
	 * @param catalogCode the catalog code
	 * @param currency {@link Currency}
	 * @param tagSet {@link TagSet}
	 * @return constructed {@link PriceListStack}
	 */
	PriceListStack getPriceListStack(String catalogCode,
			Currency currency, TagSet tagSet);
}

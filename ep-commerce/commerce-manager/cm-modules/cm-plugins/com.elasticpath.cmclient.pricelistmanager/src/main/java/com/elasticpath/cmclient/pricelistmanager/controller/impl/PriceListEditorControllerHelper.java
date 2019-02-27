/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.pricelistmanager.controller.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.domain.skuconfiguration.SkuOptionValue;

/**
 * Class contains different utility methods. 
 */
@SuppressWarnings({ "PMD.UseSingleton", "PMD.UseUtilityClass" })
public class PriceListEditorControllerHelper {

	private static final String COMMA = ","; //$NON-NLS-1$
	
	/**
	 * Formats sku option values into the single comma separated string for UI purposes.
	 *
	 * @param optionValues - sku option values.
	 * @param locale locale
	 * @return formatted  sku option values
	 */
	public static String formatSkuConfiguration(final Collection<SkuOptionValue> optionValues, final Locale locale) {
		StringBuffer buffer = new StringBuffer();
		for (Iterator<SkuOptionValue> iterator = optionValues.iterator(); iterator.hasNext();) {
			SkuOptionValue skuOptionValue = iterator.next();
			String displayName = skuOptionValue.getDisplayName(locale, false);
			if (StringUtils.isNotEmpty(displayName)) {
				buffer.append(displayName);
				if (iterator.hasNext()) {
					buffer.append(COMMA);
				}
			}
		}
		return buffer.toString();
	}
	
}

/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.helpers;

import java.util.Comparator;
import java.util.Locale;

import com.elasticpath.domain.catalog.Brand;
/**
 * The comparator defined for sorting brand list alphabetically.
 * 
 * @deprecated use BrandComparator from com.elasticpath.core project.
 */
@Deprecated
public class BrandComparator implements Comparator<Brand> {

	private final Locale locale;
	
	/**
	 * The constructor with locale passed in.
	 * @param locale the locale object for comparator.
	 */
	public BrandComparator(final Locale locale) {
		super();
		this.locale = locale;
	}

	
/**
 * implementing the compare method of the comparator.
 * @param brand1 the Brand object to be compared.
 * @param brand2 the Brand object to be compared.
 * @return &gt; 0 if brand1 &gt; brand2, 0 if brand1 = brand2, &lt;0 if brand1 &lt; brand2.
 * @deprecated use BrandComparator from com.elasticpath.core project.
 */
	@Deprecated
	public int compare(final Brand brand1, final Brand brand2) {
		final String brand1DisplayName = String.valueOf(brand1.getDisplayName(locale, true));
		final String brand2DisplayName = String.valueOf(brand2.getDisplayName(locale, true));
		return brand1DisplayName.compareToIgnoreCase(brand2DisplayName);
	}

}

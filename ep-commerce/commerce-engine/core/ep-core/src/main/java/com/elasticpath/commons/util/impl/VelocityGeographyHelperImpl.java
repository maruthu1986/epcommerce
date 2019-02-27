/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.commons.util.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;

import com.elasticpath.domain.misc.Geography;

/**
 * {@link Geography} helper class for velocity. This serves as a bridge to some of the old API as well as provides some
 * helper methods for things {@link Geography} doesn't provide you.
 */
public class VelocityGeographyHelperImpl {

	private static final String EMPTY_SUB_COUNTRY_MARKER = "---";
	private final Map<String, Map<String, String>> subCountriesWithDisplayname = new HashMap<>();
	private Map<String, String> countriesWithDisplayname;
	private final Geography geography;

	/**
	 * Default constructor.
	 * 
	 * @param geography base {@link Geography} data
	 */
	public VelocityGeographyHelperImpl(final Geography geography) {
		this.geography = geography;
	}

	/**
	 * Gets a sub-countries display name.
	 * 
	 * @param countryCode code for the country we are getting
	 * @param subCountryCode code for the sub-country we are getting
	 * @param locale {@link Locale} to get the name for
	 * @return display name for the sub-country
	 */
	public String getSubCountryDisplayName(final String countryCode, final String subCountryCode, final Locale locale) {
		return geography.getSubCountryDisplayName(countryCode, subCountryCode, locale);
	}

	/**
	 * Gets a countries display name.
	 * 
	 * @param countryCode code for the country we are getting
	 * @param locale {@link Locale} to get the name for
	 * @return display name for the country
	 */
	public String getCountryDisplayName(final String countryCode, final Locale locale) {
		return geography.getCountryDisplayName(countryCode, locale);
	}

	/**
	 * Old api callback. Calls {@link #getCountriesWithDisplayName(Locale)}.
	 * 
	 * @param locale {@link Locale} to get countries for
	 * @return map of country codes to country display names
	 */
	public Map<String, String> getCountries(final Locale locale) {
		return getCountriesWithDisplayName(locale);
	}

	/**
	 * For api compatbility. Just calls {@link #getSubCountriesWithDisplayName(String, Locale)}.
	 * 
	 * @param countryCode a country code
	 * @param locale {@link Locale} for the country
	 * @return map of sub-country codes to display names
	 */
	public Map<String, String> getSubCountries(final String countryCode, final Locale locale) {
		return getSubCountriesWithDisplayName(countryCode, locale);
	}

	/**
	 * Gets all the sub-countries and their display names in the given {@code countryCode} and {@link Locale}.
	 * 
	 * @param countryCode code of the country to get sub-countries for
	 * @param locale {@link Locale} for display names
	 * @return map of country codes to display names
	 */
	public Map<String, String> getSubCountriesWithDisplayName(final String countryCode, final Locale locale) {
		Map<String, String> result = subCountriesWithDisplayname.get(countryCode);
		if (result == null) {
			result = new LinkedHashMap<>();

			boolean addedOne = false;
			for (String code : geography.getSubCountryCodes(countryCode)) {
				result.put(code, geography.getSubCountryDisplayName(countryCode, code, locale));
				addedOne = true;
			}

			// this is purely for display purposes
			if (!addedOne) {
				result.put(EMPTY_SUB_COUNTRY_MARKER, EMPTY_SUB_COUNTRY_MARKER);
			}

			// sort result map based on values
			List<Entry<String, String>> entryList = new ArrayList<>(result.entrySet());
			Collections.sort(entryList, new MapValueComparator());

			result = new LinkedHashMap<>();
			for (Entry<String, String> entry : entryList) {
				result.put(entry.getKey(), entry.getValue());
			}

			subCountriesWithDisplayname.put(countryCode, result);
		}
		return result;
	}

	/**
	 * Gets all the countries and their display names in the given {@link Locale}.
	 * 
	 * @param locale {@link Locale} for display names
	 * @return map of country codes to display names
	 */
	public Map<String, String> getCountriesWithDisplayName(final Locale locale) {
		Map<String, String> result = countriesWithDisplayname;
		if (result == null) {
			result = new LinkedHashMap<>();

			for (String code : geography.getCountryCodes()) {
				result.put(code, geography.getCountryDisplayName(code, locale));
			}

			// sort result map based on values
			List<Entry<String, String>> entryList = new ArrayList<>(result.entrySet());
			Collections.sort(entryList, new MapValueComparator());

			result = new LinkedHashMap<>();
			for (Entry<String, String> entry : entryList) {
				result.put(entry.getKey(), entry.getValue());
			}

			countriesWithDisplayname = result;
		}
		return result;
	}

	/** {@link Comparator} which sorts {@link Entry} based on value. */
	private static class MapValueComparator implements Comparator<Entry<?, String>>, Serializable {

		private static final long serialVersionUID = -4062269107670298881L;

		@Override
		public int compare(final Entry<?, String> entry1, final Entry<?, String> entry2) {
			return Ordering.natural().nullsFirst()
				.onResultOf((Function<Entry<?, String>, String>) Entry::getValue)
				.compare(entry1, entry2);
		}
	}
}

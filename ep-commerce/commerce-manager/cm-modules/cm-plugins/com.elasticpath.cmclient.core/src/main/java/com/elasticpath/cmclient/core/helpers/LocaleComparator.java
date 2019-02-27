/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.helpers;

import java.util.Comparator;
import java.util.Locale;

/**
 * The comparator defined for sorting locale list alphabetically.
 */
public class LocaleComparator implements Comparator<Locale> {

	/**
	 * Comparares two locales by their display name.
	 * 
	 * @param locale1 locale 1
	 * @param locale2 locale 2
	 * @return > 0 if locale1.getDisplayName() > locale2.getDisplayName(), 
	 *         = 0 if locale1.getDisplayName() = locale2.getDisplayName(), 
	 *         < 0 if locale1.getDisplayName() < locale2.getDisplayName()
	 */
	public int compare(final Locale locale1, final Locale locale2) {
		return locale1.getDisplayName().compareTo(locale2.getDisplayName());
	}
}
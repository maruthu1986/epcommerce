/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */

package com.elasticpath.service.search.solr.query;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.util.ClientUtils;

/**
 * Helper class with static methods to support query composition.
 */
public final class QueryComposerHelper {

	private static final String STAR_SIGN = "*";

	private QueryComposerHelper() {
	}

	/**
	 * Escape and add wildcards in the beginning and the end of a string to enable partial search with Solr.
	 *
	 * @param string string to escape and surround by wildcards.
	 * @return string escaped and surrounded by wildcards.
	 */
	public static String escapeAndAddWildcards(final String string) {
		String escapedString = escape(string);
		if (StringUtils.isBlank(escapedString) || StringUtils.endsWith(escapedString, "*") || StringUtils.startsWith(escapedString, "*")) {
			return escapedString;
		}
		return addWildcards(escapedString);
	}

	/**
	 * Escape the string to enable partial search with Solr.
	 *
	 * @param string to escape.
	 * @return string escaped.
	 */
	public static String escape(final String string) {
		return ClientUtils.escapeQueryChars(string == null ? StringUtils.EMPTY : string);
	}

	/**
	 * Add wildcards to enable partial search with Solr.
	 *
	 * @param string to surround with wildcards.
	 * @return string with wildcards.
	 */
	public static String addWildcards(final String string) {
		return StringUtils.isBlank(string) ? STAR_SIGN : (STAR_SIGN + string + STAR_SIGN);
	}
}

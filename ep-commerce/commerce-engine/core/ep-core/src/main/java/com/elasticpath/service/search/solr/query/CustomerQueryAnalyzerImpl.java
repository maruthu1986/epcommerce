/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */

package com.elasticpath.service.search.solr.query;

import org.apache.solr.client.solrj.util.ClientUtils;

import com.elasticpath.service.search.solr.QueryAnalyzerImpl;

/**
 * Provides analyzing tools for <code>CustomerQueryComposer</code>.
 */
public class CustomerQueryAnalyzerImpl extends QueryAnalyzerImpl {
	@Override
	public String analyze(final String value) {
		return analyze(value, false);
	}

	@Override
	public String analyze(final String value, final boolean isEscapeAllQuotes) {
		getParser().setAllowLeadingWildcard(true);
		if (value.trim().length() == 0) {
			return String.valueOf(QUOTE_CHAR) + ' ' + QUOTE_CHAR;
		}
		String escapedString = value;
		boolean startsWithStar = escapedString.startsWith("*");
		boolean endsWithStar = escapedString.endsWith("*");
		if (startsWithStar) {
			escapedString = escapedString.substring(1);
		}
		if (endsWithStar) {
			escapedString = escapedString.substring(0, escapedString.length() - 1);
		}
		escapedString = ClientUtils.escapeQueryChars(escapedString);
		if (startsWithStar) {
			escapedString = "*" + escapedString;
		}
		if (endsWithStar) {
			escapedString = escapedString + "*";
		}
		return escapedString;
	}
}
/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.advancedsearch.helpers;

import com.elasticpath.cmclient.advancedsearch.service.impl.UnbuildQueryResult;
import com.elasticpath.domain.advancedsearch.AdvancedQueryType;

/**
 * Builds and unbuilds an EPQL query based on query type and query part.
 */
public final class QueryBuilder {

	private static final String FIND = "FIND"; //$NON-NLS-1$

	private static final String SPACE = " "; //$NON-NLS-1$

	private static final String WHERE = "WHERE"; //$NON-NLS-1$

	private QueryBuilder() {

	}

	/**
	 * Builds an EPQL query.
	 * 
	 * @param queryType query type
	 * @param queryPart query part
	 * @return an EPQL query
	 */
	public static String buildQuery(final AdvancedQueryType queryType, final String queryPart) {
		return FIND + SPACE + queryType.getPropertyKey() + SPACE + WHERE + SPACE + queryPart;
	}

	/**
	 * Unbuilds an EPQL query.
	 * 
	 * @param query an EPQL query to unbuild
	 * @return UnbuildQueryResult class which contains query part without FIND and query type. For example, query is 'FIND Product WHERE
	 *         CatalogCode='Telescopes'' then query part will be like this 'WHERE CatalogCode='Telescopes''.
	 */
	public static UnbuildQueryResult unbuildQuery(final String query) {
		if (query == null || query.length() == 0) {
			return null;
		}
		String exceptFirstToken = query.substring(query.indexOf(SPACE) + 1);
		String secondToken = exceptFirstToken.substring(0, exceptFirstToken.indexOf(SPACE));
		String queryPart = exceptFirstToken.substring(exceptFirstToken.indexOf(WHERE) + WHERE.length() + 1, exceptFirstToken.length());
		return new UnbuildQueryResult(AdvancedQueryType.getQueryTypeKey(secondToken), queryPart);
	}
}

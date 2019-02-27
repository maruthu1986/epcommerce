/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.domain.audit;


/**
 * A <code>ChangeOperation</code> that operates on a bulk query.
 */
public interface BulkChangeOperation extends ChangeOperation {

	/**
	 * @return Query String.
	 */
	String getQueryString();

	/**
	 * @param queryString query string.
	 */
	void setQueryString(String queryString);

	/**
	 * @return parameters.
	 */
	String getParameters();

	/**
	 * @param parameters parameters.
	 */
	void setParameters(String parameters);

}
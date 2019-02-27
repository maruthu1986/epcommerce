/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.batch.jobs;

/**
 * A job that aggregates counts and dates of all {@link com.elasticpath.domain.search.query.SearchTermsActivity}.
 */
public interface SearchTermsAggregatorJob {
	/**
	 * Updates the counts and dates of all {@link com.elasticpath.domain.search.query.SearchTermsActivity}.
	 */
	void updateSearchTermsActivity();
}

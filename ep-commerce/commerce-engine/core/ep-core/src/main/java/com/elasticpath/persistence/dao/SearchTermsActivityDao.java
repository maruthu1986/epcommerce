/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.persistence.dao;

import com.elasticpath.domain.search.query.SearchTermsActivity;

/**
 * CRUD for {@link SearchTermsActivity}.
 */
public interface SearchTermsActivityDao {

	/**
	 * Saves the given {@link SearchTermsActivity}.
	 * 
	 * @param activity a {@link SearchTermsActivity}
	 */
	void save(SearchTermsActivity activity);
}

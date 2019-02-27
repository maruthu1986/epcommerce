/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.persistence.dao.impl;

import com.elasticpath.domain.search.query.SearchTermsActivity;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.dao.SearchTermsActivityDao;

/**
 * Implementation of {@link SearchTermsActivityDao}.
 */
public class SearchTermsActivityDaoImpl implements SearchTermsActivityDao {

	private PersistenceEngine persistenceEngine;

	/**
	 * Saves the given {@link SearchTermsActivity}.
	 * 
	 * @param activity a {@link SearchTermsActivity}
	 */
	@Override
	public void save(final SearchTermsActivity activity) {
		persistenceEngine.save(activity);
	}

	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}
}

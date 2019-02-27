/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.configuration.models;

import com.elasticpath.domain.search.IndexBuildStatus;

/**
 * Selector interface for IndexBuildStatus.
 */
public interface IndexBuildStatusSelector {

	/**
	 * Gets the currently-selected IndexBuildStatus.
	 * 
	 * @return the currently-selected IndexBuildStatus
	 */
	IndexBuildStatus getSelectedIndexBuildStatus();
	
	/**
	 * Refreshes Data for Selection.
	 */
	void refresh();
}

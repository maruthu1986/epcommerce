/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.advancedsearch.helpers;

import com.elasticpath.domain.advancedsearch.AdvancedSearchQuery;

/**
 * Interface for getting currently selected AdvancedSearchQuery.
 */
public interface AdvancedSearchQuerySelector {

	/**
	 * Gets Currently Selected AdvancedSearchQuery.
	 * @return instance of selected AdvancedSearchQuery or null if nothing selected. 
	 */
	AdvancedSearchQuery getCurrentSelected();
}

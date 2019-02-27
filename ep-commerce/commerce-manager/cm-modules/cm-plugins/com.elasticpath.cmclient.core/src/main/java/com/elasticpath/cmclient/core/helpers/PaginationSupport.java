/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.helpers;

import com.elasticpath.cmclient.core.event.NavigationEvent.NavigationType;

/**
 * The interface to support pagination.
 */
public interface PaginationSupport {
	
	/**
	 * get the total result number.
	 * 
	 * @return the total result number
	 */
	int getResultsCount();

	/**
	 * get results start index.
	 * 
	 * @return the results start index
	 */
	int getResultsStartIndex();

	/**
	 * get item number on each page.
	 * 
	 * @return the item number on each page
	 */
	int getResultsPaging();
	
	/**
	 * fire navigation event.
	 * 
	 * @param navigationType the navigation type
	 * @param args the arguments
	 */
	void fireNavigationEvent(NavigationType navigationType, Object[] args);
}

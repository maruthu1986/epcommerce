/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.advancedsearch.views;

import com.elasticpath.cmclient.advancedsearch.actions.QueryBuilderAction;
import com.elasticpath.domain.advancedsearch.AdvancedSearchQuery;


/**
 * Interface for controller that will be responsible for managing tabs. 
 */
public interface TabsInteractionController {
	
	/**
	 * Selects the query tab for given action.
	 * 
	 * @param queryBuilderAction the query builder tab action
	 */
	void selectQueryBuilderTab(QueryBuilderAction queryBuilderAction);
	
	/**
	 * Executes search for selected element.
	 */
	void executeSearchForSelectedElement();
	
	/**
	 * Checks the query builder tab after delete action.
	 * 
	 * @param searchQuery the search query that was deleted
	 */
	void checkQueryBuilderTabAfterDelete(AdvancedSearchQuery searchQuery);
	
	/**
	 * Refreshes saved queries tab.
	 */
	void refreshSavedQueriesTab();
	
}

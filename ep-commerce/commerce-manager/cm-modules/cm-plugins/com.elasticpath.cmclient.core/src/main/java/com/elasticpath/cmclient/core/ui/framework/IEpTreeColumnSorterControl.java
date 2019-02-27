/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.ui.framework;

import org.eclipse.swt.widgets.TreeColumn;

import com.elasticpath.service.search.query.SearchCriteria;
import com.elasticpath.service.search.query.SortBy;

/**
 * Tree column Sorter Control adds ability to sort search results by clicking on column titles of a tree widget. 
 */
public interface IEpTreeColumnSorterControl {

	/**
	 * Registers tree column and sort type.
	 * 
	 * @param treeColumn tree column
	 * @param sortBy sort type.
	 * @return added tree column.
	 */
	TreeColumn registerTreeColumn(TreeColumn treeColumn, SortBy sortBy);

	/**
	 * Updates column headers and SWT arrows.
	 * 
	 * @param searchCriteria search criteria.
	 */
	void updateColumnOrder(SearchCriteria searchCriteria);

	/**
	 * Clears search criteria information, preventing further searches with the search criteria.
	 */
	void clear();
}
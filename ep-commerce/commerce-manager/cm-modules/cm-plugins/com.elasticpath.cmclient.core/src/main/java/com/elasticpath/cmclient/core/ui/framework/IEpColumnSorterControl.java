/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.ui.framework;

import org.eclipse.swt.widgets.TableColumn;

import com.elasticpath.service.search.query.SearchCriteria;
import com.elasticpath.service.search.query.SortBy;

/**
 * Column Sorter Control adds ability to sort search results by clicking on column titles.
 * <p>
 * To enable ability to perform sorting of the search results by clicking on column titles you have to 
 * <ul>
 * <li>Extend workbench view from <tt>AbstractSortListView</tt>.</li>
 * <li>Register table column you want to be able to sort search results:
 * <p><code>
 * 		IEpTableColumn tableColumn = epTableViewer.addTableColumn(columnName, width);
 *		registerTableColumn(tableColumn, sortType);
 * </code></p></li>
 * <li>To update UI call {@link com.elasticpath.cmclient.core.views.AbstractSortListView#updateSortingOrder(SearchCriteria)} 
 * when search is done.</li>
 * </ul>
 * </p>
 */
public interface IEpColumnSorterControl {

	/**
	 * Registers table column and sort type.
	 * <p>
	 * When you register table column selection SortSelectionListener listener will be added.
	 * </p>
	 * 
	 * @param tableColumn table column.
	 * @param sortBy sort type.
	 * @return added table column.
	 */
	TableColumn registerTableColumn(TableColumn tableColumn, SortBy sortBy);

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
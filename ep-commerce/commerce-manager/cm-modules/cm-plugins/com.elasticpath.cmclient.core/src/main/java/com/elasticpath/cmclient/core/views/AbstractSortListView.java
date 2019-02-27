/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.views;

import com.elasticpath.cmclient.core.helpers.AbstractSearchRequestJob;
import com.elasticpath.cmclient.core.ui.framework.IEpColumnSorterControl;
import com.elasticpath.cmclient.core.ui.framework.IEpTableColumn;
import com.elasticpath.cmclient.core.ui.framework.impl.EpColumnSorterControl;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.search.query.SearchCriteria;
import com.elasticpath.service.search.query.SortBy;

/**
 * Provides the abstract functionality for a SearchResults view, including column sorting.
 */
public abstract class AbstractSortListView extends AbstractListView {

	private final IEpColumnSorterControl columnSorterControl;

	/**
	 * The constructor.
	 *
	 * @param enableNavigation true if navigation buttons should be visible
	 * @param tableName name of the table
	 */
	public AbstractSortListView(final boolean enableNavigation, final String tableName) {
		super(enableNavigation, tableName);
		this.columnSorterControl = new EpColumnSorterControl(this);
	}

	/**
	 * The constructor.
	 *  @param enableNavigation true if navigation buttons should be visible
	 * @param checkable true if this view should contain checkboxes.
	 * @param tableName name of the table
	 */
	public AbstractSortListView(final boolean enableNavigation, final boolean checkable, final String tableName) {
		super(enableNavigation, checkable, tableName);
		this.columnSorterControl = new EpColumnSorterControl(this);
	}

	/**
	 * Updates sorting order this is targeted when search is done by pressing search button and just renders appropriate sorting arrows on the search
	 * results table header.
	 * 
	 * @param searchCriteria search criteria.
	 */
	public void updateSortingOrder(final SearchCriteria searchCriteria) {
		columnSorterControl.updateColumnOrder(searchCriteria);
	}

	/**
	 * Gets column sorter control.
	 * 
	 * @return column sorter control.
	 */
	protected IEpColumnSorterControl getColumnSorterControl() {
		return columnSorterControl;
	}

	/**
	 * Registers the column, that column will be automatically set sorting arrow once search is done.
	 * 
	 * @param tableColumn tableColumn
	 * @param sortBy sort by field, if <tt>null</tt> value is specified then column will not be registered.
	 */
	protected void registerTableColumn(final IEpTableColumn tableColumn, final SortBy sortBy) {
		if (sortBy == null) { // ignore this column
			return;
		}
		getColumnSorterControl().registerTableColumn(tableColumn.getSwtTableColumn(), sortBy);
	}

	/**
	 * Gets search request job to execute new search once the latter is requested by means of pressing a column header.
	 * 
	 * @return search request job.
	 */
	public abstract AbstractSearchRequestJob< ? extends Persistable> getSearchRequestJob();
}

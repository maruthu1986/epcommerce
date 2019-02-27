/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.ui.framework.impl;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.elasticpath.cmclient.core.helpers.AbstractSearchRequestJob;
import com.elasticpath.cmclient.core.helpers.ISearchJobSource;
import com.elasticpath.cmclient.core.ui.framework.IEpColumnSorterControl;
import com.elasticpath.cmclient.core.views.AbstractSortListView;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.search.query.SearchCriteria;
import com.elasticpath.service.search.query.SortBy;
import com.elasticpath.service.search.query.SortOrder;

/**
 * Default implementation of <code>IEpColumnSorterControl</code>.
 */
public class EpColumnSorterControl implements IEpColumnSorterControl {

	private final Map<SortBy, TableColumn> map = new HashMap<SortBy, TableColumn>();

	private SearchCriteria searchCriteria;
	
	private Table parentTable;

	private AbstractSortListView searchJobRequestorView;

	private AbstractSearchRequestJob< ? extends Persistable> searchJob;

	/**
	 * If search job hasn't been specified explicitly by means of calling <code>EpColumnSorterControl(AbstractSearchRequestJob)</code> constructor,
	 * then AbstractSortListView.getSearchRequestJob() is requested for the job.
	 * 
	 * @param view sort list view.
	 */
	public EpColumnSorterControl(final AbstractSortListView view) {
		super();
		this.searchJobRequestorView = view;
	}
	
	/**
	 * Passes <code>AbstractSearchRequestJob</code> explicitly if the latter is available for the client.
	 *
	 * @param searchJob specific instance of <code>AbstractSearchRequestJob</code> to provide search.
	 */
	public EpColumnSorterControl(final AbstractSearchRequestJob< ? extends Persistable> searchJob) {
		super();
		this.searchJob = searchJob;
	}

	/**
	 * Registers table column and sort type.
	 * 
	 * @param tableColumn table column
	 * @param sortBy sort type.
	 * @return added table column.
	 */
	public TableColumn registerTableColumn(final TableColumn tableColumn, final SortBy sortBy) {
		// set parent table
		Table table = tableColumn.getParent();
		for (TableColumn column : map.values()) {
			if (table != column.getParent()) {
				throw new IllegalArgumentException("Should be the same table"); //$NON-NLS-1$
			}
		}
		parentTable = table;

		// add listener
		map.put(sortBy, tableColumn);
		if (tableColumn != null) {
			tableColumn.addSelectionListener(new SortSelectionListener(this));
		}
		return tableColumn;
	}
	
    /**
	 * Updates column headers and SWT arrows.
	 * 
	 * @param searchCriteria search criteria.
	 */
	public void updateColumnOrder(final SearchCriteria searchCriteria) {
		this.searchCriteria = searchCriteria;

		TableColumn tableColumn = map.get(searchCriteria.getSortingType());
		
		if (tableColumn == null) { // suspend when table column is not registered.
			parentTable.setSortDirection(SWT.NONE);
			return;
		}
		
		
		if (parentTable.getSortColumn() == null || parentTable.getSortColumn() == tableColumn) {
			//column the same. Just change order
			switch (searchCriteria.getSortingOrder()) { // UI update column sort order.
				case ASCENDING:
					parentTable.setSortDirection(SWT.UP);
					break;
				case DESCENDING:
					parentTable.setSortDirection(SWT.DOWN);
					break;
				default:
					throw new IllegalArgumentException("Not supported sorting order type: " + searchCriteria.getSortingOrder()); //$NON-NLS-1$
			}			
		} else {
			//new column for sorting. Sorting must start from asc order.
			parentTable.setSortDirection(SWT.UP);
		}
		
		parentTable.setSortColumn(tableColumn); // UI update sort column
	}

	/**
	 * Returns sort type by table column or null if table column wasn't registered.
	 * 
	 * @param tableColumn table column 
	 * @return sort type or null if table column wasn't registered.
	 */
	protected SortBy getSortTypeByColumn(final TableColumn tableColumn) {
		for (SortBy sortBy : map.keySet()) {
			if (map.get(sortBy) == tableColumn) {
				return sortBy;
			}
		}
		return null;
	}
	
	/**
	 * Gets search criteria. 
	 * 
	 * @return search criteria.
	 */
	public SearchCriteria getSearchCriteria() {
		return searchCriteria;
	}

	/**
	 * Get search request job.
	 * 
	 * @return search request job.
	 */
	protected AbstractSearchRequestJob< ? extends Persistable> getJob() {
		if (searchJob != null) {
			return searchJob;
		}
		return searchJobRequestorView.getSearchRequestJob();
	}
	
	/**
	 * Listener that adds ability to sort columns on click.
	 * <p>
	 * It should update search criteria and perform new search when selection occurs.
	 * </p>
	 */
	private static class SortSelectionListener extends SelectionAdapter {

		private final EpColumnSorterControl registry;
		
		/**
		 * The constructor.
		 * 
		 * @param registry table column registry.
		 */
		SortSelectionListener(final EpColumnSorterControl registry) {
			super();
			this.registry = registry;
		}

		@Override
		public void widgetSelected(final SelectionEvent event) {
			final SearchCriteria searchCriteria = registry.getSearchCriteria();
			if (searchCriteria == null) {
				return;
			}
						
			updateSortingTypeAndSortingOrder(event, searchCriteria);
						
			doSearch(searchCriteria);
		}

		/*
		 * Performs search.
		 */
		private void doSearch(final SearchCriteria searchCriteria) {
			AbstractSearchRequestJob< ? extends Persistable> job = this.registry.getJob();
			job.setSearchCriteria(searchCriteria);
			if ((job instanceof ISearchJobSource) && registry.getSearchJobRequestorView() != null) {
				((ISearchJobSource) job).setSource(this.registry);
			}
			job.executeSearch(null);
		}

		/*
		 * Updates search criteria. Sets new order and sorting type.
		 */
		private void updateSortingTypeAndSortingOrder(final SelectionEvent event, final SearchCriteria searchCriteria) {
			boolean newTableColumnForSort = false; 
			if (event.getSource() instanceof TableColumn) {
				SortBy sortBy = registry.getSortTypeByColumn((TableColumn) event.getSource());
				newTableColumnForSort = searchCriteria.getSortingType() != sortBy;
				searchCriteria.setSortingType(sortBy);
			}
			if (newTableColumnForSort) {
				searchCriteria.setSortingOrder(SortOrder.ASCENDING);
			} else {
				searchCriteria.setSortingOrder(searchCriteria.getSortingOrder().reverse());
			}
		}
	}

	/**
	 * Clears search criteria information, preventing further searches with the search criteria.
	 */
	public void clear() {
		searchCriteria = null;
		if (parentTable != null) {
			parentTable.setSortDirection(SWT.NONE);
		}			
	}

	/**
	 * Get the search job requestor view.
	 * 
	 * @return the searchJobRequestorView
	 */
	public AbstractSortListView getSearchJobRequestorView() {
		return searchJobRequestorView;
	}
}

/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.ui.framework.impl;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import com.elasticpath.cmclient.core.helpers.AbstractSearchRequestJob;
import com.elasticpath.cmclient.core.ui.framework.IEpTreeColumnSorterControl;
import com.elasticpath.cmclient.core.views.AbstractSortListView;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.search.query.SearchCriteria;
import com.elasticpath.service.search.query.SortBy;
import com.elasticpath.service.search.query.SortOrder;

/**
 * Default implementation of <code>IEpTreeColumnSorterControl</code>.
 */
public class EpTreeColumnSorterControl implements IEpTreeColumnSorterControl {

	private final Map<SortBy, TreeColumn> map = new HashMap<SortBy, TreeColumn>();

	private SearchCriteria searchCriteria;

	private Tree parentTree;

	private AbstractSortListView searchJobRequestorView;

	private AbstractSearchRequestJob< ? extends Persistable> searchJob;

	/**
	 * If search job hasn't been specified explicitly by means of calling <code>EpColumnSorterControl(AbstractSearchRequestJob)</code> constructor,
	 * then AbstractSortListView.getSearchRequestJob() is requested for the job.
	 * 
	 * @param view sort list view.
	 */
	public EpTreeColumnSorterControl(final AbstractSortListView view) {
		super();
		this.searchJobRequestorView = view;
	}

	/**
	 * Passes <code>AbstractSearchRequestJob</code> explicitly if the latter is available for the client.
	 * 
	 * @param searchJob specific instance of <code>AbstractSearchRequestJob</code> to provide search.
	 */
	public EpTreeColumnSorterControl(final AbstractSearchRequestJob< ? extends Persistable> searchJob) {
		super();
		this.searchJob = searchJob;
	}

	/**
	 * Registers tree column and sort type.
	 * 
	 * @param treeColumn tree column
	 * @param sortBy sort type.
	 * @return added tree column.
	 */
	public TreeColumn registerTreeColumn(final TreeColumn treeColumn, final SortBy sortBy) {
		// set parent tree
		Tree tree = treeColumn.getParent();
		for (TreeColumn column : map.values()) {
			if (tree != column.getParent()) {
				throw new IllegalArgumentException("Should be the same tree"); //$NON-NLS-1$
			}
		}
		parentTree = tree;

		// add listener
		map.put(sortBy, treeColumn);
		if (treeColumn != null) {
			treeColumn.addSelectionListener(new SortSelectionListener(this));
		}
		return treeColumn;
	}

	/**
	 * Updates column headers and SWT arrows.
	 * 
	 * @param searchCriteria search criteria.
	 */
	public void updateColumnOrder(final SearchCriteria searchCriteria) {
		this.searchCriteria = searchCriteria;

		TreeColumn treeColumn = map.get(searchCriteria.getSortingType());

		if (treeColumn == null) { // suspend when tree column is not registered.
			parentTree.setSortDirection(SWT.NONE);
			return;
		}

		if (parentTree.getSortColumn() == treeColumn) {
			//column the same. Just change order
			switch (searchCriteria.getSortingOrder()) { // UI update column sort order.
				case ASCENDING:
					parentTree.setSortDirection(SWT.UP);
					break;
				case DESCENDING:
					parentTree.setSortDirection(SWT.DOWN);
					break;
				default:
					throw new IllegalArgumentException("Not supported sorting order type: " + searchCriteria.getSortingOrder()); //$NON-NLS-1$
			}			
		} else {
			//new column for sorting. Sorting must start from asc order.
			parentTree.setSortDirection(SWT.UP);
		}
		
		parentTree.setSortColumn(treeColumn); // UI update sort column
	}

	/**
	 * Returns sort type by tree column or null if tree column wasn't registered.
	 * 
	 * @param treeColumn tree column
	 * @return sort type or null if tree column wasn't registered.
	 */
	protected SortBy getSortTypeByColumn(final TreeColumn treeColumn) {
		for (SortBy sortBy : map.keySet()) {
			if (map.get(sortBy) == treeColumn) {
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

		private final EpTreeColumnSorterControl registry;

		/**
		 * The constructor.
		 * 
		 * @param registry tree column registry.
		 */
		SortSelectionListener(final EpTreeColumnSorterControl registry) {
			super();
			this.registry = registry;
		}

		/**
		 * This method updates search criteria and performs search.
		 * 
		 * @param event an event containing information about the selection.
		 */
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
			this.registry.getJob().setSearchCriteria(searchCriteria);
			this.registry.getJob().executeSearch(null);
		}

		/*
		 * Updates search criteria. Sets new order and sorting type.
		 */
		private void updateSortingTypeAndSortingOrder(final SelectionEvent event, final SearchCriteria searchCriteria) {
			boolean newTableColumnForSort = false; 
			
			if (event.getSource() instanceof TreeColumn) {
				SortBy sortBy = registry.getSortTypeByColumn((TreeColumn) event.getSource());
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
		if (parentTree != null) {
			parentTree.setSortDirection(SWT.NONE);
		}
	}
}

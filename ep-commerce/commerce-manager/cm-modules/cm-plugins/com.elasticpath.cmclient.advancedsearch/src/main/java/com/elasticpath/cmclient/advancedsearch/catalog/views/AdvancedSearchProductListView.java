/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.advancedsearch.catalog.views;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

import com.elasticpath.cmclient.advancedsearch.helpers.ProductAdvancedSearchRequestJob;
import com.elasticpath.cmclient.catalog.views.AbstractProductListView;

/**
 * This view displays lists of products in a table format obtained from advanced search result view.
 */
public class AdvancedSearchProductListView extends AbstractProductListView {

	/**
	 * The view id for <code>AdvancedSearchProductListView</code>.
	 */
	public static final String VIEW_ID = AdvancedSearchProductListView.class.getName();

	private static final String ADVANCED_SEARCH_PRODUCT_LIST_TABLE = "Advanced Search Product List Table"; //$NON-NLS-1$

	/**
	 * Constructor.
	 */
	public AdvancedSearchProductListView() {
		super(ADVANCED_SEARCH_PRODUCT_LIST_TABLE);
	}

	@Override
	public void init(final IViewSite site, final IMemento memento) throws PartInitException {
		super.init(site, memento);
		setPartName(site.getSecondaryId());
	}

	@Override
	public void refreshViewerInput() {
		doSearch();
	}

	/**
	 * Executes search.
	 */
	protected void doSearch() {
		final ProductAdvancedSearchRequestJob advancedSearchRequestJob = new ProductAdvancedSearchRequestJob(getPagination());
		String advancedSearchQuery = (String) getViewer().getData(VIEW_ID);
		if (advancedSearchQuery != null) {
			advancedSearchRequestJob.setAdvancedSearchCriteria(advancedSearchQuery);
			advancedSearchRequestJob.setListenerId(getPartName());
			advancedSearchRequestJob.executeSearch(getResultsStartIndex());
		}
	}

	@Override
	protected IStructuredContentProvider getViewContentProvider() {
		return new AdvancedSearchProductContentProvider(this);
	}

	@Override
	protected String getPartId() {
		return VIEW_ID;
	}
}

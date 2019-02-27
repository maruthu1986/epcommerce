/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.advancedsearch.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

import com.elasticpath.cmclient.advancedsearch.AdvancedSearchMessages;
import com.elasticpath.cmclient.advancedsearch.actions.QueryBuilderAction;
import com.elasticpath.cmclient.advancedsearch.catalog.views.AdvancedSearchProductListView;
import com.elasticpath.cmclient.advancedsearch.helpers.ProductAdvancedSearchRequestJob;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.EpUiException;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTabFolder;
import com.elasticpath.cmclient.core.views.AbstractCmClientView;
import com.elasticpath.cmclient.core.views.AbstractListView;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.advancedsearch.AdvancedQueryType;
import com.elasticpath.domain.advancedsearch.AdvancedSearchQuery;
import com.elasticpath.persistence.dao.AdvancedSearchQueryDao;

/**
 * The <code>AdvancedSearchView</code> is used to build, save and execute EpQL search queries.
 */
public abstract class AbstractAdvancedSearchView extends AbstractCmClientView implements TabsInteractionController {

	private static final int SAVED_QUERIES_TAB = 0;

	private static final int QUERY_BUILDER_TAB = 1;

	private IEpTabFolder tabFolder;

	private SavedQueriesTab savedQueriesTab;

	private QueryBuilderTab queryBuilderTab;

	private final AdvancedSearchQueryDao searchQueryDao = ServiceLocator.getService(ContextIdNames.ADVANCED_SEARCH_QUERY_DAO);

	private final ProductAdvancedSearchRequestJob productSearchJob = new ProductAdvancedSearchRequestJob(getPagination());

	@Override
	protected void createViewPartControl(final Composite parentComposite) {
		final IEpLayoutComposite parentEpComposite = CompositeFactory.createGridLayoutComposite(parentComposite, 1, false);

		tabFolder = parentEpComposite.addTabFolder(parentEpComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true));
		savedQueriesTab = new SavedQueriesTab(tabFolder, SAVED_QUERIES_TAB, this);
		queryBuilderTab = new QueryBuilderTab(tabFolder, QUERY_BUILDER_TAB, this);
		selectQueryBuilderTab(QueryBuilderAction.CREATE);
		tabFolder.setSelection(SAVED_QUERIES_TAB);
	}

	/**
	 * Executes search with given query for specific query type.
	 * 
	 * @param advancedSearchQuery the advanced search query to execute
	 */
	public void executeSearch(final AdvancedSearchQuery advancedSearchQuery) {
		String viewName = AdvancedSearchMessages.get().AdvancedSearchResults;
		if (advancedSearchQuery.isPersisted()) {
			viewName = advancedSearchQuery.getName() + " - " + advancedSearchQuery.getQueryId(); //$NON-NLS-1$
		}

		if (AdvancedQueryType.PRODUCT.equals(advancedSearchQuery.getQueryType())) {
			productSearchJob.setAdvancedSearchCriteria(advancedSearchQuery.getQueryContent());
			productSearchJob.setPageSize(getPagination());
			checkView(AdvancedSearchProductListView.VIEW_ID, viewName, advancedSearchQuery.getQueryContent());
			productSearchJob.setListenerId(viewName);
			productSearchJob.executeSearch();
		}
	}

	private void checkView(final String viewId, final String viewName, final String queryContent) {
		try {
			AbstractListView showView = (AbstractListView) getSite().getWorkbenchWindow().getActivePage().showView(viewId, viewName,
					IWorkbenchPage.VIEW_ACTIVATE);
			showView.getViewer().setData(viewId, queryContent);
		} catch (final PartInitException e) {
			// Log the error and throw an unchecked exception
			throw new EpUiException("Fail to reopen list view.", e); //$NON-NLS-1$
		}
	}

	@Override
	protected Object getModel() {
		return null;
	}

	@Override
	public void setFocus() {
		// Do nothing
	}

	@Override
	public void selectQueryBuilderTab(final QueryBuilderAction queryBuilderAction) {
		switch (queryBuilderAction) {
		case CREATE:
			AdvancedSearchQuery searchQuery = ServiceLocator.getService(ContextIdNames.ADVANCED_SEARCH_QUERY);
			queryBuilderTab.prepareForCreateAction(searchQuery);
			break;
		case OPEN:
			queryBuilderTab.prepareForOpenAction(searchQueryDao.get(savedQueriesTab.getCurrentSelected().getUidPk()));
			break;
		case EDIT:
			queryBuilderTab.prepareForEditAction(searchQueryDao.get(savedQueriesTab.getCurrentSelected().getUidPk()));
			break;
		default:

		}
		tabFolder.setSelection(QUERY_BUILDER_TAB);
	}

	@Override
	public void checkQueryBuilderTabAfterDelete(final AdvancedSearchQuery searchQuery) {
		if (queryBuilderTab.getSearchQuery().equals(searchQuery)) {
			queryBuilderTab.prepareForCreateAction(ServiceLocator.getService(
					ContextIdNames.ADVANCED_SEARCH_QUERY));
		}
	}

	@Override
	public void refreshSavedQueriesTab() {
		savedQueriesTab.refreshTab();
	}

	@Override
	public void executeSearchForSelectedElement() {
		AdvancedSearchQuery advancedSearchQuery = savedQueriesTab.getCurrentSelected();
		if (advancedSearchQuery != null) {
			executeSearch(advancedSearchQuery);
		}
	}

	/**
	 * Gets available query types.
	 * 
	 * @return view id list
	 */
	public abstract AdvancedQueryType[] getQueryTypes();
}

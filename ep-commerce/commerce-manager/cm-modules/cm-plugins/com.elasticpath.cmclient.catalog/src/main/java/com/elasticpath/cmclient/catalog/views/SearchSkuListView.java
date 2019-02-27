/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.views;

import org.eclipse.jface.viewers.IStructuredContentProvider;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.helpers.AbstractSearchRequestJob;
import com.elasticpath.cmclient.core.helpers.SkuSearchRequestJob;
import com.elasticpath.cmclient.core.ui.framework.IEpTableColumn;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.search.query.SkuSearchCriteria;
import com.elasticpath.service.search.query.SortBy;
import com.elasticpath.service.search.query.StandardSortBy;


/**
 * This view displays lists of productSkus in a table format. The list of productSkus can come from any source.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.MissingBreakInSwitch", "PMD.UnusedFormalParameter" })
public class SearchSkuListView extends AbstractSkuListView {

	/**
	 * The part id.
	 */
	public static final String PART_ID = "com.elasticpath.cmclient.catalog.views.SearchSkuListView"; //$NON-NLS-1$

	private static final String SEARCH_SKU_LIST_TABLE = "Search Sku List"; //$NON-NLS-1$

	private final SkuSearchRequestJob searchJob;

	/**
	 * Constructor.
	 */
	public SearchSkuListView() {
		super(SEARCH_SKU_LIST_TABLE);
		searchJob = new SkuSearchRequestJob();
	}
	
	@Override
	protected void initializeTable(final IEpTableViewer table) {

		final String[] columnNames =
				new String[] {
						"", //$NON-NLS-1$
						CatalogMessages.get().SkuListView_TableColumnTitle_SkuCode,
						CatalogMessages.get().SkuListView_TableColumnTitle_ProductName,
						CatalogMessages.get().SkuListView_TableColumnTitle_SkuConfiguration,
						CatalogMessages.get().SkuListView_TableColumnTitle_Brand,
						CatalogMessages.get().SkuListView_TableColumnTitle_Active };
		final int[] columnWidths = new int[] { 28, 100, 150, 150, 80, 50 };
		final SortBy[] sortTypes = new SortBy[] {
				StandardSortBy.SKU_RESULT_TYPE,
				StandardSortBy.SKU_CODE,
				StandardSortBy.PRODUCT_NAME_NON_LC,
				StandardSortBy.SKU_CONFIG,
				StandardSortBy.BRAND_NAME,
				null
		};
		
		for (int i = 0; i < columnNames.length; i++) {
			IEpTableColumn addTableColumn = table.addTableColumn(columnNames[i], columnWidths[i]);
			registerTableColumn(addTableColumn, sortTypes[i]);
		}
		
		getSite().setSelectionProvider(table.getSwtTableViewer());
	}

	@Override
	public void refreshViewerInput() {

		final Object criteria = getViewer().getData(SKU_SEARCH_CRITERIA);
		if (criteria != null) {
			doSearch((SkuSearchCriteria) criteria); // The other thread should call setInput() to set the product result
		}
	}

	
	/**
	 * fire the index search.
	 * 
	 * @param searchCriteria the search criteria
	 */
	protected void doSearch(final SkuSearchCriteria searchCriteria) {
		searchCriteria.setLocale(CorePlugin.getDefault().getDefaultLocale());
		searchJob.setSource(this);
		searchJob.setSearchCriteria(searchCriteria);
		searchJob.executeSearchFromIndex(this.getSite().getShell(), getResultsStartIndex());
	}
	
	@Override
	protected IStructuredContentProvider getViewContentProvider() {
		return new SearchProductSkuViewContentProvider(this);
	}
	
	@Override
	public AbstractSearchRequestJob < ? extends Persistable > getSearchRequestJob() {
		return searchJob;
	}

	@Override
	protected String getPartId() {
		return PART_ID;
	}
}

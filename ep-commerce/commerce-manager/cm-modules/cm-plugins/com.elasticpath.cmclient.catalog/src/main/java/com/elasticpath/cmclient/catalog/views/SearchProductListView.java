/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.views;

import org.eclipse.jface.viewers.IStructuredContentProvider;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.helpers.AbstractSearchRequestJob;
import com.elasticpath.cmclient.core.helpers.ProductSearchRequestJob;
import com.elasticpath.cmclient.core.ui.framework.IEpTableColumn;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.search.query.ProductSearchCriteria;
import com.elasticpath.service.search.query.SortBy;
import com.elasticpath.service.search.query.StandardSortBy;


/**
 * This view displays lists of products in a table format. The list of products can come from any source. For example, the results of a product
 * search can be displayed or a the products in a category may be displayed when the category is selected in another view.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.MissingBreakInSwitch", "PMD.UnusedFormalParameter" })
public class SearchProductListView extends AbstractProductListView {

	/**
	 * The part id.
	 */
	public static final String PART_ID = "com.elasticpath.cmclient.catalog.views.SearchProductListView"; //$NON-NLS-1$

	private static final String SEARCH_PRODUCT_LIST_TABLE = "Search Product List"; //$NON-NLS-1$

	// NOTE: loadSku !MUST! be !TRUE! since delete action checks whether the orderSkus are in shipment or not.
	// setting loadSku to false results in orderSku = null and omitting the check.
	private final ProductSearchRequestJob searchJob = new ProductSearchRequestJob(true);

	/**
	 * Constructor.
	 */
	public SearchProductListView() {
		super(SEARCH_PRODUCT_LIST_TABLE);
	}


	@Override
	protected void initializeTable(final IEpTableViewer table) {

		final String[] columnNames =
				new String[] {
						"", //$NON-NLS-1$
						CatalogMessages.get().ProductListView_TableColumnTitle_ProductCode,
						CatalogMessages.get().ProductListView_TableColumnTitle_ProductName,
						CatalogMessages.get().ProductListView_TableColumnTitle_ProductType,
						CatalogMessages.get().ProductListView_TableColumnTitle_Brand,
						CatalogMessages.get().ProductListView_TableColumnTitle_DefaultCategory,
						CatalogMessages.get().ProductListView_TableColumnTitle_Active };
		final int[] columnWidths = new int[] { 28, 150, 250, 150, 100, 150, 70};
		final SortBy[] sortTypes = new SortBy[] {
				null,
				StandardSortBy.PRODUCT_CODE,
				StandardSortBy.PRODUCT_NAME_NON_LC,
				StandardSortBy.PRODUCT_TYPE_NAME,
				StandardSortBy.BRAND_NAME,
				StandardSortBy.PRODUCT_DEFAULT_CATEGORY_NAME,
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

		final Object criteria = getViewer().getData(PRODUCT_SEARCH_CRITERIA);
		if (criteria != null) {
			doSearch((ProductSearchCriteria) criteria); // The other thread should call setInput() to set the product result
		}
	}

	/**
	 * fire the index search.
	 * 
	 * @param searchCriteria the search criteria
	 */
	protected void doSearch(final ProductSearchCriteria searchCriteria) {
		searchCriteria.setLocale(CorePlugin.getDefault().getDefaultLocale());
		searchJob.setSource(this);
		searchJob.setSearchCriteria(searchCriteria);
		searchJob.executeSearchFromIndex(this.getSite().getShell(), getResultsStartIndex());
	}
	
	@Override
	protected IStructuredContentProvider getViewContentProvider() {
		return new SearchProductViewContentProvider(this);
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

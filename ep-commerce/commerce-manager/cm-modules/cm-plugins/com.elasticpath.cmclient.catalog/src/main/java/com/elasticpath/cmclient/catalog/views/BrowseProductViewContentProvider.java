/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */

package com.elasticpath.cmclient.catalog.views;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.service.CatalogEventService;
import com.elasticpath.cmclient.core.ui.framework.IEpColumnSorterControl;
import com.elasticpath.cmclient.core.ui.framework.impl.EpColumnSorterControl;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.service.search.query.ProductSearchCriteria;

/**
 * The content provider class is responsible for providing objects to the view. It can wrap existing objects in adapters or simply return objects
 * as-is. These objects may be sensitive to the current input of the view, or ignore it and always show the same content.
 */
public class BrowseProductViewContentProvider extends ProductListViewContentProvider {

	/**
	 * Constructor.
	 *
	 * @param productListView the list view that have this content provider
	 */
	public BrowseProductViewContentProvider(final AbstractProductListView productListView) {
		super(productListView);
		CatalogEventService.getInstance().addProductListener(this);
	}

	@Override
	public void dispose() {
		CatalogEventService.getInstance().removeProductListener(this);
	}

	@Override
	public void productSearchResultReturned(final SearchResultEvent<Product> event) {

		if ((getProductListView().getViewer() == null)
				|| !isValidEvent(event)) {
			return;
		}

		getProductListView().setResultsStartIndex(event.getStartIndex());
		getProductListView().setResultsCount(event.getTotalNumberFound());
		getProductListView().getViewer().setInput(wrapProducts(event.getItems()));

		ProductSearchCriteria searchCriteria = null;
		if (event.getSource() instanceof ProductSearchViewTab) {
			searchCriteria = ((ProductSearchViewTab) event.getSource()).getModel();
			getProductListView().setSearchCriteria(searchCriteria);
		} else if (event.getSource() instanceof AbstractProductListView) {
			searchCriteria = ((AbstractProductListView) event.getSource()).getSearchCriteria();
		} else if (event.getSource() instanceof IEpColumnSorterControl) {
			searchCriteria = (ProductSearchCriteria) ((EpColumnSorterControl) event.getSource()).getSearchCriteria();
		}
		searchCriteria.setLocale(CorePlugin.getDefault().getDefaultLocale());
		getProductListView().getViewer().setData(AbstractProductListView.PRODUCT_SEARCH_CRITERIA, searchCriteria);

		getProductListView().updateNavigationComponents();
		getProductListView().updateSortingOrder(searchCriteria);
		handleSearchResultMessage(CatalogMessages.get().ProductListView_No_Result_Found);
	}

	/**
	 * Valid events to process search results.
	 * 
	 * @param event the event to validate
	 * @return true if the search results should be processed
	 */
	private boolean isValidEvent(final SearchResultEvent<Product> event) {
		Object source = event.getSource();
		if (source instanceof BrowseProductListView) {
			return true;
		}
		if (source instanceof EpColumnSorterControl) {
			EpColumnSorterControl control = (EpColumnSorterControl) source;
			return (control.getSearchJobRequestorView() instanceof BrowseProductListView);
		}
		return false;
	}
}

/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */

package com.elasticpath.cmclient.catalog.views;

import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.service.CatalogEventService;
import com.elasticpath.cmclient.core.ui.framework.IEpColumnSorterControl;
import com.elasticpath.cmclient.core.ui.framework.impl.EpColumnSorterControl;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.service.search.query.SkuSearchCriteria;

/**
 * The content provider class is responsible for providing objects to the view. It can wrap existing objects in adapters or simply return objects
 * as-is. These objects may be sensitive to the current input of the view, or ignore it and always show the same content.
 */
public class SearchProductSkuViewContentProvider extends SkuListViewContentProvider {

	/**
	 * Constructor.
	 *
	 * @param skuListView the list view that have this content provider
	 */
	public SearchProductSkuViewContentProvider(final AbstractSkuListView skuListView) {
		super(skuListView);
		CatalogEventService.getInstance().addProductSkuListener(this);
	}

	@Override
	public void dispose() {
		CatalogEventService.getInstance().removeProductSkuListener(this);
	}

	@Override
	public void productSkuSearchResultReturned(final SearchResultEvent<ProductSku> event) {

		if ((getProductSkuListView().getViewer() == null)
				|| !isValidEvent(event)) {
			return;
		}

		PlatformUI.getWorkbench().getDisplay().syncExec(() -> {

			getProductSkuListView().setResultsStartIndex(event.getStartIndex());
			getProductSkuListView().setResultsCount(event.getTotalNumberFound());
			getProductSkuListView().getViewer().setInput(event.getItems().toArray());

			SkuSearchCriteria searchCriteria = null;
			if (event.getSource() instanceof SkuSearchViewTab) {
				searchCriteria = ((SkuSearchViewTab) event.getSource()).getModel();
				getProductSkuListView().setSearchCriteria(searchCriteria);
			} else if (event.getSource() instanceof AbstractSkuListView) {
				searchCriteria = ((AbstractSkuListView) event.getSource()).getSearchCriteria();
			} else if (event.getSource() instanceof IEpColumnSorterControl) {
				searchCriteria = (SkuSearchCriteria) ((EpColumnSorterControl) event.getSource()).getSearchCriteria();
			}
			searchCriteria.setLocale(CorePlugin.getDefault().getDefaultLocale());
			getProductSkuListView().getViewer().setData(AbstractSkuListView.SKU_SEARCH_CRITERIA, searchCriteria);

			getProductSkuListView().updateNavigationComponents();
			getProductSkuListView().updateSortingOrder(searchCriteria);
			handleSearchResultMessage(CatalogMessages.get().ProductListView_No_Result_Found);
		});
	}

	/**
	 * Valid events to process search results.
	 * 
	 * @param event the event to validate
	 * @return true if the search results should be processed
	 */
	private boolean isValidEvent(final SearchResultEvent<ProductSku> event) {
		Object source = event.getSource();
		if ((source instanceof AbstractSkuListView) || (source instanceof SkuSearchViewTab)) {
			return true;
		}
		if (source instanceof EpColumnSorterControl) {
			EpColumnSorterControl control = (EpColumnSorterControl) source;
			return (control.getSearchJobRequestorView() instanceof AbstractSkuListView);
		}
		return false;
	}
}

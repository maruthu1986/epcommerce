/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.advancedsearch.catalog.views;

import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.advancedsearch.helpers.AdvancedSearchProductListener;
import com.elasticpath.cmclient.advancedsearch.service.AdvancedSearchEventService;
import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.views.AbstractProductListView;
import com.elasticpath.cmclient.catalog.views.ProductListViewContentProvider;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.service.CatalogEventService;
import com.elasticpath.domain.catalog.Product;

/**
 * The content provider class is responsible for providing products to the advanced search view.
 */
public class AdvancedSearchProductContentProvider extends ProductListViewContentProvider implements AdvancedSearchProductListener {

	/**
	 * Constructs the advanced search product content provider. 
	 * 
	 * @param productListView the product list view
	 */
	public AdvancedSearchProductContentProvider(final AbstractProductListView productListView) {
		super(productListView);
		CatalogEventService.getInstance().addProductListener(this);
		AdvancedSearchEventService.getInstance().addAdvancedSearchProductListener(productListView.getPartName(), this);
	}
	
	@Override
	public void dispose() {
		CatalogEventService.getInstance().removeProductListener(this);
		AdvancedSearchEventService.getInstance().removeAdvancedSearchProductListener(getProductListView().getPartName());
	}

	@Override
	public void productAdvancedSearchResultReturned(final SearchResultEvent<Product> event) {

		PlatformUI.getWorkbench().getDisplay().syncExec(() -> {

			getProductListView().setResultsStartIndex(event.getStartIndex());
			getProductListView().setResultsCount(event.getTotalNumberFound());
			getProductListView().getViewer().setInput(wrapProducts(event.getItems()));
			getProductListView().updateNavigationComponents();
			handleSearchResultMessage(CatalogMessages.get().ProductListView_No_Result_Found);
		});
	}

}

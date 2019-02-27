/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import com.elasticpath.cmclient.core.ui.PerspectiveDefaults;
import com.elasticpath.cmclient.fulfillment.views.SearchView;
import com.elasticpath.cmclient.fulfillment.views.customer.CustomerSearchResultsView;
import com.elasticpath.cmclient.fulfillment.views.order.OrderSearchResultsView;
import com.elasticpath.cmclient.jobs.views.CustomerJobListView;

/**
 * Factory for specifying the layout of the perspective.
 */
public class FulfillmentPerspectiveFactory implements IPerspectiveFactory {
	/**
	 * Fulfillment perspective ID.
	 */
	public static final String PERSPECTIVE_ID = "com.elasticpath.cmclient.fulfillment.perspective"; //$NON-NLS-1$

	/**
	 * Called by Eclipse to layout the perspective.
	 * 
	 * @param layout the page layout
	 */
	@Override
	public void createInitialLayout(final IPageLayout layout) {
		final String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);

		layout.addView(SearchView.ID_SEARCH_VIEW, IPageLayout.LEFT, PerspectiveDefaults.FULFILLMENT_LEFT_RATIO, editorArea);
		layout.getViewLayout(SearchView.ID_SEARCH_VIEW).setCloseable(false);

		final IFolderLayout folder = layout.createFolder("views", IPageLayout.TOP, PerspectiveDefaults.TOP_RATIO, editorArea); //$NON-NLS-1$

		folder.addPlaceholder(CustomerJobListView.VIEW_ID);
		layout.getViewLayout(CustomerJobListView.VIEW_ID).setMoveable(false);

		folder.addPlaceholder(CustomerSearchResultsView.VIEW_ID);
		layout.getViewLayout(CustomerSearchResultsView.VIEW_ID).setMoveable(false);

		folder.addPlaceholder(OrderSearchResultsView.VIEW_ID);
		layout.getViewLayout(OrderSearchResultsView.VIEW_ID).setMoveable(false);

		layout.addFastView(IPageLayout.ID_PROGRESS_VIEW);
	}

}

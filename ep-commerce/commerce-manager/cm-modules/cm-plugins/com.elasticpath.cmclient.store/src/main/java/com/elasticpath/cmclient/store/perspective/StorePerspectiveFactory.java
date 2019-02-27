/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.store.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import com.elasticpath.cmclient.core.ui.PerspectiveDefaults;
import com.elasticpath.cmclient.store.StorePlugin;
import com.elasticpath.cmclient.store.promotions.views.PromotionsSearchResultsView;
import com.elasticpath.cmclient.store.shipping.views.ShippingLevelsSearchResultsView;
import com.elasticpath.cmclient.store.targetedselling.conditionalexpression.views.ConditionalExpressionSearchResultsView;
import com.elasticpath.cmclient.store.targetedselling.dynamiccontent.views.DynamicContentSearchResultsView;
import com.elasticpath.cmclient.store.views.SearchView;

/**
 * Factory for specifying the layout of the perspective.
 */
public class StorePerspectiveFactory implements IPerspectiveFactory {
	/**
	 * Store perspective ID.
	 */
	public static final String PERSPECTIVE_ID = "com.elasticpath.cmclient.store.storeperspective"; //$NON-NLS-1$

	/**
	 * Called by Eclipse to layout the perspective.
	 * 
	 * @param layout the page layout
	 */
	@Override
	public void createInitialLayout(final IPageLayout layout) {
		final String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);

		layout.addView(SearchView.ID_SEARCH_VIEW, IPageLayout.LEFT, PerspectiveDefaults.LEFT_RATIO, editorArea);
		layout.getViewLayout(SearchView.ID_SEARCH_VIEW).setCloseable(false);
		layout.getViewLayout(SearchView.ID_SEARCH_VIEW).setMoveable(false);

		final IFolderLayout right = layout.createFolder("searchResults", IPageLayout.TOP, PerspectiveDefaults.TOP_RATIO, //$NON-NLS-1$
			editorArea);
		right.addPlaceholder("com.elasticpath.cmclient.store.*.views.*"); //$NON-NLS-1$

		right.addView(ShippingLevelsSearchResultsView.VIEW_ID);
		layout.getViewLayout(ShippingLevelsSearchResultsView.VIEW_ID).setMoveable(false);

		right.addView(PromotionsSearchResultsView.ID_PROMOTIONS_SEARCH_RESULTS_VIEW);
		layout.getViewLayout(PromotionsSearchResultsView.ID_PROMOTIONS_SEARCH_RESULTS_VIEW).setMoveable(false);

		if (StorePlugin.ENABLE_DYNAMIC_CONTENT_IN_UI) {
			right.addView(DynamicContentSearchResultsView.VIEW_ID);
			layout.getViewLayout(DynamicContentSearchResultsView.VIEW_ID).setMoveable(false);

			right.addView(ConditionalExpressionSearchResultsView.VIEW_ID);
			layout.getViewLayout(ConditionalExpressionSearchResultsView.VIEW_ID).setMoveable(false);
		}

		// hack to only show commands in this perspective and not others
		// eclipse doesn't currently have a way using the new command framework to show icons
		// based on the current perspective
		layout.addActionSet("com.elasticpath.cmclient.store.actionSets.emptyActionSet"); //$NON-NLS-1$
	}
}

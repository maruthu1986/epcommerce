/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.changeset.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import com.elasticpath.cmclient.changeset.views.ChangeSetsView;
import com.elasticpath.cmclient.changeset.views.SearchView;
import com.elasticpath.cmclient.core.ui.PerspectiveDefaults;

/**
 * Factory for specifying the layout of the perspective.
 */
public class ChangeSetPerspectiveFactory implements IPerspectiveFactory {

	/**
	 * Change Sets perspective ID.
	 */
	public static final String PERSPECTIVE_ID = "com.elasticpath.cmclient.changeset.perspective"; //$NON-NLS-1$

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

		final IFolderLayout folder = layout.createFolder("views", IPageLayout.TOP, PerspectiveDefaults.TOP_RATIO, editorArea); //$NON-NLS-1$

		folder.addPlaceholder(ChangeSetsView.ID_CHANGESETS_VIEW);
		layout.getViewLayout(ChangeSetsView.ID_CHANGESETS_VIEW).setMoveable(false);

		layout.addFastView(IPageLayout.ID_PROGRESS_VIEW);
	}
}

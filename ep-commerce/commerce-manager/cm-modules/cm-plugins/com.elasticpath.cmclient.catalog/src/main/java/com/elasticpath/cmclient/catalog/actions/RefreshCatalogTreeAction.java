/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.catalog.actions;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.core.CoreImageRegistry;

/**
 * This action refreshes the catalog tree viewer.
 */
public class RefreshCatalogTreeAction extends AbstractCatalogViewAction {

	private static final String ACTION_NAME = "refreshCatalogTreeAction"; //$NON-NLS-1$
	private final Viewer viewer;
	
	/**
	 * Constructor.
	 * 
	 * @param viewer the viewer to refresh
	 */
	public RefreshCatalogTreeAction(final Viewer viewer) {
		super(CatalogMessages.get().CatalogBrowseView_Action_Refresh, CoreImageRegistry.IMAGE_REFRESH);
		this.viewer = viewer;
	}

	@Override
	protected void pageSelectionChanged(final ISelection selection) {
		// not interested in that event
	}
	
	@Override
	protected boolean isAuthorized() {
		return true;
	}

	@Override
	public String getTargetIdentifier() {
		return ACTION_NAME;
	}

	@Override
	public void run() {
		this.viewer.refresh();
	}
}

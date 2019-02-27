/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.presentation;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * Dummy Perspective. It is used as first entrance perspective.
 * This perspective will be switched to the one that is defined by the PerspectiveService in ApplicationWorkbenchAdvisor,
 * so that the listeners registered by the PerspectiveManager will be triggered and
 * Icons will be updated and required perspective will be opened
 */
public class EmptyPerspective implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(final IPageLayout layout) {
		//do nothing
	}
}

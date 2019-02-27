/**
 * Copyright (c) Elastic Path Software Inc., 2007
 *
 */
package com.elasticpath.cmclient.reporting.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import com.elasticpath.cmclient.core.ui.PerspectiveDefaults;
import com.elasticpath.cmclient.reporting.views.ReportingNavigationView;
import com.elasticpath.cmclient.reporting.views.ReportingView;

/**
 * The default perspective factory for the Admin plugin.
 */
public class ReportingPerspectiveFactory implements IPerspectiveFactory {
	/** Perspective ID. **/
	public static final String PERSPECTIVE_ID = "com.elasticpath.cmclient.reporting.perspectives.ReportingPerspective"; //$NON-NLS-1$

	/**
	 * Creates the initial arrangement of Admin-related views.
	 * This will be invoked to set the layout when the user requests 
	 * that the perspective be reset.
	 * 
	 * @param layout the perspective's page layout
	 */
	public void createInitialLayout(final IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		
		//Put the ExpandBar on the left
		layout.addView(ReportingNavigationView.VIEW_ID, IPageLayout.LEFT, PerspectiveDefaults.LEFT_RATIO, editorArea);
		layout.getViewLayout(ReportingNavigationView.VIEW_ID).setCloseable(false);
		layout.getViewLayout(ReportingNavigationView.VIEW_ID).setMoveable(false);
		
		//Put the adminSection views on the right
		IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT,  PerspectiveDefaults.TOP_RATIO, editorArea); //$NON-NLS-1$
		right.addPlaceholder(ReportingView.REPORTVIEWID); 

		layout.setFixed(true);
	}	

}

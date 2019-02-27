/**
 * Copyright (c) Elastic Path Software Inc., 2007
 *
 */
package com.elasticpath.cmclient.admin.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import com.elasticpath.cmclient.admin.views.AdminSectionsNavigationView;
import com.elasticpath.cmclient.core.ui.PerspectiveDefaults;

/**
 * The default perspective factory for the Admin plugin.
 */
public class AdminPerspectiveFactory implements IPerspectiveFactory {
	/** Perspective ID. **/
	public static final String PERSPECTIVE_ID = "com.elasticpath.cmclient.admin.perspectives.AdminPerspective"; //$NON-NLS-1$

	private static final String USER_SEARCH_VIEW_ID = "com.elasticpath.cmclient.admin.users.views.UserSearchView"; //$NON-NLS-1$

	/**
	 * Creates the initial arrangement of Admin-related views.
	 * This will be invoked to set the layout when the user requests 
	 * that the perspective be reset.
	 * 
	 * @param layout the perspective's page layout
	 */
	public void createInitialLayout(final IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);

		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, PerspectiveDefaults.LEFT_RATIO, editorArea); //$NON-NLS-1$
		
		//Put the ExpandBar on the left
		left.addView(AdminSectionsNavigationView.VIEW_ID);
		left.addPlaceholder(USER_SEARCH_VIEW_ID);
		layout.getViewLayout(AdminSectionsNavigationView.VIEW_ID).setCloseable(false);
		layout.getViewLayout(AdminSectionsNavigationView.VIEW_ID).setMoveable(false);
		layout.getViewLayout(USER_SEARCH_VIEW_ID).setMoveable(false);
		
		//Put the adminSection views on the right
		IFolderLayout right = layout.createFolder("right", IPageLayout.TOP, PerspectiveDefaults.TOP_RATIO, editorArea); //$NON-NLS-1$
		right.addPlaceholder("com.elasticpath.cmclient.admin.*"); //$NON-NLS-1$
		right.addView("com.elasticpath.cmclient.admin.configuration.views.SystemConfigurationView");  //$NON-NLS-1$
		//right.addPlaceholder("")
		
		//layout.setFixed(true); // MSC-5555, but NTRN-848 wants it off		
	}	

}

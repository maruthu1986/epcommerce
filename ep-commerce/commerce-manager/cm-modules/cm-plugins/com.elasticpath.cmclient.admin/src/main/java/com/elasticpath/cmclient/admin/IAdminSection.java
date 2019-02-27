/**
 * Copyright (c) Elastic Path Software Inc., 2007
 *
 */
package com.elasticpath.cmclient.admin;

import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * Defines the interface that must be implemented by AdminSection plugins.
 */
public interface IAdminSection {

	/**
	 * Creates the controls specified by the admin section.
	 * 
	 * @param toolkit the top level toolkit which contains all admin sections
	 * @param parent the parent section which is the container for this specific admin section
	 * @param site the Workbench site, so that the composite can get a reference to Views that it should open.
	 */
	void createControl(FormToolkit toolkit, Section parent, IWorkbenchPartSite site);

	/**
	 * Returns whether the user is authorized to manage the admin section.
	 *
	 * @return <code>true</code> if the user authorized to manage the admin section, <code>false</code> otherwise
	 */
	boolean isAuthorized();
}

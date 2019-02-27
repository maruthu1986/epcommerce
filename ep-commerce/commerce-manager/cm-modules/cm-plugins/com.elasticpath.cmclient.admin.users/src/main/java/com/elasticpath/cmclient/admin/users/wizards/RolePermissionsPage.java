/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.users.wizards;

import org.eclipse.core.databinding.DataBindingContext;

import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.wizard.AbstractEPWizardPage;
import com.elasticpath.domain.cmuser.UserRole;

/**
 * The Role Permissions wizard page.
 */
public class RolePermissionsPage extends AbstractEPWizardPage<UserRole> {

	/**
	 * 
	 */
	private static final int HEIGHT = 290;

	/**
	 * 
	 */
	private static final int WIDTH = 500;

	private final UserRole userRole;

	private RolePermissionsDualListbox permissionsListbox;

	/**
	 * Constructor.
	 *  @param pageName the page name
	 * @param title the page title
	 * @param message the message
	 * @param model the model object
	 */
	protected RolePermissionsPage(final String pageName, final String title, final String message,
								  final UserRole model) {
		super(1, false, pageName, title, message, new DataBindingContext());
		this.userRole = model;
	}

	@Override
	protected void bindControls() {
		// Do nothing

	}

	@Override
	protected void createEpPageContent(final IEpLayoutComposite pageComposite) {
		this.permissionsListbox = new RolePermissionsDualListbox(pageComposite, userRole);
		this.permissionsListbox.initialize();
		this.permissionsListbox.setPreferredSize(WIDTH, HEIGHT);
		this.setControl(pageComposite.getSwtComposite());
	}

	@Override
	protected void populateControls() {
		// Do nothing
	}
}

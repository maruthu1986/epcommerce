/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.users.wizards;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Composite;

import com.elasticpath.cmclient.admin.users.AdminUsersMessages;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.wizard.AbstractEPWizardPage;
import com.elasticpath.domain.cmuser.CmUser;

/**
 * The Role Assignment wizard page.
 */
public class RoleAssignmentPage extends AbstractEPWizardPage<CmUser> {

	private final CmUser cmUser;

	/**
	 * Constructor.
	 *  @param pageName the page name
	 * @param title the page title
	 * @param message the message
	 * @param cmUser the model object
	 */
	protected RoleAssignmentPage(final String pageName, final String title, final String message,
								 final CmUser cmUser) {
		super(1, false, pageName, title, message, new DataBindingContext());
		this.cmUser = cmUser;
	}

	@Override
	protected void bindControls() {
		// Do nothing
	}

	/**
	 * Create the wizard's page composite.
	 *
	 * @param parent the page's parent
	 */
	public void createControl(final Composite parent) {
		IEpLayoutComposite mainComposite = CompositeFactory.createGridLayoutComposite(parent, 1, false);
		final RoleAssignmentDualListBox listbox = new RoleAssignmentDualListBox(
				mainComposite,
				this.cmUser,
				AdminUsersMessages.get().RoleAssignment_AvailableRoles,
				AdminUsersMessages.get().RoleAssignment_AssignedRoles);

		listbox.createControls();
		
		/* MUST be called */
		this.setControl(mainComposite.getSwtComposite());
	}
	
	@Override
	protected void createEpPageContent(final IEpLayoutComposite pageComposite) {
		// Do nothing
	}

	@Override
	protected void populateControls() {
		//Do nothing
	}
}

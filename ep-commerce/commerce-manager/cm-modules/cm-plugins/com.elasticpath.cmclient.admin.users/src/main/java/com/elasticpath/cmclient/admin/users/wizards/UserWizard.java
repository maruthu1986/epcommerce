/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.users.wizards;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.admin.users.AdminUsersImageRegistry;
import com.elasticpath.cmclient.admin.users.AdminUsersMessages;
import com.elasticpath.cmclient.core.wizard.AbstractEpWizard;
import com.elasticpath.cmclient.core.wizard.EpWizardDialog;
import com.elasticpath.domain.cmuser.CmUser;

/**
 * The wizard for creating and editing CM Users.
 */
public class UserWizard extends AbstractEpWizard<CmUser> {

	private final String title;

	private static final String PAGE_NAME_USER_DETAILS = "UserDetailsPage"; //$NON-NLS-1$

	private final CmUser cmUser;

	/**
	 * Constructor.
	 * 
	 * @param cmUser the cmUser object that is being edited. If null, a new one will be created.
	 */
	protected UserWizard(final CmUser cmUser) {
		super("UserWizard", null, null); //$NON-NLS-1$
		if (cmUser.isPersisted()) { // Existing user
			this.setWindowTitle(AdminUsersMessages.get().EditUser);
			this.title = AdminUsersMessages.get().EditUser;
		} else { // New user
			this.setWindowTitle(AdminUsersMessages.get().CreateUser);
			this.title = AdminUsersMessages.get().CreateUser;
		}
		this.setPagesTitleBlank(title + " - " + AdminUsersMessages.get().TitleStep); //$NON-NLS-1$
		this.cmUser = cmUser;
		this.cmUser.initialize();
	}	

	/**
	 * Shows this wizard.
	 * 
	 * @param shell the Eclipse {@link Shell}
	 * @param cmUser the CmUser instance
	 * @return Window#OK in case the customer completed the wizard (clicked the Finish button)
	 */
	public static int showWizard(final Shell shell, final CmUser cmUser) {
		UserWizard wizard = new UserWizard(cmUser);
		
		WizardDialog dialog = new EpWizardDialog(shell, wizard);
		dialog.addPageChangingListener(wizard);
		return dialog.open();
	}

	/**
	 * Get the Model Object for this wizard, to which the data will be bound.
	 * 
	 * @return the cmUser object
	 */
	public CmUser getModel() {
		return this.cmUser;
	}
	
	@Override
	public boolean performFinish() {
		return ((UserDetailsPage) getPage(PAGE_NAME_USER_DETAILS)).processUserParametersValidation();
	}

	@Override
	public void addPages() {
		addPage(new UserDetailsPage(PAGE_NAME_USER_DETAILS, title, AdminUsersMessages.get().UserDetails, this.getModel()));
		addPage(new RoleAssignmentPage("RoleAssignmentPage", title, AdminUsersMessages.get().RoleAssignments, this.getModel())); //$NON-NLS-1$
		addPage(new CatalogPermissionsPage("CatalogPermissionsPage", title, AdminUsersMessages.get().CatalogPermissions,  //$NON-NLS-1$
				this.getModel()));
		addPage(new StorePermissionsPage("StorePermissionsPage", title, AdminUsersMessages.get().StorePermissions, this.getModel())); //$NON-NLS-1$
		addPage(new WarehousePermissionsPage("WarehousePermissionsPage", title, AdminUsersMessages.get().WarehousePermissions,  //$NON-NLS-1$
				this.getModel()));
		addPage(new PriceListPermissionsPage("PriceListPermissionsPage", title, AdminUsersMessages.get().PriceListPermissions,  //$NON-NLS-1$
				this.getModel()));
		
	}

	@Override
	protected Image getWizardImage() {
		if (cmUser.isPersisted()) {
			return AdminUsersImageRegistry.getImage(AdminUsersImageRegistry.IMAGE_USER_EDIT);
		}
		return AdminUsersImageRegistry.getImage(AdminUsersImageRegistry.IMAGE_USER_CREATE);
	}
}

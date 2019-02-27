/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.users.wizards;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.admin.users.AdminUsersImageRegistry;
import com.elasticpath.cmclient.admin.users.AdminUsersMessages;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.wizard.AbstractEpWizard;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cmuser.UserRole;
import com.elasticpath.service.cmuser.UserRoleService;

/**
 * The wizard for creating and editing CM Roles.
 */
public class RoleWizard extends AbstractEpWizard<UserRole> {
	private static final Logger LOG = Logger.getLogger(RoleWizard.class);

	private final String title;

	private final UserRole userRole;

	private final UserRoleService service;

	private static final String PAGE_NAME_ROLE_DETAILS = "RoleDetailsPage"; //$NON-NLS-1$

	private static final String PAGE_NAME_ROLE_PERMISSIONS = "RolePermissionsPage"; //$NON-NLS-1$

	@Override
	public boolean performFinish() {
		if (roleNameExists()) {
			RoleDetailsPage roleDetailsPage = ((RoleDetailsPage) this.getPage(PAGE_NAME_ROLE_DETAILS));
			roleDetailsPage.setErrorMessage(AdminUsersMessages.get().RoleNameExists);
			this.getContainer().showPage(roleDetailsPage);
			return false;
		}
		return true;
	}

	private boolean roleNameExists() {
		LOG.debug("Checking if RoleName exists"); //$NON-NLS-1$
		if (service.nameExists(this.userRole)) {
			return true;
		}
		return false;
	}

	@Override
	protected UserRole getModel() {
		return userRole;
	}

	/**
	 * Constructor.
	 * 
	 * @param userRole the userRole object that is being edited. If null, a new one will be created.
	 */
	public RoleWizard(final UserRole userRole) {
		super("RoleWizard", null, null); //$NON-NLS-1$
		if (userRole.isPersisted()) {
			this.setWindowTitle(AdminUsersMessages.get().EditRole);
			this.title = AdminUsersMessages.get().EditRole;
		} else {
			this.setWindowTitle(AdminUsersMessages.get().CreateRole);
			this.title = AdminUsersMessages.get().CreateRole;
		}
		this.userRole = userRole;
		this.setPagesTitleBlank(title + " - " + AdminUsersMessages.get().TitleStep); //$NON-NLS-1$
		this.service = (UserRoleService) ServiceLocator.getService(ContextIdNames.USER_ROLE_SERVICE);
	}

	@Override
	public void addPages() {
		this.addPage(new RoleDetailsPage(PAGE_NAME_ROLE_DETAILS, title, AdminUsersMessages.get().RoleDetails, this.getModel()));
		this.addPage(new RolePermissionsPage(PAGE_NAME_ROLE_PERMISSIONS, title, AdminUsersMessages.get().RolePermissions, this.getModel()));
	}

	@Override
	protected Image getWizardImage() {
		if (userRole.isPersisted()) {
			return AdminUsersImageRegistry.getImage(AdminUsersImageRegistry.IMAGE_ROLE_EDIT);
		}
		return AdminUsersImageRegistry.getImage(AdminUsersImageRegistry.IMAGE_USER_CREATE);
	}
}

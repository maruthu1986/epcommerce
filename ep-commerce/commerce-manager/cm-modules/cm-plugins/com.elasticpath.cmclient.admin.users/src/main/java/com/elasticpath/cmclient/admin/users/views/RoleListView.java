/**
 * Copyright (c) Elastic Path Software Inc., 2007
 *
 */
package com.elasticpath.cmclient.admin.users.views;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.MessageBox;

import com.elasticpath.cmclient.admin.users.AdminUsersImageRegistry;
import com.elasticpath.cmclient.admin.users.AdminUsersMessages;
import com.elasticpath.cmclient.admin.users.AdminUsersPlugin;
import com.elasticpath.cmclient.admin.users.wizards.RoleWizard;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.views.AbstractListView;
import com.elasticpath.cmclient.core.wizard.EpWizardDialog;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cmuser.UserRole;
import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.service.cmuser.CmUserService;
import com.elasticpath.service.cmuser.UserRoleService;

/**
 * View to show and allow the manipulation of the available Roles in CM.
 */
public class RoleListView extends AbstractListView {
	private static final Logger LOG = Logger.getLogger(RoleListView.class);

	/** The View's ID. */
	public static final String VIEW_ID = "com.elasticpath.cmclient.admin.users.views.RoleListView"; //$NON-NLS-1$

	// Column indices
	private static final int INDEX_ROLENAME = 0;

	private static final int INDEX_ROLEDESCRIPTION = 1;

	private static final String ROLE_TABLE = "Role"; //$NON-NLS-1$

	private final Separator roleActionGroup = new Separator("roleActionGroup"); //$NON-NLS-1$

	// Actions
	private final Action createRoleAction = new CreateRoleAction();

	private final Action editRoleAction = new EditRoleAction();

	private final Action deleteRoleAction = new DeleteRoleAction();

	// Actions have to be wrapped in ActionContributionItems so that they can be forced to display both text and image
	private final ActionContributionItem createRoleActionContributionItem = new ActionContributionItem(createRoleAction);

	private final ActionContributionItem editRoleActionContributionItem = new ActionContributionItem(editRoleAction);

	private final ActionContributionItem deleteRoleActionContributionItem = new ActionContributionItem(deleteRoleAction);

	/**
	 * Constructor.
	 */
	public RoleListView() {
		super(false, ROLE_TABLE);
	}

	@Override
	public void setFocus() {
		// do nothing
	}

	@Override
	protected Object[] getViewInput() {
		final UserRoleService service = (UserRoleService) ServiceLocator.getService(ContextIdNames.USER_ROLE_SERVICE);
		final List< UserRole > userRoles = service.list();
		return userRoles.toArray(new UserRole[userRoles.size()]);
	}

	@Override
	protected ITableLabelProvider getViewLabelProvider() {
		return new RoleListViewLabelProvider();
	}

	@Override
	protected void initializeTable(final IEpTableViewer viewerTable) {
		final String[] columnNames = new String[] { AdminUsersMessages.get().Role, AdminUsersMessages.get().RoleDescription };

		final int[] columnWidths = new int[] { 200, 400 };

		for (int i = 0; i < columnNames.length; i++) {
			viewerTable.addTableColumn(columnNames[i], columnWidths[i]);
		}
		addDoubleClickAction(editRoleAction);
	}

	@Override
	protected String getPluginId() {
		return AdminUsersPlugin.PLUGIN_ID;
	}

	@Override
	protected void initializeViewToolbar() {

		getToolbarManager().add(roleActionGroup);

		createRoleActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		editRoleActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		deleteRoleActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);

		getToolbarManager().appendToGroup(roleActionGroup.getGroupName(), editRoleActionContributionItem);
		getToolbarManager().appendToGroup(roleActionGroup.getGroupName(), createRoleActionContributionItem);
		getToolbarManager().appendToGroup(roleActionGroup.getGroupName(), deleteRoleActionContributionItem);

		// Disable buttons until a row is selected.
		toggleEditDelete(false, false);
		// Ensure that if an unmodifiable role is selected the delete and edit actions are disabled
		this.getViewer().addSelectionChangedListener(event -> {
			final UserRole selectedRole = getSelectedUserRole();
			if (selectedRole == null) {
				return;
			}
			if (selectedRole.isUnmodifiableRole()) {
				toggleEditDelete(false, false);
			} else {
				toggleEditDelete(true, isDeletableRole(selectedRole));
			}
		});
	}
	
	private void toggleEditDelete(final boolean isEditable, final boolean isDeletable) {
		deleteRoleAction.setEnabled(isDeletable);
		editRoleAction.setEnabled(isEditable);
	}
	
	private boolean isDeletableRole(final UserRole role) {
		if (role.isUnmodifiableRole()) {
			return false;
		}
		final CmUserService service = (CmUserService) ServiceLocator.getService(ContextIdNames.CMUSER_SERVICE);
		if (service.findByRoleId(role.getUidPk()).isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * Gets the currently-selected role.
	 * 
	 * @return the currently-selected UserRole
	 */
	protected UserRole getSelectedUserRole() {
		final ISelection selection = getViewer().getSelection();
		if (selection == null) {
			return null;
		}
		return (UserRole) ((IStructuredSelection) selection).getFirstElement();
	}

	/**
	 * Label provider for the view.
	 */
	protected class RoleListViewLabelProvider extends LabelProvider implements ITableLabelProvider {

		/**
		 * Get the image to put in each column.
		 * 
		 * @param element the row object
		 * @param columnIndex the column index
		 * @return the Image to put in the column
		 */
		public Image getColumnImage(final Object element, final int columnIndex) {
			if (!(element instanceof UserRole)) {
				return null;
			}
			ImageDescriptor imageDescriptor = null;
//			if (columnIndex == RoleListView.INDEX_ROLEIMAGE) {
//				imageDescriptor = CoreImageRegistry.IMAGE_ROLE;
//			}
			return CoreImageRegistry.getImage(imageDescriptor);
		}

		/**
		 * Get the text to put in each column.
		 * 
		 * @param element the row object
		 * @param columnIndex the column index
		 * @return the String to put in the column
		 */
		public String getColumnText(final Object element, final int columnIndex) {
			if (!(element instanceof UserRole)) {
				return null;
			}
			final UserRole role = (UserRole) element;

			switch (columnIndex) {

//			case RoleListView.INDEX_ROLEIMAGE:
//				return AdminUsersMessages.EMPTY_STRING;
			case RoleListView.INDEX_ROLENAME:
				return getName(role);
			case RoleListView.INDEX_ROLEDESCRIPTION:
				String desc = role.getDescription();
				if (desc != null) {
					return desc;
				}
				return AdminUsersMessages.EMPTY_STRING;
			default:
				return AdminUsersMessages.EMPTY_STRING;
			}
		}
		
		private String getName(final UserRole role) {
			String result = role.getName();
			if (result.equals(UserRole.CMUSER)) {
				return AdminUsersMessages.get().CMUser;
			} else if (result.equals(UserRole.SUPERUSER)) {
				return AdminUsersMessages.get().SuperUser;
			} else if (result.equals(UserRole.WSUSER)) {
				return AdminUsersMessages.get().WSUser;
			}
			return result;
		}
	}

	/**
	 * Begin the process for creating a new role.
	 */
	protected class CreateRoleAction extends Action {

		/**
		 * Constructor.
		 */
		public CreateRoleAction() {
			super();
			this.setImageDescriptor(AdminUsersImageRegistry.IMAGE_ROLE_CREATE);
			this.setToolTipText(AdminUsersMessages.get().CreateRole);
			this.setText(AdminUsersMessages.get().CreateRole);
		}

		@Override
		public void run() {
			LOG.debug("CreateRole Action called."); //$NON-NLS-1$
			final UserRole userRole = (UserRole) ServiceLocator.getService(ContextIdNames.USER_ROLE);
			// Create the wizard
			final RoleWizard wizard = new RoleWizard(userRole);
			// Create the wizard dialog
			final WizardDialog dialog = new EpWizardDialog(RoleListView.this.getSite().getShell(), wizard);
			// Open the wizard dialog
			if (Window.OK == dialog.open()) {
				final UserRoleService userRoleService = (UserRoleService) ServiceLocator.getService(
						ContextIdNames.USER_ROLE_SERVICE);
				userRoleService.update(userRole);
				refreshViewerInput();
			}
		}
	}

	/**
	 * Begin the process for editing a role.
	 */
	private class EditRoleAction extends Action {

		/**
		 * Constructor.
		 */
		EditRoleAction() {
			super();
			this.setImageDescriptor(AdminUsersImageRegistry.IMAGE_ROLE_EDIT);
			this.setToolTipText(AdminUsersMessages.get().EditRole);
			this.setText(AdminUsersMessages.get().EditRole);
		}

		@Override
		public void run() {
			LOG.debug("EditRole Action called."); //$NON-NLS-1$
			final UserRole selectedRole = getSelectedUserRole();
			if (selectedRole == null) {
				return;
			}
			final UserRole userRole = getPersistedUserRole(selectedRole.getUidPk());
			final RoleWizard wizard = new RoleWizard(userRole);
			final WizardDialog dialog = new EpWizardDialog(RoleListView.this.getSite().getShell(), wizard);
			if (Window.OK == dialog.open()) {
				final UserRoleService userRoleService = (UserRoleService) ServiceLocator.getService(
						ContextIdNames.USER_ROLE_SERVICE);
				userRoleService.update(userRole);
				refreshViewerInput();
			}
		}

		private UserRole getPersistedUserRole(final long roleId) {
			final UserRoleService roleService = (UserRoleService) ServiceLocator.getService(ContextIdNames.USER_ROLE_SERVICE);
			return roleService.get(roleId);
		}
	}

	/**
	 * Begin the process for deleting a role.
	 */
	private class DeleteRoleAction extends Action {

		/**
		 * Constructor.
		 */
		DeleteRoleAction() {
			super();
			this.setImageDescriptor(AdminUsersImageRegistry.IMAGE_ROLE_DELETE);
			this.setToolTipText(AdminUsersMessages.get().DeleteRole);
			this.setText(AdminUsersMessages.get().DeleteRole);
		}

		@Override
		public void run() {
			LOG.debug("DeleteRole Action called."); //$NON-NLS-1$
			final UserRole userRole = getSelectedUserRole();
			if (userRole == null) {
				return;
			}
			final MessageBox confirmBox = new MessageBox(RoleListView.this.getSite().getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
			confirmBox.setText(AdminUsersMessages.get().DeleteRole);
			confirmBox.setMessage(AdminUsersMessages.get().ConfirmDeleteRole + AdminUsersMessages.get().ConfirmLineSeparator + userRole.getName());
			if (confirmBox.open() == SWT.NO) {
				return;
			}
			deleteUserRole(userRole);
			refreshViewerInput();
		}

		private void deleteUserRole(final UserRole userRole) {
			LOG.info("Deleting Role: " + userRole.getAuthority()); //$NON-NLS-1$
			final UserRoleService roleService = (UserRoleService) ServiceLocator.getService(ContextIdNames.USER_ROLE_SERVICE);
			try {
				roleService.remove(userRole);
			} catch (EpPersistenceException e) {
				LOG.error(e);
			}
		}
	}

	@Override
	protected String getPartId() {
		return VIEW_ID;
	}
}

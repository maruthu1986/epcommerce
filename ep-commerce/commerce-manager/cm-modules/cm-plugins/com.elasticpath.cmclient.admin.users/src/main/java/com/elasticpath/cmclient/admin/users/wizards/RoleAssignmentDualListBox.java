/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.users.wizards;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.elasticpath.cmclient.admin.users.AdminUsersMessages;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.ui.framework.AbstractEpDualListBoxControl;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.cmuser.UserRole;
import com.elasticpath.service.cmuser.UserRoleService;

/**
 * The RoleAssignement dual listbox.
 */
public class RoleAssignmentDualListBox extends AbstractEpDualListBoxControl<CmUser> {

	/**
	 * Constructor.
	 *
	 * @param parent the parent composite
	 * @param cmUser the model object (the CmUser)
	 * @param availableTitle the title for the Available listbox
	 * @param assignedTitle the title for the Assigned listbox
	 */
	public RoleAssignmentDualListBox(
			final IEpLayoutComposite parent,
			final CmUser cmUser,
			final String availableTitle,
			final String assignedTitle
			) {
		super(parent, cmUser, availableTitle, assignedTitle, MULTI_SELECTION, null, EpState.EDITABLE);
	}

	@Override
	protected boolean assignToModel(final IStructuredSelection selection) {
		if (selection == null || selection.isEmpty()) {
			return false;
		}
		
		final CmUser cmUser = this.getModel();
		for (final Iterator< ? > it = selection.iterator(); it.hasNext();) {
			cmUser.addUserRole((UserRole) it.next());
		}
		return true;
	}

	@Override
	protected boolean removeFromModel(final IStructuredSelection selection) {
		if (selection == null || selection.isEmpty()) {
			return false;
		}
		
		final CmUser cmUser = this.getModel();
		for (final Iterator< ? > it = selection.iterator(); it.hasNext();) {
			cmUser.removeUserRole((UserRole) it.next());
		}
		return true;
	}

	@Override
	public Collection<UserRole> getAssigned() {
		return this.getModel().getUserRoles();
	}

	@Override
	public Collection<UserRole> getAvailable() {
		final UserRoleService service = (UserRoleService) ServiceLocator.getService(ContextIdNames.USER_ROLE_SERVICE);
		return service.list();
	}

	@Override
	public ViewerFilter getAvailableFilter() {
		return new RoleAssignmentAvailableViewerFilter();
	}

	/**
	 * Filters the AvailableListView so that it doesn't display any objects that are in the AssignedListView. Subclasses should override the Select
	 * method if they want to do any filtering.
	 */
	protected class RoleAssignmentAvailableViewerFilter extends ViewerFilter {

		@Override
		public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
			// TODO: Use equals method of UserRole instead
			final String roleGuid = ((UserRole) element).getGuid();
			for (final UserRole role : RoleAssignmentDualListBox.this.getAssigned()) {
				if (role.getGuid().equals(roleGuid)) {
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * Label provider for UserRole listviewers.
	 */
	class UserRoleLabelProvider extends LabelProvider {

		@Override
		public String getText(final Object element) {
			String result;
			if (element instanceof UserRole) {
				result = ((UserRole) element).getName();
				if (result.equals(UserRole.CMUSER)) {
					return AdminUsersMessages.get().CMUser;
				} else if (result.equals(UserRole.SUPERUSER)) {
					return AdminUsersMessages.get().SuperUser;
				} else if (result.equals(UserRole.WSUSER)) {
					return AdminUsersMessages.get().WSUser;
				}
				return result;
			}
		
			return AdminUsersMessages.EMPTY_STRING;
		}

		@Override
		public boolean isLabelProperty(final Object element, final String property) {
			return false;
		}

	}

	@Override
	protected ILabelProvider getLabelProvider() {
		return new UserRoleLabelProvider();
	}
}

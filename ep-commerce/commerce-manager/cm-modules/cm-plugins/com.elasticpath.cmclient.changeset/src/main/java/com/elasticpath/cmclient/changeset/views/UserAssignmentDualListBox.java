/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.changeset.views;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;

import com.elasticpath.cmclient.changeset.ChangeSetMessages;
import com.elasticpath.cmclient.changeset.ChangeSetPermissions;
import com.elasticpath.cmclient.changeset.helpers.UserViewFormatter;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareDualListBox;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.changeset.ChangeSetUserView;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.service.changeset.ChangeSetService;
import com.elasticpath.service.cmuser.CmUserService;

/**
 * Presents a dual list box control for assigning users to change sets.
 */
public class UserAssignmentDualListBox extends AbstractPolicyAwareDualListBox<ChangeSet> {
	
	private Collection<ChangeSetUserView> changeSetUserViews;

	/**
	 * Constructor.
	 *
	 * @param mainComposite the parent composite
	 * @param data the layout data
	 * @param container the policy container
	 * @param changeSet the model object (the changeSet)
	 * @param availableTitle the title for the Available listbox
	 * @param assignedTitle the title for the Assigned listbox
	 */
	public UserAssignmentDualListBox(final IPolicyTargetLayoutComposite mainComposite,
			final IEpLayoutData data,
			final PolicyActionContainer container, 
			final ChangeSet changeSet,
			final String availableTitle, 
			final String assignedTitle) {
		super(mainComposite, data, container, changeSet, availableTitle, assignedTitle, NONE);
	}
	
	
	@Override
	protected void customizeControls() {
		getAssignedTableViewer().setComparator(getChangeSetUserComparator());		
		getAvailableTableViewer().setComparator(getChangeSetUserComparator());
	}
	
	/**
	 * Returns a user comparator that compares the last name.
	 * @return a new viewer comparator
	 */
	public ViewerComparator getChangeSetUserComparator() {
		
		return new ViewerComparator() {
			
			@Override
			public int compare(final Viewer viewer, final Object element1, final Object element2) {
											
				ChangeSetUserView userOne = (ChangeSetUserView) element1;
				ChangeSetUserView userTwo = (ChangeSetUserView) element2;
				return UserViewFormatter.formatWithNameAndUserName(userOne).compareTo(UserViewFormatter.formatWithNameAndUserName(userTwo));
								
			}

		};
	}
	

	@Override
	protected boolean assignToModel(final IStructuredSelection selection) {
		if (selection == null || selection.isEmpty()) {
			return false;
		}
					
		for (final Iterator<ChangeSetUserView> it = selection.iterator(); it.hasNext();) {
			
			// Create a new change set user to add
			ChangeSetUserView changeSetUserView = it.next();
			getModel().addAssignedUser(changeSetUserView.getGuid());			
			changeSetUserViews.add(changeSetUserView);
		}
		return true;
	}

	@Override
	protected boolean removeFromModel(final IStructuredSelection selection) {
		if (selection == null || selection.isEmpty()) {
			return false;
		}
				
		final ChangeSet changeSet = this.getModel();
		
		for (final Iterator<ChangeSetUserView> it = selection.iterator(); it.hasNext();) {
			ChangeSetUserView changeSetUserView = it.next();						
			changeSet.removeAssignedUser(changeSetUserView.getGuid());
			changeSetUserViews.remove(changeSetUserView);
		}
		return true;
	}

	@Override
	public Collection<ChangeSetUserView> getAssigned() {
		Collection<String> changeSetUserGuids = this.getModel().getAssignedUserGuids();
					
		final ChangeSetService changeSetService = ServiceLocator.getService(ContextIdNames.CHANGESET_SERVICE);
		
		changeSetUserViews = changeSetService.getChangeSetUserViews(changeSetUserGuids);
		return changeSetUserViews;
	}

	@Override
	public Collection<ChangeSetUserView> getAvailable() {
		ChangeSetService service = ServiceLocator.getService(ContextIdNames.CHANGESET_SERVICE);
		return service.getAvailableUsers(ChangeSetPermissions.CHANGE_SET_PERMISSIONS_MANAGE, 
				ChangeSetPermissions.WORK_WITH_CHANGE_SETS_PERMISSION);		
	}

	@Override
	public ViewerFilter getAvailableFilter() {
		return new UserAvailableViewerFilter();				
	}	
	
	@Override
	public ViewerFilter getAssignedFilter() {
		return new UserAssignedViewerFilter();
	}

	/**
	 * Filters the AvailableListView so that it doesn't display any objects that are in the assigned list. Subclasses should override the Select
	 * method if they want to do any filtering.
	 */
	protected class UserAvailableViewerFilter extends ViewerFilter {
		
		private final CmUserService cmUserService = ServiceLocator.getService(ContextIdNames.CMUSER_SERVICE);

		@Override
		public boolean select(final Viewer viewer, final Object parentElement, final Object element) {							
					
			CmUser createdByUser = cmUserService.findByGuid(getModel().getCreatedByUserGuid());
			
			if (element instanceof ChangeSetUserView) {
				
				ChangeSetUserView changeSetUserView = (ChangeSetUserView) element;
				
				if (createdByUser != null && createdByUser.getGuid().equals(changeSetUserView.getGuid())) {
					return false;									
				}
				
				// Compare current user to assigned users
				Collection<ChangeSetUserView> assignedUsers = UserAssignmentDualListBox.this.changeSetUserViews;
				
				for (final ChangeSetUserView currChangeSetUserView : assignedUsers) {
					if (changeSetUserView.getGuid().equals(currChangeSetUserView.getGuid())) {
						return false;
					}									
				}									
			}
			
			return true;
		}	
	}
	
	/**
	 * Filters the AssignedListView.
	 */
	protected class UserAssignedViewerFilter extends ViewerFilter {
		
		private final CmUserService cmUserService = ServiceLocator.getService(ContextIdNames.CMUSER_SERVICE);

		@Override
		public boolean select(final Viewer viewer, final Object parentElement, final Object element) {							
					
			CmUser createdByUser = cmUserService.findByGuid(getModel().getCreatedByUserGuid());
			
			if (createdByUser != null
				&& element instanceof ChangeSetUserView) {
				
				ChangeSetUserView user = (ChangeSetUserView) element;
				if (user.getGuid().equals(createdByUser.getGuid())) {
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
			
			// Available users
			if (element instanceof ChangeSetUserView) {
				ChangeSetUserView user = ((ChangeSetUserView) element);
				return UserViewFormatter.formatWithNameAndUserName(user);				
			}
		
			return ChangeSetMessages.EMPTY_STRING;
		}		
	}		

	@Override
	protected ILabelProvider getLabelProvider() {
		return new UserRoleLabelProvider();
	}	
}

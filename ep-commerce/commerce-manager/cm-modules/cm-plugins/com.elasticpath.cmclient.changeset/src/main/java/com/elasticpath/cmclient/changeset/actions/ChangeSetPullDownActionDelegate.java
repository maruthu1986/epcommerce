/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.changeset.actions;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.eclipse.ui.IWorkbenchWindow;

import com.elasticpath.cmclient.changeset.ChangeSetPermissions;
import com.elasticpath.cmclient.changeset.ChangeSetPlugin;
import com.elasticpath.cmclient.changeset.event.ChangeSetEventListener;
import com.elasticpath.cmclient.changeset.event.ChangeSetEventService;
import com.elasticpath.cmclient.changeset.helpers.impl.ChangeSetPermissionsHelperImpl;
import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.actions.AbstractDynamicPullDownActionDelegate;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.ItemChangeEvent.EventType;
import com.elasticpath.cmclient.core.eventlistener.CoolbarEventManager;
import com.elasticpath.cmclient.core.registry.ObjectRegistry;
import com.elasticpath.cmclient.core.registry.ObjectRegistryListener;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.changeset.ChangeSetStateCode;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.service.changeset.ChangeSetLoadTuner;
import com.elasticpath.service.changeset.ChangeSetManagementService;
import com.elasticpath.service.changeset.ChangeSetSearchCriteria;
import com.elasticpath.service.search.query.SortOrder;
import com.elasticpath.service.search.query.StandardSortBy;

/**
 * A delegate for representing change set pull down menu.
 */
public class ChangeSetPullDownActionDelegate extends AbstractDynamicPullDownActionDelegate<ChangeSetSwitchAction, ChangeSet>
		implements ChangeSetEventListener {

	private static final int MAX_DISPLAY_LENGTH = 15;
	private final ChangeSetManagementService changeSetManagementService
			= ServiceLocator.getService(ContextIdNames.CHANGESET_MANAGEMENT_SERVICE);
	private IWorkbenchWindow workbenchWindow;

	@Override
	protected ChangeSet getActiveMenuObject() {
		return ChangeSetPlugin.getDefault().getActiveChangeSet();
	}

	@Override
	protected Collection<ChangeSet> getAvailableMenuObjects() {
		// get currently logged in CM user
		CmUser cmUser = LoginManager.getCmUser();
		
		Collection<ChangeSet> changeSets;
		ChangeSetSearchCriteria searchCriteria = new ChangeSetSearchCriteria();
		searchCriteria.setChangeSetStateCode(ChangeSetStateCode.OPEN);
		searchCriteria.setSortingType(StandardSortBy.NAME);
		searchCriteria.setSortingOrder(SortOrder.ASCENDING);
		ChangeSetLoadTuner noMembersLoadTuner = ServiceLocator.getService(ContextIdNames.CHANGESET_LOAD_TUNER);
		noMembersLoadTuner.setLoadingMemberObjects(false);
		
		if (cmUser.isSuperUser()) {
			// super users can view all change sets
			changeSets = changeSetManagementService.findByCriteria(searchCriteria, noMembersLoadTuner);
			
		} else {
			// only get the change sets this user is associated with
			String userGuid = cmUser.getGuid();		
			searchCriteria.setUserGuid(userGuid);
			changeSets = changeSetManagementService.findByCriteria(searchCriteria, noMembersLoadTuner);
		}
		
		return changeSets;
	}

	@Override
	protected void preInitialize(final IWorkbenchWindow workbenchWindow) {
		this.workbenchWindow = workbenchWindow;
		ChangeSetEventService.getInstance().registerChangeSetEventListener(this);
		ObjectRegistry.getInstance().addObjectListener(objectRegistryListener);
	}

	@Override
	protected ChangeSetSwitchAction createPullDownAction(final ChangeSet menuObject) {
		return new ChangeSetSwitchAction(menuObject, workbenchWindow);
	}

	@Override
	public void dispose() {
		ObjectRegistry.getInstance().removeObjectListener(objectRegistryListener);
		ChangeSetEventService.getInstance().unregisterChangeSetEventListener(this);
		super.dispose();
	}

	@Override
	public void refresh() {
		super.refresh();
		ChangeSet activeChangeSet = ChangeSetPlugin.getDefault().getActiveChangeSet();
		if (activeChangeSet == null) {
			String actionText = "Select Change Set";  //$NON-NLS-1$
			getAction().setText(actionText); 
			getAction().setToolTipText(actionText);
		} else {
			String actionText = StringUtils.abbreviate(activeChangeSet.getName(), MAX_DISPLAY_LENGTH);
			getAction().setText(actionText);
			getAction().setToolTipText(activeChangeSet.getName());
		}
		CoolbarEventManager.getInstance().updateRequest();
	}

	@Override
	protected boolean isEnabled() {
		return ChangeSetPermissionsHelperImpl.getDefault().isChangeSetFeatureEnabled() && isAuthorized();
	}
	
	/**
	 * can this action be executed by the current user.
	 * 
	 * @return true if the current user is allowed to execute it
	 */
	protected boolean isAuthorized() {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(ChangeSetPermissions.CHANGE_SET_PERMISSIONS_MANAGE)
			|| AuthorizationService.getInstance().isAuthorizedWithPermission(ChangeSetPermissions.WORK_WITH_CHANGE_SETS_PERMISSION);
	}

	@Override
	public void changeSetModified(final ItemChangeEvent<ChangeSet> event) {
		if (event.getEventType() == EventType.CHANGE
			&& event.getItem().getStateCode() != ChangeSetStateCode.OPEN
			&& isModifiedChangesetTheActiveChangeSet(event.getItem())) {
			ChangeSetPlugin.getDefault().setActiveChangeSet(null);
		}
		this.refresh();
	}

	private boolean isModifiedChangesetTheActiveChangeSet(final ChangeSet changeSetModified) {
		final ChangeSet activeChangeSet = ChangeSetPlugin.getDefault().getActiveChangeSet();
		return activeChangeSet != null  && activeChangeSet.equals(changeSetModified);
	}

	private final ObjectRegistryListener objectRegistryListener = new ObjectRegistryListener() {
		public void objectUpdated(final String key, final Object oldValue, final Object newValue) {
			if (newValue instanceof ChangeSet) {
				refresh();
			}
		}
		public void objectRemoved(final String key, final Object object) {
			if (object instanceof ChangeSet) {
				refresh();
			}
		}
		public void objectAdded(final String key, final Object object) {
			if (object instanceof ChangeSet) {
				refresh();
			}
		}
	};

}

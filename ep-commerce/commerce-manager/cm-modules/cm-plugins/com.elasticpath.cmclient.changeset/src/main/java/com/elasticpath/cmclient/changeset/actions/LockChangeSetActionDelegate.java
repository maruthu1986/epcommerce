/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.changeset.actions;

import com.elasticpath.cmclient.changeset.ChangeSetPermissions;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.changeset.ChangeSetStateCode;

/**
 * An action delegate to lock a change set.
 */
public class LockChangeSetActionDelegate extends AbstractChangeStateActionDelegate {

	@Override
	protected boolean isStateEnabler(final ChangeSetStateCode stateCode) {
		return stateCode == ChangeSetStateCode.OPEN;
	}

	@Override
	protected ChangeSetStateCode getNewState() {
		return ChangeSetStateCode.LOCKED;
	}

	@Override
	protected boolean isAuthorized() {
		return isAuthorized(ChangeSetPermissions.CHANGE_SET_PERMISSIONS_MANAGE)
			|| isAuthorized(ChangeSetPermissions.WORK_WITH_CHANGE_SETS_PERMISSION);
	}
	
	@Override
	protected boolean checkEditors(final ChangeSet changeSet) {
		return getChangeSetActionUtil().saveAndReloadEditors(changeSet, getView().getSite().getWorkbenchWindow());
	}
}

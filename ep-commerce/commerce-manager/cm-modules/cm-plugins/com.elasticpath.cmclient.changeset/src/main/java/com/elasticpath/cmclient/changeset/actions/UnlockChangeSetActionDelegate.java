/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.changeset.actions;

import com.elasticpath.cmclient.changeset.ChangeSetPermissions;
import com.elasticpath.domain.changeset.ChangeSetStateCode;

/**
 * An action delegate to be used to unlock a change set.
 */
public class UnlockChangeSetActionDelegate extends AbstractChangeStateActionDelegate {

	@Override
	protected ChangeSetStateCode getNewState() {
		return ChangeSetStateCode.OPEN;
	}

	@Override
	protected boolean isStateEnabler(final ChangeSetStateCode stateCode) {
		return stateCode == ChangeSetStateCode.LOCKED;
	}

	@Override
	protected boolean isAuthorized() {
		return isAuthorized(ChangeSetPermissions.CHANGE_SET_PERMISSIONS_MANAGE)
			|| isAuthorized(ChangeSetPermissions.WORK_WITH_CHANGE_SETS_PERMISSION);
	}

}

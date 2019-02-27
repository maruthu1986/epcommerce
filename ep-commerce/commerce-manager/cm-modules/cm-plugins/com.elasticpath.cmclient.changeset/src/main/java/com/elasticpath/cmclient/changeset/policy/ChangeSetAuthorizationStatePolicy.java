/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.changeset.policy;

import com.elasticpath.cmclient.changeset.ChangeSetPermissions;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;

/**
 * A state policy to determine the editable state depending on the 
 * permissions the current user has been assigned to.
 */
public class ChangeSetAuthorizationStatePolicy extends AbstractChangeSetEditorStatePolicy {

	private AuthorizationService authorizationService;

	@Override
	public EpState determineContainerState(final PolicyActionContainer targetContainer) {
		if (!authorizationService.isAuthorizedWithPermission(ChangeSetPermissions.CHANGE_SET_PERMISSIONS_MANAGE)
				&& !authorizationService.isAuthorizedWithPermission(ChangeSetPermissions.WORK_WITH_CHANGE_SETS_PERMISSION)) {
			return EpState.READ_ONLY;
		}
		return EpState.EDITABLE;
	}

	@Override
	public void init(final Object dependentObject) {
		super.init(dependentObject);
		this.authorizationService = AuthorizationService.getInstance();
	}

}

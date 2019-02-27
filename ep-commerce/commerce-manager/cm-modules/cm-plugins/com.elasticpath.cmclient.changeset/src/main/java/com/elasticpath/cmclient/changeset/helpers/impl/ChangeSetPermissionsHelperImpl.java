/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.changeset.helpers.impl;

import com.elasticpath.cmclient.changeset.ChangeSetPermissions;
import com.elasticpath.cmclient.changeset.helpers.ChangeSetPermissionsHelper;
import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.core.service.AuthorizationService;

/**
 * Implementation of {@code ChangeSetPermissionsHelper}.
 */
public class ChangeSetPermissionsHelperImpl implements ChangeSetPermissionsHelper {

	/**
	 * Delegator that leaves in core. We ask it for ChangeSet being Enabled.
	 */
	private final ChangeSetHelper changeSetHelper = ServiceLocator.getService(ChangeSetHelper.BEAN_ID);

	/**
	 * Getter for change set permission helper.
	 *
	 * @return ChangeSetPermissionsHelper
	 */
	public static ChangeSetPermissionsHelper getDefault() {
		return CmSingletonUtil.getApplicationInstance(ChangeSetPermissionsHelperImpl.class);
	}

	/**
	 * Returns true if the change set feature is enabled.
	 * @return The result
	 */
	@Override
	public boolean isChangeSetFeatureEnabled() {
		return changeSetHelper.isChangeSetsEnabled();
	}
	
	/**
	 * @return True if the user has one of the change set permissions.
	 */
	@Override
	public boolean userHasChangeSetPermission() {
		AuthorizationService authorizationService = AuthorizationService.getInstance();
		return authorizationService.isAuthorizedWithPermission(ChangeSetPermissions.CHANGE_SET_PERMISSIONS_MANAGE)
				|| authorizationService.isAuthorizedWithPermission(ChangeSetPermissions.WORK_WITH_CHANGE_SETS_PERMISSION);
	}
}

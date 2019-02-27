/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.changeset.perspective;

import com.elasticpath.cmclient.changeset.helpers.impl.ChangeSetPermissionsHelperImpl;
import com.elasticpath.cmclient.core.helpers.AbstractOpenPerspectiveHandler;

/**
 * Opens Change Sets perspective on user action.
 */
public class OpenChangeSetPerspectiveHandler extends AbstractOpenPerspectiveHandler {

	@Override
	protected String getPerspectiveId() {
		return ChangeSetPerspectiveFactory.PERSPECTIVE_ID;
	}

	@Override
	public boolean isEnabled() {
		return ChangeSetPermissionsHelperImpl.getDefault().isChangeSetFeatureEnabled() && super.isEnabled();
	}

}

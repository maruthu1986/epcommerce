/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.changeset.helpers;

import com.elasticpath.cmclient.changeset.helpers.EditorEventObserver.IEditorCondition;
import com.elasticpath.cmclient.changeset.helpers.impl.ChangeSetPermissionsHelperImpl;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;

/**
 * This condition verifies the status of the flag for enabling/disabling the change sets.
 */
public class ChangeSetsEnabledCondition implements IEditorCondition {

	/**
	 * Checks whether change sets are enabled.
	 * 
	 * @param editor the editor
	 * @return true if they are enabled
	 */
	@Override
	public boolean isConditionFulfilled(final AbstractCmClientFormEditor editor) {
		return ChangeSetPermissionsHelperImpl.getDefault().isChangeSetFeatureEnabled();
	}

}

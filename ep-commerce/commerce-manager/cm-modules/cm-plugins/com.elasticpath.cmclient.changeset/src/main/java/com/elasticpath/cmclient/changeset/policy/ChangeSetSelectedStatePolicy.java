/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.changeset.policy;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;

/**
 * A state policy to determine the editable state depending on
 * whether one change set is selected.
 */
public class ChangeSetSelectedStatePolicy extends AbstractChangeSetEditorStatePolicy {
	
	@Override
	public EpState determineContainerState(final PolicyActionContainer targetContainer) {
		
		if (getChangeSetHelper().isChangeSetsEnabled() && getChangeSetHelper().isActiveChangeSet()) {
			return EpState.EDITABLE;
		} 
		
		return EpState.READ_ONLY;		
	}
}

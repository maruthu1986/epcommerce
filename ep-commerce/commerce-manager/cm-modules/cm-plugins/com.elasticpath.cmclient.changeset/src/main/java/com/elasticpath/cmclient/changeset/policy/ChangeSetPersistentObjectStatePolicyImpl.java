/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.cmclient.changeset.policy;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.persistence.api.Persistable;

/**
 * Changeset policy for the new Persistable object.
 * If !Persistable.isPersisted() && changeset activated then return EpState.EDITABLE.
 *
 */
public class ChangeSetPersistentObjectStatePolicyImpl extends ChangesetMemberStatePolicyImpl {

	/* (non-Javadoc)
	 * @see com.elasticpath.cmclient.changeset.policy.ChangeSetMemberDependentObjectStatePolicyImpl#
	 * determineState(com.elasticpath.cmclient.policy.common.PolicyActionContainer)
	 */
	@Override
	public EpState determineContainerState(final PolicyActionContainer targetContainer) {
		
		Object dependentObject = this.getDependentObject();
		if (dependentObject instanceof Persistable) {
			Persistable testObject = (Persistable) dependentObject;
			if (!testObject.isPersisted()) {
					return EpState.EDITABLE;
			}
		}

		return super.determineContainerState(targetContainer);
	}

}

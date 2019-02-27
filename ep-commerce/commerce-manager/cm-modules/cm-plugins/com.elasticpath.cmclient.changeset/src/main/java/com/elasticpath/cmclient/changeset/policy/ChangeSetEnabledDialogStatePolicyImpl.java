/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.changeset.policy;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;

/**
 * UI control state policy for dialogs which checks whether an object is a member of a changeset.
 * Must be initialized with a Boolean editMode indicating the state of the dialog.
 * Add mode is false, edit mode is true. 
 */
public class ChangeSetEnabledDialogStatePolicyImpl extends ChangeSetMemberContainerDependentObjectStatePolicyImpl {

	private Boolean editMode;
	
	@Override
	public void init(final Object dependentObject) {
		if ((dependentObject != null) && (dependentObject instanceof Boolean)) {
			editMode = (Boolean) dependentObject;
		}
	}
	
	/**
	 * The dependent object provided during initialization is the editMode.
	 * If editMode is false, a new object is being created and this should return EDITABLE.
	 * 
	 * Otherwise, return EDITABLE as state for all targets if the target container's dependent object is in the
	 * currently selected change set, otherwise return READ_ONLY. 
	 * 
	 * @param targetContainer the target container containing the dependent object to check against the current change set
	 * @return an <code>EpState</code> determined by the policy.
	 */
	@Override
	public EpState determineState(final PolicyActionContainer targetContainer) {
		
		if (!editMode) {
			return EpState.EDITABLE;
		}
		
		Object dependentObject = targetContainer.getPolicyDependent();
		
		if (dependentObject == null) {
			return EpState.EDITABLE;
		}

		if (!isMemberOfCurrentChangeSet(dependentObject)) {
			return EpState.READ_ONLY;
		}
		
		if (currentChangeSetChangeable()) {
			return EpState.EDITABLE;
		}

		return EpState.READ_ONLY;
	}
	
}

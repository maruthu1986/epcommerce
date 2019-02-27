/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.changeset.policy;

import java.util.Collection;
import java.util.Collections;

import com.elasticpath.cmclient.changeset.ChangeSetPlugin;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.common.AbstractStatePolicyImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.changeset.ChangeSetObjectStatus;
import com.elasticpath.service.changeset.ChangeSetManagementService;
import com.elasticpath.service.changeset.ChangeSetService;
import com.elasticpath.cmclient.core.ServiceLocator;
/**
 * An abstract UI control state policy which checks whether an object is a member of a changeset.
 */
public class ChangeSetMemberContainerDependentObjectStatePolicyImpl extends AbstractStatePolicyImpl {

	private ChangeSetService changesetService;
	
	private ChangeSetManagementService changesetManagementService;
	
	/**
	 * Return EDITABLE as state for all targets if the target container's dependent object is in the
	 * currently selected change set, otherwise return READ_ONLY. The dependent object provided during initialization
	 * is not used.
	 * 
	 * @param targetContainer the target container containing the dependent object to check against the current change set
	 * @return an <code>EpState</code> determined by the policy.
	 */
	public EpState determineState(final PolicyActionContainer targetContainer) {
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
	
	/**
	 * @return true if the current change set is modifiable, false otherwise.
	 */
	protected boolean currentChangeSetChangeable() {
		return getChangeSetManagementService().isChangeAllowed(getChangeSetGuid());
	}
	
	/**
	 * @param dependentObject the dependent object to check.
	 * @return true if the object is in the current change set, false otherwise.
	 */
	protected boolean isMemberOfCurrentChangeSet(final Object dependentObject) {
		ChangeSetObjectStatus status = getChangeSetService().getStatus(dependentObject);
		return status.isMember(getChangeSetGuid());
	}	
	
	/**
	 * Get the guid of the changeset that should be checked.
	 * 
	 * @return the <code>ChangeSet</code> GUID.
	 */
	protected String getChangeSetGuid() {
		ChangeSet changeSet = ChangeSetPlugin.getDefault().getActiveChangeSet();
		if (changeSet == null) {
			return null;
		}
		return changeSet.getGuid();
	}

	/**
	 * Get the change set service.
	 * 
	 * @return the change set service.
	 */
	protected ChangeSetService getChangeSetService() {
		if (changesetService == null) {
			changesetService = ServiceLocator.getService(ContextIdNames.CHANGESET_SERVICE);
		}
		return changesetService;
	}
	
	/**
	 * Get the change set management service.
	 * 
	 * @return the change set management service.
	 */
	protected ChangeSetManagementService getChangeSetManagementService() {
		if (changesetManagementService == null) {
			changesetManagementService = ServiceLocator.getService(ContextIdNames.CHANGESET_MANAGEMENT_SERVICE);
		}
		return changesetManagementService;
	}


	/**
	 * This policy is generic enough to not care about delegation, so
	 * we return an empty list.
	 * 
	 * @return an empty collection
	 */
	public Collection<String> getDelegatingContainerNames() {
		return Collections.emptyList();
	}

}

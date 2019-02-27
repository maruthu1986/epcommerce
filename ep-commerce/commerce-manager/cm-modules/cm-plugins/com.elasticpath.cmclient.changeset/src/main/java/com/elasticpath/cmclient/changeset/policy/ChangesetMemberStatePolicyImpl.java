/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.changeset.policy;

import java.util.Collection;
import java.util.Collections;

import com.elasticpath.cmclient.changeset.ChangeSetPlugin;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
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
public class ChangesetMemberStatePolicyImpl extends AbstractChangeSetExcludeDeterminationStatePolicy {

	private ChangeSetService changesetService;
	
	private ChangeSetManagementService changesetManagementService;
	
	@Override
	public EpState determineContainerState(final PolicyActionContainer targetContainer) {
		if (getDependentObject() == null) {
			return EpState.READ_ONLY;
		}
		
		if (!isMemberOfCurrentChangeSet(getDependentObject())) {
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
	 * @param object the dependent object to check.
	 * @return true if the object is in the current change set, false otherwise.
	 */
	protected boolean isMemberOfCurrentChangeSet(final Object object) {
		ChangeSetObjectStatus status = getChangeSetService().getStatus(getDependentObject());
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
			changesetService =  ServiceLocator.getService(ContextIdNames.CHANGESET_SERVICE);
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
			changesetManagementService =  ServiceLocator.getService(ContextIdNames.CHANGESET_MANAGEMENT_SERVICE);
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

	@Override
	protected Collection<String> getEditableContainerNames() {
		return Collections.emptyList();
	}

	@Override
	protected Collection<String> getReadOnlyContainerNames() {
		return Collections.emptyList();
	}

}

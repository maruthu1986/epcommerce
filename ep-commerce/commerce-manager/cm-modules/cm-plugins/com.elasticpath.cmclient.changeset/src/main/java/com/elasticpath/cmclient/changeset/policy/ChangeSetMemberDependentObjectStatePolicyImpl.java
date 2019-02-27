/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.changeset.policy;

import java.util.Arrays;
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
 * A state policy which checks whether an object is a member of a Change Set. See determine state method for details. 
 */
public class ChangeSetMemberDependentObjectStatePolicyImpl extends AbstractStatePolicyImpl {

	private ChangeSetService changesetService;
	
	private Object dependentObject;

	private ChangeSetManagementService changesetManagementService;
	
	/**
	 * Construct an instance of this policy that ties to the business object 
	 * and required change set guid.
	 * 
	 * @param dependentObject an object that will be translated to a <code>BusinessObjectDescriptor</code>
	 */
	public void init(final Object dependentObject) {
		this.dependentObject = dependentObject;
	}
	
	/**
	 * Returns {@link EpState#EDITABLE} for all targets if the business descriptor is in the required change set,
	 * unless the {@link PolicyActionContainer targetContainer} has a dependent object and is apart of a different
	 * change set; this will return {@link EpState#READ_ONLY}.
	 * 
	 * @param targetContainer the {@link PolicyActionContainer}
	 * @return an {@link EpState} determined by the policy
	 */
	public EpState determineState(final PolicyActionContainer targetContainer) {
		if (getEditableContainerNames().contains(targetContainer.getName())) {
			return EpState.EDITABLE;
		}

		if (dependentObject != null && !isMemberOfCurrentChangeSet(dependentObject)) {
			return EpState.READ_ONLY;
		}

		Object policyDependent = targetContainer.getPolicyDependent();
		if (policyDependent != null) {
			ChangeSetObjectStatus dependentStatus = getChangeSetService().getStatus(policyDependent);
			// if locked but not a member of this changeset, then it must be associated to another
			if (dependentStatus.isLocked() && !dependentStatus.isMember(getChangeSetGuid())) {
				return EpState.READ_ONLY;
			}
		}

		return EpState.EDITABLE;
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
	 * Get the collection of names of <code>PolicyTargetContainer</code> objects
	 * that should should always be in edit mode.
	 * 
	 * @return the collection of names
	 */
	protected Collection<String> getEditableContainerNames() {
		return Arrays.asList(
				"navigationControls", //$NON-NLS-1$
				"openSkuControls", //$NON-NLS-1$
				"editablePriceControls" //$NON-NLS-1$
				);
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

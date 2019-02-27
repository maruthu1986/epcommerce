/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.changeset.policy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.elasticpath.cmclient.changeset.ChangeSetPlugin;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.common.AbstractStatePolicyImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.service.changeset.ChangeSetManagementService;
import com.elasticpath.service.changeset.ChangeSetService;
import com.elasticpath.cmclient.core.ServiceLocator;
/**
 * A state policy which checks whether an object is a member of a Change Set. See determine state method for details. 
 */
public class ChangeSetPriceAdjustmentStatePolicyImpl extends AbstractStatePolicyImpl {

	private ChangeSetService changesetService;
	
	private Object dependentObject;

	private ChangeSetManagementService changesetManagementService;
	
	private final Comparator<EpState> stateComparator = new Comparator<EpState>() {

		/**
		 * Compare two states in regards to restrictivity. 
		 * So EDITABLE < READ_ONLY < DISABLED
		 * 
		 * @param state1 the first state to compare
		 * @param state2 the second state to compare
		 * @return 0 if same, <0 if less restrictive, >0 if more restrictive.
		 */
		@Override
		public int compare(final EpState state1, final EpState state2) {
			return stateValue(state1) - stateValue(state2);
		}
		
		private int stateValue(final EpState state) {
			List<EpState> orderedStates = new ArrayList<EpState>();
			orderedStates.add(EpState.EDITABLE);
			orderedStates.add(EpState.READ_ONLY);
			orderedStates.add(EpState.DISABLED);
			return orderedStates.indexOf(state);
		}
		
	};	
	
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
	 * Return EDITABLE as state for all targets if the business object descriptor is in the
	 * required change set, otherwise return READ_ONLY. In addition to this, the container's dependent object
	 * is also checked. If the dependent object is not associated with any Change Set, the returned state is EDITABLE.
	 * If the dependent object is associated with any Change Set, the Change Set must be the active Change Set.
	 * 
	 * @param targetContainer the target container
	 * @return an <code>EpState</code> determined by the policy.
	 */
	public EpState determineState(final PolicyActionContainer targetContainer) {
		EpState mostRestrictiveState = EpState.EDITABLE;
		if (getEditableContainerNames().contains(targetContainer.getName())) {
			mostRestrictiveState = EpState.EDITABLE;
		} else if (dependentObject instanceof Collection< ? >) {
			for (Object dependent : (Collection< ? >) dependentObject) {
				if (dependent != null) {
					EpState thisState = getChangeSetState(dependent); 
					mostRestrictiveState = getMostRestrictiveState(mostRestrictiveState, thisState);
				}
			}
			return mostRestrictiveState;
		} else if (dependentObject != null) {
			EpState thisState = getChangeSetState(dependentObject);
			if (thisState != null) {
				mostRestrictiveState = thisState;
			}
		}
	
		return mostRestrictiveState;		
	}

	private EpState getMostRestrictiveState(final EpState currentMostRestrictiveState, final EpState newState) {
		if (newState != null && stateComparator.compare(newState, currentMostRestrictiveState) > 0) {
			return newState;
		}
		return currentMostRestrictiveState;
	}

	/**
	 * Get state based on change set.
	 *
	 * @param dependent dependent object
	 * @return the epstate, Editable by default
	 */
	protected EpState getChangeSetState(final Object dependent) {
		ChangeSet changeSet = getChangeSetService().findChangeSet(dependent);
		if (changeSet == null && getChangeSetService().isChangeSetEnabled()) {
			return EpState.READ_ONLY;
		} else if (changeSet != null) {
			if (!getChangeSetManagementService().isChangeAllowed(changeSet.getGuid())) {
				return EpState.READ_ONLY;
			} 
			if (!getChangeSetService().getStatus(dependent).isMember(getChangeSetGuid())) {
				return EpState.READ_ONLY;
			}
		} 
		return EpState.EDITABLE;
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

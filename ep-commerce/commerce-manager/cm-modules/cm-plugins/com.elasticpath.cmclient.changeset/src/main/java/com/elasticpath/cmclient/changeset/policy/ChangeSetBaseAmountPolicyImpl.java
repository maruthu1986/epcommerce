/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.changeset.policy;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StateDeterminer;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.domain.changeset.ChangeSetObjectStatus;


/**
 * Change set policies for Base Amount.
 */
public class ChangeSetBaseAmountPolicyImpl extends AbstractChangeSetDeterminerStatePolicy {
	
	private final Map<String, StateDeterminer> determinerMap = new HashMap<String, StateDeterminer>();
	
	/**
	 * Map container names to determiners.
	 * 
	 * @return a map of container name to <code>StateDeterminer</code>
	 */
	protected Map<String, StateDeterminer> getDeterminerMap() {
		if (determinerMap.isEmpty()) {
			determinerMap.put("removeBaseAmountButtonContainer", new BaseAmountRemovingDeterminer()); //$NON-NLS-1$
		}
		return determinerMap;
	}
	
	/**
	 * State determiner for removing base amount.
	 */
	public static class BaseAmountRemovingDeterminer implements StateDeterminer {

		private final ChangeSetHelper changeSetHelper = ServiceLocator.getService(ChangeSetHelper.BEAN_ID);

		/**
		 * Determine the removing state based on the base amount selection.
		 * The base amount could be removed if the base amount is in the current change set
		 * and it is not in any other change set
		 * 
		 * @param targetContainer the target container
		 * @return the <code>EpState</code> determined
		 */
		public EpState determineState(final PolicyActionContainer targetContainer) {
			Object dependentObject = targetContainer.getPolicyDependent();
			
			if (!(dependentObject instanceof BaseAmountDTO)) {
				// base amount must be selected
				return EpState.READ_ONLY;
			}

			if (!changeSetHelper.isActiveChangeSet()) {
				return EpState.READ_ONLY;
			}

			ChangeSetObjectStatus status = changeSetHelper.getChangeSetObjectStatus(dependentObject);
			
			if (status.isAvailable(changeSetHelper.getActiveChangeSet().getGuid())
				|| status.isMember(changeSetHelper.getActiveChangeSet().getGuid())) {			
				return EpState.EDITABLE;
			}
			
			return EpState.READ_ONLY;
		}
	}

	@Override
	protected StateDeterminer getDefaultDeterminer() {
		return new DefaultEditableDeterminer();
	}
}

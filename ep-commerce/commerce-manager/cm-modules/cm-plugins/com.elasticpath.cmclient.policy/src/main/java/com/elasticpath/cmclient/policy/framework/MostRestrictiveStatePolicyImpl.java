/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.policy.framework;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.common.AbstractStatePolicyImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;

/**
 * A state policy implementation that combines other state policies, where
 * the resultant state is the most restrictive of any of the combined state
 * policies.
 * 
 */
public class MostRestrictiveStatePolicyImpl extends AbstractStatePolicyImpl {

	private final Collection<StatePolicy> policies;
	
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
			List<EpState> orderedStates = new ArrayList<>();
			orderedStates.add(EpState.EDITABLE);
			orderedStates.add(EpState.READ_ONLY);
			orderedStates.add(EpState.DISABLED);
			return orderedStates.indexOf(state);
		}
		
	};
	
	/**
	 * Create a new instance of this policy given the collection of policies
	 * to combine.
	 * 
	 * @param policies a collection of <code>StatePolicy</code> to combine.
	 */
	public MostRestrictiveStatePolicyImpl(final Collection<StatePolicy> policies) {
		super();
		this.policies = policies;
	}

	/**
	 * Get the most restrictive state of all the policies.
	 * 
	 * @param targetContainer the policy context
	 * @return an <code>EpState</code> determined by the policy.
	 */
	@Override
	public EpState determineState(final PolicyActionContainer targetContainer) {
		EpState mostRestrictiveState = EpState.EDITABLE;
		for (StatePolicy policy : policies) {
			EpState thisState = policy.determineState(targetContainer);
			if (stateComparator.compare(thisState, mostRestrictiveState) > 0) {
				mostRestrictiveState = thisState;
			}
		}
		return mostRestrictiveState;
	}

	/**
	 * Initialize this policy, passing in any dependencies. This in turn
	 * calls init with the same dependency for all of the policies being combined.
	 * 
	 * @param dependentObject an object the policy may depend on for further information.
	 */
	@Override
	public void init(final Object dependentObject) {
		for (StatePolicy policy : policies) {
			policy.init(dependentObject);
		}
	}

}

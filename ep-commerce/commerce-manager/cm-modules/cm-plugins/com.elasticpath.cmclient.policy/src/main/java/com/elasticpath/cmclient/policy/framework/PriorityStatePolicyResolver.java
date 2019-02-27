/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.policy.framework;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.StatePolicyResolver;
import com.elasticpath.cmclient.policy.common.StatePolicyContribution;

/**
 * Resolve a state policy based on priority. The highest priority wins, and
 * in the case of equal priorities, the policies will be combined with a 
 * logical AND to determine editability. 
 */
public class PriorityStatePolicyResolver implements StatePolicyResolver {

	private final Comparator<StatePolicyContribution> priorityComparator = new Comparator<StatePolicyContribution>() {
		/**
		 * Compares two state policy contributions by priority.
		 * @param spc1 the first <code>StatePolicyContribution</code>
		 * @param spc2 the second <code>StatePolicyContribution</code>
		 * @return a negative integer, zero, or a positive integer as the first argument's priority
		 * 		   is less than, equal to, or greater than the second's. 
		 */
		@Override
		public int compare(final StatePolicyContribution spc1, final StatePolicyContribution spc2) {
			return spc2.getPriority() - spc1.getPriority();
		}
	};
	
	/**
	 * Resolve the given policies into a single policy.
	 *
	 * @param contributions a collection of state policy contributions
	 * @return a resolved <code>StatePolicy</code>
	 */
	@Override
	public StatePolicy resolvePolicy(final Collection<StatePolicyContribution> contributions) {
		List<StatePolicy> policies = getWinningPolicies(contributions);
		StatePolicy policy;
		if (policies.size() == 1) {
			policy = policies.get(0);
		} else {
			policy = new MostRestrictiveStatePolicyImpl(policies);
		}
		return policy;
	}

	/**
	 * Sort the collection of policies by priority, highest to lowest.
	 * 
	 * @param policies the collection of policy contributions
	 * @return a sorted <code>List</code> of policy contributions.
	 */
	protected List<StatePolicyContribution> sortPoliciesByPriority(final Collection<StatePolicyContribution> policies) {

		List<StatePolicyContribution> policyList = new ArrayList<>(policies);
		Collections.sort(policyList, priorityComparator);
		return policyList;
	}

	/**
	 * Get the list of winning policies.
	 * 
	 * @param contributions the collection of policy contributions
	 * @return a <code>List</code> of <code>StatePolicy</code> objects
	 */
	protected List<StatePolicy> getWinningPolicies(final Collection<StatePolicyContribution> contributions) {
		if (contributions.isEmpty()) {
			return Collections.emptyList();
		}
		List<StatePolicy> policies = new ArrayList<>();
		List<StatePolicyContribution> sortedList = sortPoliciesByPriority(contributions);
		int topPriority = sortedList.get(0).getPriority();
		for (StatePolicyContribution contribution : sortedList) {
			if (contribution.getPriority() != topPriority) {
				break;
			}
			policies.add(contribution.getStatePolicy());
		}
		return policies;
	}
	
}

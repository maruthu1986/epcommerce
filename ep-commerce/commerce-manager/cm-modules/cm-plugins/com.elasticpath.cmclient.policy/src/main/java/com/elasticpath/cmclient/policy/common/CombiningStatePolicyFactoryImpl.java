/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.policy.common;

import java.util.Collection;
import java.util.HashSet;

import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.StatePolicyFactory;
import com.elasticpath.cmclient.policy.StatePolicyResolver;

/**
 * A <code>StatePolicy</code> factory that uses a resolver class to combine a
 * collection of contributed policies.
 */
public class CombiningStatePolicyFactoryImpl implements StatePolicyFactory {

	private final Collection<StatePolicyContribution> contributions;
	
	private final StatePolicyResolver resolver;
	
	/**
	 * Create an instance of a state policy factory that combines state policies.
	 * 
	 * @param contributions the collection of policy contributions
	 * @param resolver a policy resolver
	 */
	public CombiningStatePolicyFactoryImpl(final Collection<StatePolicyContribution> contributions, final StatePolicyResolver resolver) {
		super();
		this.contributions = contributions;
		this.resolver = resolver;
	}

	@Override
	public StatePolicy getStatePolicy(final String targetId) {
		Collection<StatePolicyContribution> targetPolicies = getContributionsForTarget(targetId);
		if (targetPolicies.isEmpty()) {
			return null;
		}
		return resolver.resolvePolicy(targetPolicies);
	}

	/**
	 * Get the collection of contributions for the given target.
	 * 
	 * @param targetId the ID of the target 
	 * @return the collection of policy contributions for the given target.
	 */
	protected Collection<StatePolicyContribution> getContributionsForTarget(final String targetId) {
		Collection<StatePolicyContribution> targetContributions = new HashSet<>();
		for (StatePolicyContribution contribution : contributions) {
			if (targetId.equals(contribution.getTargetId())) {
				targetContributions.add(contribution);
			}
		}
		return targetContributions;
	}

}

/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.policy;

import java.util.Collection;

import com.elasticpath.cmclient.policy.common.StatePolicyContribution;


/**
 * Methods to resolve a state policy from multiple contributed policies. 
 */
public interface StatePolicyResolver {

	/**
	 * Resolve the given policies into a single policy.
	 * 
	 * @param policies a collection of state policy contributions 
	 * @return a resolved <code>StatePolicy</code>
	 */
	StatePolicy resolvePolicy(Collection<StatePolicyContribution> policies); 
}

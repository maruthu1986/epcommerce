/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.policy;

import java.util.Map;

import com.elasticpath.cmclient.policy.common.PolicyActionContainer;

/**
 * An object that can be governed by a state policy.
 */
public interface StatePolicyGovernable {

	/**
	 * Apply the state policy.
	 * 
	 * @param statePolicy a <code>StatePolicy</code>
	 */
	void applyStatePolicy(StatePolicy statePolicy);
	
	/**
	 * Add a policy target container with the given name.
	 * 
	 * @param name the name of the container
	 * @return a <code>PolicyTargetContainer</code> object.
	 */
	PolicyActionContainer addPolicyActionContainer(String name);

	/**
	 * Get the collection of policy target containers belonging to this object.
	 * 
	 * @return a collection of <code>PolicyTargetContainer</code> objects.
	 */
	Map<String, PolicyActionContainer> getPolicyActionContainers();
	
}

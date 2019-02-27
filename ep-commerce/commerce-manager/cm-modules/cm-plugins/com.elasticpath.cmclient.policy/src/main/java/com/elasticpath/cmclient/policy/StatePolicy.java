/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.policy;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;

/**
 * Interface that defines a UI state based on a policy.
 */
public interface StatePolicy {

	/**
	 * Initialize this policy, passing in any dependencies.
	 * 
	 * @param dependentObject an object the policy may depend on for further information.
	 */
	void init(Object dependentObject);
	
	/**
	 * Apply this policy to the given control policy target container.
	 * 
	 * @param targetContainer the policy target container.
	 */
	void apply(PolicyActionContainer targetContainer);

	/**
	 * Get the state as determined by this policy.
	 * 
	 * @param targetContainer the policy context
	 * @return an <code>EpState</code> determined by the policy.
	 */
	EpState determineState(PolicyActionContainer targetContainer);

}

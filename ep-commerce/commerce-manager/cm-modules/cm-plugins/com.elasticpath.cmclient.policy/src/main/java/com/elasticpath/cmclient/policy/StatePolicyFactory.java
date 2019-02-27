/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.policy;

/**
 * A provider of <code>StatePolicy</code>.
 */
public interface StatePolicyFactory {

	/**
	 * Get the state policy for the given policy target.
	 * 
	 * @param targetId the ID of the target 
	 * @return the <code>StatePolicy</code> for the given target.
	 */
	StatePolicy getStatePolicy(String targetId);
}

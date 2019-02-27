/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.policy;



/**
 * A <code>StatePolicyGovernable</code> that also acts as a target for
 * an injected state policy.
 */
public interface StatePolicyTarget extends StatePolicyGovernable {

	/**
	 * Add a listener to this governable.
	 * 
	 * @param governableListener a <code>GovernableListener</code>
	 */
	void addGovernableListener(StatePolicyTargetListener governableListener);

	/**
	 * Remove a governable listener from this object.
	 *
	 * @param listener a <code>GovernableListener</code>
	 */
	void removeGovernableListener(StatePolicyTargetListener listener);

	/**
	 * Get a String identifier that identifies this target.
	 * 
	 * @return a String identifier
	 */
	String getTargetIdentifier();

}
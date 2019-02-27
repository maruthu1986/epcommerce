/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.policy;

/**
 * A listener to events that may occur on <code>StatePolicyGovernable</code> objects.
 */
public interface StatePolicyTargetListener {

	/**
	 * Notifies that a <code>StatePolicyTarget</code> has been activated.
	 * 
	 * @param target the <code>StatePolicyTarget</code> that was activated. 
	 */
	void statePolicyTargetActivated(StatePolicyTarget target);
}

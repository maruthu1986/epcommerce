/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.policy.ui;

import com.elasticpath.cmclient.policy.PolicyPlugin;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.StatePolicyFactory;
import com.elasticpath.cmclient.policy.StatePolicyTarget;
import com.elasticpath.cmclient.policy.StatePolicyTargetListener;

/**
 * Listener that will set a <code>StatePolicy</code> on a state policy target.
 */
public class StatePolicyListener implements StatePolicyTargetListener {


	/**
	 * When a state policy target is activated, get the appropriate policy from the factory
	 * and apply it to the governable.
	 *
	 * @param target the <code>StatePolicyTarget</code> that was activated.
	 */
	@Override
	public void statePolicyTargetActivated(final StatePolicyTarget target) {
		StatePolicy policy = getStatePolicy(target.getTargetIdentifier());
		if (policy != null) {
			target.applyStatePolicy(policy);
		}
	}

	/**
	 * Get the state policy for the given target by iterating through
	 * the collection of policies.
	 *
	 * @param targetId the identified policy target.
	 * @return a <code>StatePolicy</code> for the identified target.
	 */
	protected StatePolicy getStatePolicy(final String targetId) {
		for (StatePolicyFactory factory : PolicyPlugin.getDefault().getStatePolicyFactories()) {
			StatePolicy policy = factory.getStatePolicy(targetId);
			if (policy != null) {
				return policy;
			}
		}
		return null;
	}

}

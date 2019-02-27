/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.policy.common;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.StatePolicyGovernable;

/**
 * Default implementation of a state policy target.
 */
public class DefaultStatePolicyGovernableImpl implements StatePolicyGovernable {

	private final Map<String, PolicyActionContainer> policyTargetContainers = new HashMap<>();

	@Override
	public void applyStatePolicy(final StatePolicy policy) {
		for (PolicyActionContainer container : getPolicyActionContainers().values()) {
			policy.apply(container);
		}
		refreshLayout();
	}

	@Override
	public Map<String, PolicyActionContainer> getPolicyActionContainers() {
		return policyTargetContainers;
	}

	@Override
	public PolicyActionContainer addPolicyActionContainer(final String name) {
		PolicyActionContainer container = new PolicyActionContainer(name);
		getPolicyActionContainers().put(name, container);
		return container;
	}

	/**
	 * Override to ensure any composite layout is refreshed. 
	 */
	public void refreshLayout() {
		// Nothing required for default case.
	}

}

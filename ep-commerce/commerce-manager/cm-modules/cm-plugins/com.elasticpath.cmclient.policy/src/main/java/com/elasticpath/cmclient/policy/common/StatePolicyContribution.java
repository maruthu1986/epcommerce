/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.policy.common;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import com.elasticpath.cmclient.policy.StatePolicy;

/**
 * Represents the information required for a contribution to a state policy.
 */
public class StatePolicyContribution {
	
	private static final Logger LOG = Logger.getLogger(StatePolicyExtensionRetriever.class);
	
	private static final String ATTR_TARGET_ID = "targetId"; //$NON-NLS-1$

	private static final String ATTR_PRIORITY = "priority"; //$NON-NLS-1$

	private static final String ATTR_STATE_POLICY = "statePolicy"; //$NON-NLS-1$

	private final IConfigurationElement configElement;

	/**
	 * Create a new state policy contribution.
	 * 
	 * @param configElement the configuration element
	 */
	public StatePolicyContribution(final IConfigurationElement configElement) {
		super();
		this.configElement = configElement;
	}

	/**
	 * Get the state policy being contributed. Creates a new instance of the class on each invocation.
	 * This is required so that the state policies are not shared between different editors, parts, etc.
	 * 
	 * @return the unique instance of a StatePolicy
	 */
	public StatePolicy getStatePolicy() {
		try {
			return (StatePolicy) configElement.createExecutableExtension(ATTR_STATE_POLICY);
		} catch (CoreException e) {
			LOG.error("Failed to instantiate state policy: " //$NON-NLS-1$
					+ configElement.getAttribute(ATTR_STATE_POLICY) + " in plugin: " //$NON-NLS-1$
					+ configElement.getDeclaringExtension().getNamespaceIdentifier(), e);
		}
		return null;
	}

	/**
	 * Get the priority of the policy.
	 * 
	 * @return the priority
	 */
	public int getPriority() {
		return Integer.valueOf(configElement.getAttribute(ATTR_PRIORITY));
	}

	/**
	 * Get the id of the policy target.
	 * 
	 * @return the targetId
	 */
	public String getTargetId() {
		return configElement.getAttribute(ATTR_TARGET_ID);
	}
	
}

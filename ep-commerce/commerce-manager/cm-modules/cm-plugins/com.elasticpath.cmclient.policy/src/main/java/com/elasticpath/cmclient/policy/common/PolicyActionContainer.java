/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.policy.common;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import com.elasticpath.cmclient.policy.StateChangeTarget;
import com.elasticpath.cmclient.policy.StatePolicyDelegate;

/**
 * A container for items that will be acted upon by a <code>StatePolicy</code>.
 */
public class PolicyActionContainer {
	
	private final String name;

	private final Collection<StateChangeTarget> targets = new LinkedList<>();

	private final Collection<StatePolicyDelegate> delegates = new LinkedList<>();

	private Object policyDependent;

	/**
	 * Create a new policy target container with the given name.
	 *
	 * @param name the name of this target container
	 */
	public PolicyActionContainer(final String name) {
		super();
		this.name = name;
	}

	/**
	 * Add a policy target to this container.
	 * 
	 * @param target the <code>PolicyTarget</code> to add
	 */
	public void addTarget(final StateChangeTarget target) {
		targets.add(target);
	}
	
	/**
	 * Remove a policy target from this container.
	 * 
	 * @param target the <code>PolicyTarget</code> to remove.
	 */
	public void removeTarget(final StateChangeTarget target) {
		targets.remove(target);
	}
	
	/**
	 * Get the collection of UI controls in this context.
	 * 
	 * @return a read-only collection of controls.
	 */
	public Collection<StateChangeTarget> getTargets() {
		return Collections.unmodifiableCollection(targets);
	}

	/**
	 * Add a {@link StatePolicyDelegate} to this container.
	 * 
	 * @param delegate a <code>StatePolicyDelegate</code> object.
	 */
	public void addDelegate(final StatePolicyDelegate delegate) {
		delegates.add(delegate);
	}
	
	/**
	 * Remove a {@link StatePolicyDelegate} object from this container.
	 * 
	 * @param delegate a {@link StatePolicyDelegate} object.
	 */
	public void removeDelegate(final StatePolicyDelegate delegate) {
		delegates.remove(delegate);
	}
	
	/**
	 * Get the collection of {@link StatePolicyDelegate}s from this container.
	 * 
	 * @return a read-only collection of <code>StatePolicyDelegate</code>
	 */
	public Collection<StatePolicyDelegate> getDelegates() {
		return Collections.unmodifiableCollection(delegates);
	}
	
	/**
	 * Get the context name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get any policy dependent object.
	 * 
	 * @return the policyDependent object
	 */
	public Object getPolicyDependent() {
		return policyDependent;
	}

	/**
	 * Set into the container an object the policy may depend on.
	 * 
	 * @param policyDependent the policy dependent object to set
	 */
	public void setPolicyDependent(final Object policyDependent) {
		this.policyDependent = policyDependent;
	}
}

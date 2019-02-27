/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.policy.ui;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.HandlerEvent;
import org.eclipse.core.runtime.ListenerList;

import com.elasticpath.cmclient.core.registry.ObjectRegistry;
import com.elasticpath.cmclient.core.registry.ObjectRegistryListener;
import com.elasticpath.cmclient.policy.PolicyPlugin;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.StatePolicyDelegate;
import com.elasticpath.cmclient.policy.StatePolicyTarget;
import com.elasticpath.cmclient.policy.StatePolicyTargetListener;
import com.elasticpath.cmclient.policy.common.DefaultStatePolicyDelegateImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;

/**
 * A <code>StatePolicy</code> aware Handler. This registers itself as an object registry
 * listener and a policy plugin handler to allow injection of a <code>StatePolicy</code>
 * into handlers. 
 */
public abstract class AbstractPolicyAwareHandler extends AbstractHandler implements StatePolicyTarget, ObjectRegistryListener {

	private final StatePolicyDelegate delegate = new DefaultStatePolicyDelegateImpl();
	
	private StatePolicy statePolicy;

	private static ListenerList listenerList;

	/** Active change set. */
	protected static final String ACTIVE_CHANGE_SET = "activeChangeSet"; //$NON-NLS-1$

	static {
		listenerList = new ListenerList(ListenerList.IDENTITY);		
	}
	
	/**
	 * Register as an object registry listener and state policy handler on construction
	 * and fire a handler changed event to allow the enabled property to be
	 * policy driven.
	 */
	public AbstractPolicyAwareHandler() {
		super();
		ObjectRegistry.getInstance().addObjectListener(this);
		PolicyPlugin.getDefault().registerStatePolicyTarget(this);
		fireStatePolicyTargetActivated();
	}

	@Override
	public PolicyActionContainer addPolicyActionContainer(final String name) {
		return delegate.addPolicyActionContainer(name);
	}

	@Override
	public void applyStatePolicy(final StatePolicy statePolicy) {
		this.statePolicy = statePolicy;
		delegate.applyStatePolicy(statePolicy);
	}

	/**
	 * Get the state policy that has already been applied.
	 * 
	 * @return a <code>StatePolicy</code>
	 */
	protected StatePolicy getStatePolicy() {
		return statePolicy;
	}

	@Override
	public Map<String, PolicyActionContainer> getPolicyActionContainers() {
		return delegate.getPolicyActionContainers();
	}

	@Override
	public void objectAdded(final String key, final Object object) {
		if (ACTIVE_CHANGE_SET.equals(key)) {
			fireHandlerChanged(new HandlerEvent(this, true, false));
		}
	}

	@Override
	public void objectRemoved(final String key, final Object object) {
		if (ACTIVE_CHANGE_SET.equals(key)) {
			fireHandlerChanged(new HandlerEvent(this, true, false));
		}
	}

	@Override
	public void objectUpdated(final String key, final Object oldValue, final Object newValue) {
		if (ACTIVE_CHANGE_SET.equals(key)) {
			fireHandlerChanged(new HandlerEvent(this, true, false));
		}
	}

	@Override
	public void addGovernableListener(final StatePolicyTargetListener governableListener) {
		listenerList.add(governableListener);		
	}

	@Override
	public void removeGovernableListener(final StatePolicyTargetListener listener) {
		listenerList.remove(listener);
	}
	
	/**
	 * Fire the activation event to all listeners.
	 */
	private void fireStatePolicyTargetActivated() {
		for (Object listener : listenerList.getListeners()) {
			((StatePolicyTargetListener) listener).statePolicyTargetActivated(this);
		}
	}		
}

/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.policy.common;

import org.eclipse.core.runtime.ListenerList;

import com.elasticpath.cmclient.policy.PolicyPlugin;
import com.elasticpath.cmclient.policy.StatePolicyTarget;
import com.elasticpath.cmclient.policy.StatePolicyTargetListener;

/**
 * TODO.
 */
public abstract class AbstractStatePolicyTargetImpl extends DefaultStatePolicyDelegateImpl implements StatePolicyTarget {
	
	private final ListenerList listenerList = new ListenerList(ListenerList.IDENTITY);
	
	/**
	 * TODO.
	 */
	public AbstractStatePolicyTargetImpl() {
		PolicyPlugin.getDefault().registerStatePolicyTarget(this);
	}

	/**
	 * Fire the activation event to all listeners.
	 */
	protected void fireStatePolicyTargetActivated() {
		for (Object listener : listenerList.getListeners()) {
			((StatePolicyTargetListener) listener).statePolicyTargetActivated(this);
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

}

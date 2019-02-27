/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.policy.ui;

import java.util.Map;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.core.registry.ObjectRegistry;
import com.elasticpath.cmclient.core.registry.ObjectRegistryListener;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.PolicyPlugin;
import com.elasticpath.cmclient.policy.StateChangeTarget;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.StatePolicyDelegate;
import com.elasticpath.cmclient.policy.StatePolicyTarget;
import com.elasticpath.cmclient.policy.StatePolicyTargetListener;
import com.elasticpath.cmclient.policy.common.DefaultStatePolicyDelegateImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;

/**
 * This extension of an {@link Action} involves the usage of state policies.
 * The class registers itself both as a state policy target and a state change target.
 * This allows for handling its own container state and enabling/disabling the action 
 * depending on the policies state.
 * Providing a dependent object by overriding {@link #getDependentObject()} is essential 
 * if the associated policies require the context of an object.
 */
public abstract class AbstractPolicyAwareAction extends Action implements StatePolicyTarget, StateChangeTarget, ObjectRegistryListener {

	private final ChangeSetHelper changeSetHelper = ServiceLocator.getService(ChangeSetHelper.BEAN_ID);
	private final StatePolicyDelegate delegate = new DefaultStatePolicyDelegateImpl();
	
	private StatePolicy statePolicy;

	private static ListenerList listenerList = new ListenerList(ListenerList.IDENTITY);
	
	private final PolicyActionContainer defaultContainer;

	/** Active change set. */
	protected static final String ACTIVE_CHANGE_SET = ChangeSetHelper.OBJECT_REG_ACTIVE_CHANGE_SET;

	/**
	 * Register as an object registry listener and state policy handler on construction
	 * and fire a handler changed event to allow the enabled property to be
	 * policy driven.
	 */
	public AbstractPolicyAwareAction() {
		super();
		ObjectRegistry.getInstance().addObjectListener(this);
		// register the action as a state policy target...
		PolicyPlugin.getDefault().registerStatePolicyTarget(this);
		// and also register the action as a state change target
		defaultContainer = delegate.addPolicyActionContainer(getTargetIdentifier());
		defaultContainer.addTarget(this);
		// fire policy target activated event in order to get the right policy applied
		fireStatePolicyTargetActivated();
	}
	
	/**
	 * Creates a new action with the given text and no image. Calls the zero-arg constructor, which disables the action if not authorized, then
	 * <code>setText</code>.
	 * 
	 * @param text the string used as the text for the action, or <code>null</code> if there is no text
	 * @see #setText
	 */
	protected AbstractPolicyAwareAction(final String text) {
		this();
		setText(text);
	}

	/**
	 * Creates a new action with the given text and image. Calls the zero-arg constructor, which disables the action if not authorized, then
	 * <code>setText</code> and <code>setImageDescriptor</code>.
	 * 
	 * @param text the action's text, or <code>null</code> if there is no text
	 * @param image the action's image, or <code>null</code> if there is no image
	 * @see #setText
	 * @see #setImageDescriptor
	 */
	protected AbstractPolicyAwareAction(final String text, final ImageDescriptor image) {
		this(text);
		setImageDescriptor(image);
	}

	@Override
	public PolicyActionContainer addPolicyActionContainer(final String name) {
		return delegate.addPolicyActionContainer(name);
	}

	@Override
	public void applyStatePolicy(final StatePolicy statePolicy) {
		this.statePolicy = statePolicy;
		statePolicy.init(getDependentObject());
		delegate.applyStatePolicy(statePolicy);
	}

	/**
	 * Provides the dependent object that should be used by the policies.
	 * 
	 * @return the dependent object for that action
	 */
	protected Object getDependentObject() {
		return null;
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
			reApplyStatePolicy();
		}
	}

	/**
	 *
	 */
	public void reApplyStatePolicy() {
		if (this.statePolicy != null) {
			applyStatePolicy(this.statePolicy);
		}
	}

	@Override
	public void objectRemoved(final String key, final Object object) {
		if (ACTIVE_CHANGE_SET.equals(key)) {			
			reApplyStatePolicy();
		}
	}

	@Override
	public void objectUpdated(final String key, final Object oldValue, final Object newValue) {
		if (ACTIVE_CHANGE_SET.equals(key)) {
			reApplyStatePolicy();
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

	@Override
	public void setState(final EpState state) {
		setEnabled(state == EpState.EDITABLE);
	}
	
	/**
	 * @return the listenerList
	 */
	protected static ListenerList getListenerList() {
		return listenerList;
	}

	/**
	 * Get the default policy action container.
	 * 
	 * @return the defaultContainer
	 */
	public PolicyActionContainer getDefaultContainer() {
		return defaultContainer;
	}

	public ChangeSetHelper getChangeSetHelper() {
		return changeSetHelper;
	}
}

/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.policy.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.wizard.AbstractEpWizard;
import com.elasticpath.cmclient.policy.PolicyPlugin;
import com.elasticpath.cmclient.policy.StateChangeTarget;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.StatePolicyDelegate;
import com.elasticpath.cmclient.policy.StatePolicyTarget;
import com.elasticpath.cmclient.policy.StatePolicyTargetListener;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;

/**
 * Provides state policy support to wizards.
 * @param <T> the type of the wizard
 */
public abstract class AbstractPolicyAwareWizard<T> extends AbstractEpWizard<T> 
	implements StatePolicyTarget, StateChangeTarget {

	private final Map<String, PolicyActionContainer> policyTargetContainers = new HashMap<>();

	private final ListenerList listenerList = new ListenerList(ListenerList.IDENTITY);

	private EpState currentState = EpState.EDITABLE;

	private StatePolicy statePolicy;

	/**
	 * Constructs a new instance.
	 * 
	 * @param windowTitle the window title
	 * @param pagesTitleBlank the page title
	 * @param wizardImage the wizard image
	 */
	public AbstractPolicyAwareWizard(final String windowTitle, final String pagesTitleBlank, final Image wizardImage) {
		super(windowTitle, pagesTitleBlank, wizardImage);
		PolicyPlugin.getDefault().registerStatePolicyTarget(this);
	}

	@Override
	public void applyStatePolicy(final StatePolicy statePolicy) {
		this.statePolicy = statePolicy;
		
		statePolicy.init(getDependentObject());
		
		for (PolicyActionContainer container : getPolicyActionContainers().values()) {
			statePolicy.apply(container);
		}
	}

	/**
	 *
	 * @return the dependent object
	 */
	protected Object getDependentObject() {
		return getModel();
	}

	@Override
	public PolicyActionContainer addPolicyActionContainer(final String name) {
		PolicyActionContainer container = new PolicyActionContainer(name);
		getPolicyActionContainers().put(name, container);
		return container;
	}

	/**
	 * Get the collection of policy target containers belonging to this object.
	 * Control containers have no delegates so this just returns an empty map.
	 * 
	 * @return a collection of <code>PolicyTargetContainer</code> objects.
	 */
	@Override
	public Map<String, PolicyActionContainer> getPolicyActionContainers() {
		return policyTargetContainers;
	}

	/**
	 *
	 * @param page the wizard page
	 * @param container the policy action container 
	 */
	public void addPage(final IWizardPage page, final PolicyActionContainer container) {
		super.addPage(page);
		
		if (page instanceof StatePolicyDelegate) {
			container.addDelegate((StatePolicyDelegate) page);
		}
		if (page instanceof StateChangeTarget) {
			container.addTarget((StateChangeTarget) page);
		}
		
		if (page instanceof AbstractPolicyAwareWizardPage) {
			((AbstractPolicyAwareWizardPage< ? >) page).setDependentObject(this.getDependentObject());
		}
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
	public void addGovernableListener(final StatePolicyTargetListener governableListener) {
		listenerList.add(governableListener);		
	}

	@Override
	public void removeGovernableListener(final StatePolicyTargetListener listener) {
		listenerList.remove(listener);
	}

	@Override
	public void createPageControls(final Composite pageContainer) {
		super.createPageControls(pageContainer);

		// fire event that the wizard is created
		fireStatePolicyTargetActivated();
	}


	@Override
	public void setState(final EpState state) {
		this.currentState = state;
	}

	/**
	 * Check if can finish.
	 *
	 * @return true, if can.
	 */
	@Override
	public boolean canFinish() {
		return super.canFinish() && isEditable();
	}

	/**
	 *
	 * @return whether the wizard is for editing
	 */
	public boolean isEditable() {
		return currentState == EpState.EDITABLE;
	}
	
	
	/**
	 * Re-apply state policy.
	 */
	public void reApplyStatePolicy() {
		if (this.statePolicy != null) {
			applyStatePolicy(this.statePolicy);
		}
	}
	
}

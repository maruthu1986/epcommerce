/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.policy.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.DataBindingContext;

import com.elasticpath.cmclient.core.registry.ObjectRegistry;
import com.elasticpath.cmclient.core.ui.ICompositeBlock;
import com.elasticpath.cmclient.core.ui.ICompositeBlockSupport;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.wizard.AbstractEPWizardPage;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.StatePolicyDelegate;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;

/**
 * Provides support to the wizard pages for applying policies to the underlying controls.
 * 
 * @param <T> the type of the wizard page
 */
public abstract class AbstractPolicyAwareWizardPage<T> extends AbstractEPWizardPage<T> implements StatePolicyDelegate, ICompositeBlockSupport {

	private final Map<String, PolicyActionContainer> policyTargetContainers = new HashMap<>();

	private final List<ICompositeBlock> blocks = new ArrayList<>();

	private Object dependentObject;

	/**
	 * Constructs the wizard page.
	 *  @param numColumns columns count for the GridLayout.
	 * @param equalWidthColumns should the columns be with equal width.
	 * @param pageName name of the page.
	 * @param titleName the titleName
	 * @param message the message
	 * @param dataBindingContext Data Binding Context for managing bindings.
	 */
	public AbstractPolicyAwareWizardPage(final int numColumns, final boolean equalWidthColumns,
										 final String pageName, final String titleName, final String message,
										 final DataBindingContext dataBindingContext) {
		super(numColumns, equalWidthColumns, pageName, titleName, message, dataBindingContext);
		ObjectRegistry.getInstance().putObject("activeEditor", this); //$NON-NLS-1$
	}

	/**
	 * Constructs the wizard page.
	 * 
	 * @param numColumns columns count for the GridLayout.
	 * @param equalWidthColumns should the columns be with equal width.
	 * @param pageName name of the page.
	 * @param dataBindingContext Data Binding Context for managing bindings.
	 */
	protected AbstractPolicyAwareWizardPage(final int numColumns, final boolean equalWidthColumns, 
			final String pageName, final DataBindingContext dataBindingContext) {
		super(numColumns, equalWidthColumns, pageName, dataBindingContext);
	}

	@Override
	protected void createEpPageContent(final IEpLayoutComposite pageComposite) {
		IPolicyTargetLayoutComposite policyComposite = PolicyTargetCompositeFactory.wrapLayoutComposite(pageComposite);
		
		createPageContents(policyComposite);

		if (isCreateCompositeBlock()) {
			for (ICompositeBlock block : blocks) {
				block.init(pageComposite, getDependentObject());
			}
		}
	}

	/**
	 * Implement this method in order to add all the policy target controls.
	 * 
	 * @param policyComposite the policy composite
	 */
	protected abstract void createPageContents(IPolicyTargetLayoutComposite policyComposite);


	@Override
	public void applyStatePolicy(final StatePolicy statePolicy) {
		for (PolicyActionContainer container : getPolicyActionContainers().values()) {
			statePolicy.apply(container);
		}
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

	@Override
	public void addCompositeBlock(final ICompositeBlock block) {
		blocks.add(block);
	}
	
	/**
	 * Gets the dependent object.
	 * 
	 * @return the dependent object or null
	 */
	protected Object getDependentObject() {
		return dependentObject;
	}

	/**
	 * Sets the dependent object.
	 * 
	 * @param dependentObject the dependent object
	 */
	public void setDependentObject(final Object dependentObject) {
		this.dependentObject = dependentObject;
	}

	/**
	 * Flag that determines if we should display the composite blocks.
	 *
	 * @return flag that determines if we should display the composite blocks.
	 */
	protected boolean isCreateCompositeBlock() {
		return true;
	}
}

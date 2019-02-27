/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.policy.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;

import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.StatePolicyDelegate;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;

/**
 * Abstract extension of AbstractCmClientEditorPageSectionPart that can apply a <code>StatePolicy</code>.
 */
public abstract class AbstractPolicyAwareEditorPageSectionPart extends AbstractCmClientEditorPageSectionPart implements StatePolicyDelegate {

	private final Map<String, PolicyActionContainer> policyTargetContainers = new HashMap<>();

	private StatePolicy statePolicy;

	private final List<Composite> compositesToRefresh;
	
	/**
	 * Get the state policy.
	 * 
	 * @return the instance of the state policy
	 */
	public StatePolicy getStatePolicy() {
		return statePolicy;
	}

	/**
	 * Constructor to create a new Section in an editor's FormPage.
	 *
	 * @param formPage the formpage
	 * @param editor the CmClientFormEditor that contains the form
	 * @param style the style bits applicable to a <code>Section</code>
	 */
	public AbstractPolicyAwareEditorPageSectionPart(final FormPage formPage, final AbstractCmClientFormEditor editor, final int style) {
		super(formPage, editor, style);
		compositesToRefresh = new ArrayList<>();
	}

	@Override
	public void applyStatePolicy(final StatePolicy statePolicy) {
		this.statePolicy = statePolicy;
		for (PolicyActionContainer container : getPolicyActionContainers().values()) {
			statePolicy.apply(container);
		}
		refreshLayout();
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
	 * Override to implement custom layout refresh, or add composites needing
	 * refreshing with addCompositesToRefresh. 
	 */
	public void refreshLayout() {
		for (Composite composite : getCompositesToRefresh()) {
			layoutIfNotDisposed(composite);
		}
	}
	
	/**
	 * Add composites which need to have their layout refreshed when refreshLayout is called.
	 * @param composites composites needing refreshing.
	 */
	public void addCompositesToRefresh(final Composite... composites) {
		Collections.addAll(compositesToRefresh, composites);
	}

	/**
	 * Get the list of composites to refresh. 
	 * @return the list of composites to refresh.
	 */
	protected List<Composite> getCompositesToRefresh() {
		return compositesToRefresh;
	}

	/**
	 * Call to layout with null && disposed protection.
	 * @param composite the composite to layout.
	 */
	protected void layoutIfNotDisposed(final Composite composite) {
		if (composite != null && !composite.isDisposed()) {
			composite.layout();
		}
	}
}

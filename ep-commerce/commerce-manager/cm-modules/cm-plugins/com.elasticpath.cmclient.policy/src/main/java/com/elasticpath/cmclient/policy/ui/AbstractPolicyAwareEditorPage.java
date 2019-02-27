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
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;

import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPage;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.policy.StateChangeTarget;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.StatePolicyDelegate;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;

/**
 * Abstract extension of AbstractCmClientEditorPage that can apply a <code>StatePolicy</code>.
 */
public abstract class AbstractPolicyAwareEditorPage extends AbstractCmClientEditorPage implements StatePolicyDelegate {

    private final Map<String, PolicyActionContainer> policyTargetContainers = new HashMap<>();

	private final List<Composite> compositesToRefresh;
	
	/**
	 * Constructs the editor page.
	 * 
	 * @param editor The EP FormEditor
	 * @param partId the id for the editor page
	 * @param title the title of the editor page
	 * @param isLocaleDependent indicate whether to show the locale selection combo 
	 */
	public AbstractPolicyAwareEditorPage(final AbstractCmClientFormEditor editor, final String partId, final String title, 
			final boolean isLocaleDependent) {
		super(editor, partId, title, isLocaleDependent);
		compositesToRefresh = new ArrayList<>();
    }

	/**
	 * Constructs the editor page.
	 * 
	 * @param editor The EP FormEditor
	 * @param partId the id for the editor page
	 * @param title the title of the editor page
	 */
	public AbstractPolicyAwareEditorPage(final AbstractCmClientFormEditor editor, final String partId, final String title) {
		super(editor, partId, title);
		compositesToRefresh = new ArrayList<>();
    }

    @Override
	public void applyStatePolicy(final StatePolicy statePolicy) {
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
	 * Add an editor form part to a managed form and a policy target container.
	 * 
	 * @param container the <code>PolicyTargetContainer</code> this part belongs in
	 * @param managedForm the <code>IManagedForm</code> that owns this part
	 * @param part the <code>IFormPart</code> to add.
	 */
	protected void addPart(final PolicyActionContainer container, final IManagedForm managedForm, final IFormPart part) {
		if (part instanceof StatePolicyDelegate) {
			container.addDelegate((StatePolicyDelegate) part);
		}
		if (part instanceof StateChangeTarget) {
			container.addTarget((StateChangeTarget) part);
		}
		managedForm.addPart(part);
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

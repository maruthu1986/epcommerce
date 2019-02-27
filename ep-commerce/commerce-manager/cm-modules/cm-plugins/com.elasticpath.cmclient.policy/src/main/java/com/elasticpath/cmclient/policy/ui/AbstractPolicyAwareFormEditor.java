/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.policy.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.helpers.EPTestUtilFactory;
import com.elasticpath.cmclient.policy.PolicyPlugin;
import com.elasticpath.cmclient.policy.StateChangeTarget;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.StatePolicyDelegate;
import com.elasticpath.cmclient.policy.StatePolicyTarget;
import com.elasticpath.cmclient.policy.StatePolicyTargetListener;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;

/**
 * Abstract extension of AbstractCmClientFormEditor that can apply a <code>StatePolicy</code>.
 */
public abstract class AbstractPolicyAwareFormEditor extends AbstractCmClientFormEditor implements StatePolicyTarget {

	private final Map<String, PolicyActionContainer> policyTargetContainers = new HashMap<>();

	private StatePolicy statePolicy;

	private static ListenerList listenerList;

	static {
		listenerList = new ListenerList(ListenerList.IDENTITY);		
	}

	
	/**
	 * Ensure editor is registered as a state policy target.
	 */
	public AbstractPolicyAwareFormEditor() {
		super();
		PolicyPlugin.getDefault().registerStatePolicyTarget(this);
	}

	/**
	 * Create pages.
	 */
	@Override
	protected void createPages() {
		super.createPages();
		fireStatePolicyTargetActivated();
	}
	

	/**
	 * Apply the given policy, storing it for later use.
	 * 
	 * @param statePolicy the <code>StatePolicy</code> to apply. 
	 */
	@Override
	public void applyStatePolicy(final StatePolicy statePolicy) {
		this.statePolicy = statePolicy;
		statePolicy.init(getDependentObject());
		applyStatePolicy();
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
	 * Page change handler.
	 *
	 * @param newPageIndex the new page index.
	 */
	@Override
	protected void pageChange(final int newPageIndex) {
		super.pageChange(newPageIndex);
		Object selectedPage = getSelectedPage();
		if (selectedPage instanceof StatePolicyDelegate && statePolicy != null) {
			((StatePolicyDelegate) selectedPage).applyStatePolicy(statePolicy);
		}
	}
	
	/**
	 * Apply the already stored state policy.
	 */
	public void applyStatePolicy() {
		if (statePolicy != null) {
			for (PolicyActionContainer container : getPolicyActionContainers().values()) {
				statePolicy.apply(container);
			}
		}
	}

	/**
	 * Add a page to the form editor and also to the policy target container.
	 * 
	 * @param page the page to add
	 * @param container the policy action container to add the page to
	 * @return the index of the page
	 * @throws PartInitException if an error occurs adding the page
	 */
	public int addPage(final IFormPage page, final PolicyActionContainer container) throws PartInitException {
		if (page instanceof StatePolicyDelegate) {
			container.addDelegate((StatePolicyDelegate) page);
		}
		if (page instanceof StateChangeTarget) {
			container.addTarget((StateChangeTarget) page);
		}

		Composite tabFolder = this.getContainer();
		EPTestUtilFactory.getInstance().getTestIdUtil().addIdToMultiPageEditorTabFolder(tabFolder, page);

		return addPage(page);
	}
	
	@Override
	public void refreshEditorPages() {
		this.getContainer().setRedraw(false);
		super.refreshEditorPages();
		
		//Only apply state policy to the active page.
		final IFormPage activePage = getActivePageInstance();
		if (statePolicy != null) {
			applyStatePolicyForPage(activePage);
		}
		this.getContainer().setRedraw(true);
	}

	@Override
	public void reloadPage(final String pageId) {
		super.reloadPage(pageId);
		IFormPage page = findPage(pageId);
		if (page != null) {
			applyStatePolicyForPage(page);
		}
	}

	/**
	 * Apply state policy for a given page.
	 *
	 * @param page the page
	 */
	private void applyStatePolicyForPage(final IFormPage page) {
			for (PolicyActionContainer container : getPolicyActionContainers().values()) {
				for (StatePolicyDelegate delegate : container.getDelegates()) {
				if (page.equals(delegate)) {
						delegate.applyStatePolicy(statePolicy);
					break;
					}
				}
			}
		}
	
	/**
	 * State policy reinit with fresh model (a model after save operation for example).
	 */
	protected void reinitStatePolicy() {
		statePolicy.init(getDependentObject());
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

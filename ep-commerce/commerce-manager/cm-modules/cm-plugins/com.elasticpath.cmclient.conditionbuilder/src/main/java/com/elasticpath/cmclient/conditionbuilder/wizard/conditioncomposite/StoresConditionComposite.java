/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.conditionbuilder.wizard.conditioncomposite;

import java.util.LinkedList;
import java.util.List;

import com.elasticpath.cmclient.conditionbuilder.wizard.duallistbox.RemoveObjectHandler;
import com.elasticpath.cmclient.conditionbuilder.wizard.duallistbox.StoreSelectionDualListBox;
import com.elasticpath.cmclient.conditionbuilder.wizard.model.StoresConditionModelAdapter;
import com.elasticpath.cmclient.core.ui.framework.IDualListChangeListener;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.domain.store.Store;

/**
 * Delegate class for the STORES condition builder control.
 */
public class StoresConditionComposite {

	private final StoreSelectionDualListBox storeDualListBox;
	
	private final StoresConditionModelAdapter model;
	
	/**
	 * Constructor.
	 * @param parent parent composite
	 * @param container the policy container
	 * @param availableTitle title for the list of available stores
	 * @param assignedTitle title for the list of assigned stores
	 * @param listener action listener that handles the changes of this control 
	 * @param model model adapter
	 * @param isEditorUsage if this composite used in the editor
	 */
	public StoresConditionComposite(final IPolicyTargetLayoutComposite parent,
			final PolicyActionContainer container,
			final String availableTitle,
			final String assignedTitle, final IDualListChangeListener listener, 
			final StoresConditionModelAdapter model, final boolean isEditorUsage) {

		storeDualListBox = new StoreSelectionDualListBox(parent, container, model.getStores(), availableTitle, assignedTitle);
		storeDualListBox.createControls();
		storeDualListBox.registerChangeListener(() -> {
			List<Store> storesSet = new LinkedList<>(storeDualListBox.getAssigned());
			model.setStores(storesSet);
		});
		storeDualListBox.registerChangeListener(listener);
		this.model = model;
		this.model.setEditorUsage(isEditorUsage);
	}
	
	/**
	 * Returns flag if all available stores are chosen. Delegate method.
	 * 
	 * @return true - if all available stores are chosen, false - otherwise.
	 */
	public boolean isAllAvailableSelected() {
		return storeDualListBox.isAllAvailableSelected();
	}
	
	/**
	 * Sets all available stores to assigned. Delegate method.
	 */
	public void addAllEvent() {
		storeDualListBox.addAllEvent();
	}
	
	/**
	 * Removes all assigned selling channels. Delegate method.
	 */
	public void removeAllEvent() {
		storeDualListBox.removeAllEvent();
	}
	
	/**
	 * Saves selected stores to the model.
	 */
	public void saveStores() {
		List<Store> storesSet = new LinkedList<>(storeDualListBox.getAssigned());
		model.setStores(storesSet);
	}
	
	/**
	 * @return true, if the assigned list is empty. Otherwise returns false. Delegate method.
	 */
	public boolean isEmpty() {
		return storeDualListBox.getAssigned().isEmpty();
	}
	
	/**
	 * @return true if the assigned list has only one element left
	 */
	public boolean hasOneOrMoreElements() {
		return storeDualListBox.getAssigned().size() >= 1;
	}
	
	/**
	 * Adds an action listener to the current composite.
	 * 
	 * @param listChangedListener store dual list box change listener
	 */
	public void addListChangedListener(
			final IDualListChangeListener listChangedListener) {
		storeDualListBox.registerChangeListener(listChangedListener);
	}

	/**
	 * Sets <code>RemoveObjectHandler</code> instance to the dual list box.
	 * @param removeHandler <code>RemoveObjectHandler</code> instance to set
	 */
	public void setRemoveHandler(final RemoveObjectHandler<Store> removeHandler) {
		storeDualListBox.setRemoveHandler(removeHandler);
	}
	
}

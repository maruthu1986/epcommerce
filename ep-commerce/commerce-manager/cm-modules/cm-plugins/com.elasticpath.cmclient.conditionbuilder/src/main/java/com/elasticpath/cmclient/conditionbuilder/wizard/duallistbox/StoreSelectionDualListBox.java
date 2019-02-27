/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.conditionbuilder.wizard.duallistbox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.comparator.StoreComparator;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareDualListBox;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.StoreState;
import com.elasticpath.service.store.StoreService;

/**
 * The store selection dual listbox.
 */
public class StoreSelectionDualListBox extends AbstractPolicyAwareDualListBox<List<Store>> {

	private List<Store> availableStores;
	
	private RemoveObjectHandler<Store> removeHandler = new DefaultRemoveHandler();
	
	/**
	 * Constructor.
	 * 
	 * @param parent the parent composite
	 * @param container the policy action container
	 * @param availableStores the model object (the Collection)
	 * @param availableTitle the title for the Available listbox
	 * @param assignedTitle the title for the Assigned listbox
	 */
	public StoreSelectionDualListBox(final IPolicyTargetLayoutComposite parent, final PolicyActionContainer container, 
			final List<Store> availableStores, final String availableTitle,
			final String assignedTitle) {
		super(parent, parent.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true), container, availableStores, availableTitle, 
				assignedTitle, ALL_BUTTONS | MULTI_SELECTION);
	}
	
	/**
	 * Returns flag if all available stores are chosen.
	 * 
	 * @return true - if all available stores are chosen, false - otherwise.
	 */
	public boolean isAllAvailableSelected() {
		return getAvailable().size() == getAssigned().size();
	}
	
	
	/**
	 * Sets all available stores to assigned.
	 */
	public void addAllEvent() {
		super.handleAddAllEvent();
	}


	/**
	 * Removes all assigned selling channels.
	 */
	public void removeAllEvent() {
		super.handleRemoveAllEvent();
	}
	/**
	 * Validate the dual list box selection.
	 * 
	 * @return true if the selection valid
	 */
	public boolean validate() {
		return !getModel().isEmpty();
	}

	@Override
	public Collection<Store> getAssigned() {
		List<Store> assignedStores = getModel();
		assignedStores.sort(new StoreComparator());
		return assignedStores;
	}
	
	@Override
	public Collection<Store> getAvailable() {
		return getAvailableStores();
	}
	
	@Override
	public ViewerFilter getAvailableFilter() {
		return new AvailableStoresFilter();
	}

	@Override
	protected ILabelProvider getLabelProvider() {
		return new StoresSelectionLabelProvider();
	}

	@Override
	protected boolean assignToModel(final IStructuredSelection selection) {
		if (selection == null || selection.isEmpty()) {
			return false;
		}
		final List<Store> list = this.getModel();
		for (final Iterator<Store> it = selection.iterator(); it.hasNext();) {
			list.add(it.next());
		}

		return true;
	}

	@Override
	protected boolean removeFromModel(final IStructuredSelection selection) {
		if (selection == null || selection.isEmpty()) {
			return false;
		}
		
		final List<Store> list = this.getModel();
		for (final Iterator<Store> it = selection.iterator(); it.hasNext();) {
			Store store = it.next();
			if (removeHandler.isRemovalAllowed(store)) {
				list.remove(store);
			} else {
				return true;
			}
		}
		return true;
	}


	private List<Store> getAvailableStores() {
		if (null == availableStores) {
			availableStores = new ArrayList<>();
			StoreService dynamicContentService = ServiceLocator.getService(ContextIdNames.STORE_SERVICE);
			List<Store> allStores = dynamicContentService.findAllStores(LoginManager.getCmUser());
			for (Store store : allStores) {
				if (store.getStoreState().equals(StoreState.OPEN) || store.getStoreState().equals(StoreState.RESTRICTED)) {
					availableStores.add(store);		
				}
			}
			availableStores.sort(new StoreComparator());
		}
		return availableStores;
	}

	/**
	 * Filters the AvailableListView so that it doesn't display any objects that are in the AssignedListView. Subclasses should
	 * override the Select method if they want to do any filtering.
	 */
	protected class AvailableStoresFilter extends ViewerFilter {
		@Override
		public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
			boolean sel = true;
			for (Store assigned : StoreSelectionDualListBox.this.getAssigned()) {
				if (assigned.equals(element)) {
					sel = false;
					break;
				}
			}
			return sel;
		}
	}

	/**
	 * Label provider for CountryHelper listviewers.
	 */
	class StoresSelectionLabelProvider extends LabelProvider implements ILabelProvider {

		@Override
		public Image getImage(final Object element) {
			return null;
		}

		@Override
		public String getText(final Object element) {
			String text = null;
			if (element instanceof Store) {
				text = ((Store) element).getName();
			}

			return text;
		}

		@Override
		public boolean isLabelProperty(final Object element, final String property) {
			return false;
		}
	}
	
	/**
	 * Default removal handler.
	 */
	private class DefaultRemoveHandler implements RemoveObjectHandler<Store> {
		public boolean isRemovalAllowed(final Store object) {
			return true;
		}
	}

	/**
	 * @return <code>RemoveObjectHandler</code> instance
	 */
	public RemoveObjectHandler<Store> getRemoveHandler() {
		return removeHandler;
	}

	/**
	 * Sets <code>RemoveObjectHandler</code> instance.
	 * @param removeHandler <code>RemoveObjectHandler</code> instance to set
	 */
	public void setRemoveHandler(final RemoveObjectHandler<Store> removeHandler) {
		this.removeHandler = removeHandler;
	}
	
	/**
	 * Replaces the registered <code>RemoveObjectHandler</code> instance with a default implementation.
	 */
	public void unregisterRemoveHandler() {
		this.removeHandler = new DefaultRemoveHandler();
	}
	
}
/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.stores.editors;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.admin.stores.AdminStoresMessages;
import com.elasticpath.cmclient.core.helpers.store.StoreEditorModel;
import com.elasticpath.cmclient.core.helpers.store.StoreEditorModelHelper;
import com.elasticpath.cmclient.core.ui.framework.AbstractEpDualListBoxControl;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;

/**
 * The Shared Customer Accounts Selection dual ListBox for Store Editor Model.
 */
public class SharedCustomerAccountsDualListBox extends AbstractEpDualListBoxControl<StoreEditorModel> {
	
	private StoreEditorModelHelper editorModelHelper;

	/**
	 * Constructor.
	 * 
	 * @param parentComposite the Composite that contains this thing
	 * @param model the model
	 * @param availableTitle the Available Title
	 * @param assignedTitle the Assigned Title
	 */
	public SharedCustomerAccountsDualListBox(final IEpLayoutComposite parentComposite, final StoreEditorModel model,
			final String availableTitle, final String assignedTitle) {
		super(parentComposite, model, availableTitle, assignedTitle, ALL_BUTTONS | MULTI_SELECTION,
				parentComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true), EpState.EDITABLE);
	}

	/**
	 * Another Convenient Constructor.
	 * 
	 * @param parentComposite the Composite that contains this thing.
	 * @param model the model
	 */
	public SharedCustomerAccountsDualListBox(final IEpLayoutComposite parentComposite, final StoreEditorModel model) {
		this(parentComposite, model, AdminStoresMessages.get().AvailableStores, AdminStoresMessages.get().LinkedStores);
	}

	@Override
	protected boolean assignToModel(final IStructuredSelection selection) {
		if (selection == null || selection.isEmpty()) {
			return false;
		}
		
		for (final Iterator<StoreEditorModel> it = selection.iterator(); it.hasNext();) {
			final StoreEditorModel storeEditorModel = it.next();
			getAssigned().add(storeEditorModel);
		}
		return true;
	}

	@Override
	protected boolean removeFromModel(final IStructuredSelection selection) {
		if (selection == null || selection.isEmpty()) {
			return false;
		}
		
		for (final Iterator<StoreEditorModel> it = selection.iterator(); it.hasNext();) {
			final StoreEditorModel storeEditorModel = it.next();
			getAssigned().remove(storeEditorModel);
		}
		return true;
	}

	@Override
	public Collection<StoreEditorModel> getAssigned() {
		return getModel().getSharedLoginStoreEntries();
	}

	@Override
	public Collection<StoreEditorModel> getAvailable() {
		final List<StoreEditorModel> findAllStoreEditorModels = getEditorModelHelper().findAllStoreEditorModels();
		CollectionUtils.filter(findAllStoreEditorModels, arg0 -> {
			StoreEditorModel storeEditorModel = (StoreEditorModel) arg0;
			return !getModel().equals(storeEditorModel);
		});
		return findAllStoreEditorModels;
	}

	@Override
	public ViewerFilter getAvailableFilter() {
		return new ViewerFilter() {
			@Override
			public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
				for (final StoreEditorModel storeEditorModel : getAssigned()) {
					if (storeEditorModel.equals(element)) {
						return false;
					}
				}
				return true;
			}
		};
	}
	
	private StoreEditorModelHelper getEditorModelHelper() {
		if (editorModelHelper == null) {
			editorModelHelper = StoreEditorModelHelper.createStoreEditorModelHelper();
		}
		return editorModelHelper;
	}

	@Override
	protected ILabelProvider getLabelProvider() {
		return new LabelProvider() {
			@Override
			public Image getImage(final Object element) {
				return null;
			}

			@Override
			public String getText(final Object element) {
				if (element instanceof StoreEditorModel) {
					return ((StoreEditorModel) element).getName();
				}
				return null;
			}

			@Override
			public boolean isLabelProperty(final Object element, final String property) {
				return false;
			}
		};
	}
}

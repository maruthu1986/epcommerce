/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.actions.product;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.actions.AbstractCatalogViewAction;
import com.elasticpath.cmclient.core.registry.ObjectRegistryListener;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.domain.catalog.Category;

/**
 * An abstract implementation of CreateProductAction. 
 */
public abstract class AbstractCreateProductAction extends AbstractCatalogViewAction implements ObjectRegistryListener {

	private Category category;
	
	/**
	 * Actually opens wizard. Called form {@link #run()}.
	 * 
	 * @param shell the parent shell for wizard dialog
	 * @param category category that was selected
	 */
	protected abstract void openWizard(final Shell shell, final Category category);

	@Override
	public abstract String getTargetIdentifier();
	
	/**
	 * Constructor.
	 * 
	 * @param text the action text
	 * @param image the action image
	 */
	public AbstractCreateProductAction(final String text, final ImageDescriptor image) {
		super(text, image);
		this.setToolTipText(CatalogMessages.get().CreateProductAction);
		this.setEnabled(false);
	}

	@Override
	public void run() {
		if (getCategory() == null) {
			this.setEnabled(false);
			return;
		}
		openWizard(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), getCategory());
	}
	
	private Category getCategory() {
		return this.category;
	}
	
	/**
	 * Sets the selected category.
	 * 
	 * @param selectedCategory the selected category
	 */
	public void setCategory(final Category selectedCategory) {
		this.category = selectedCategory;
		this.setEnabled(isEditable(category));
	}
	
	@Override
	protected void pageSelectionChanged(final ISelection selection) {
		if (!(selection instanceof IStructuredSelection)) {
			this.setEnabled(false);
			return;
		}

		final Object selectedObject = ((IStructuredSelection) selection).getFirstElement();
		
		if (selectedObject instanceof Category) {
			this.category = (Category) selectedObject;
			setEnabled(isEditable(category));
		} else {
			setEnabled(false);
		}
	}
	
	/**
	 * IsEditable checks whether the the button should be enabled.
	 * @param object is the object to check
	 * @return true if editable, false otherwise
	 */
	protected boolean isEditable(final Object object) {
		if (!(object instanceof Category)
				|| !canCreateProductInCategory((Category) object)) {
			return false;
		}
		boolean editable = false;
		
		final StatePolicy statePolicy = getStatePolicy();
		
		if (statePolicy != null) {
			
			statePolicy.init(object);
			
			//Create sub-category is enabled only for Non-linked categories
			
			editable = (EpState.EDITABLE == statePolicy.determineState(getDefaultContainer()));
		}
		
		return editable;
	}
	
	/**
	 * Determines whether a product can be created in the given category.
	 * @param category the category in which the product is to be created
	 * @return true if a product can be created in the given category
	 */
	boolean canCreateProductInCategory(final Category category) {
		//Cannot create a product in a virtual or a linked category
		return !category.isVirtual() && !category.isLinked();
	}
	
	@Override
	protected boolean isAuthorized() {
		return true;
	}
}

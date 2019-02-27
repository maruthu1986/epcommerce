/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.actions;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.catalog.views.CatalogBrowseView;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareAction;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.service.catalog.CategoryService;


/**
 * An abstraction for the actions related to the catalog view.
 */
public abstract class AbstractCatalogViewAction extends AbstractPolicyAwareAction {



	/** Listener for selection events in the workbench. */
	private ISelectionListener pageSelectionListener;
	private Object selectedObject;
	private final CategoryService categoryService = ServiceLocator.getService(ContextIdNames.CATEGORY_SERVICE);

	/**
	 * Constructor.
	 * 
	 * @param text the action text
	 * @param image the action image
	 */
	@SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
	public AbstractCatalogViewAction(final String text, final ImageDescriptor image) {
		super(text, image);
		hookSelectionListener();
		super.setEnabled(isAuthorized());
	}

	/**
	 * Listens to changes in the selection on the workbench page. Enable the create sub-category action only when a valid category is selected.
	 * 
	 * @param selection a representation of what has been selected
	 */
	protected abstract void pageSelectionChanged(final ISelection selection);
	
	/**
	 * Adds a post selection listener so that this action knows about selection events fired from the catalog browse view.
	 */
	private void hookSelectionListener() {
		this.pageSelectionListener = (part, selection) -> {
			selectedObject = ((IStructuredSelection) selection).getFirstElement();
			pageSelectionChanged(selection);
		};
		final IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		workbenchPage.addPostSelectionListener(CatalogBrowseView.VIEW_ID, this.pageSelectionListener);
	}
	
	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled && isAuthorized());
	}

	/**
	 * Returns <code>true</code> if authorized, otherwise <code>false</code>.
	 * 
	 * @return boolean <code>true</code> if authorized, otherwise <code>false</code>
	 * @deprecated instead a policy should be involved
	 */
	@Deprecated
	protected abstract boolean isAuthorized();

	@Override
	protected Object getDependentObject() {
		return selectedObject;
	}

	/**
	 * Determine if a category has a parent.
	 *
	 * @param category - the category that needs to be checked
	 * @return <code>true</code> if this category has a parent 
	 */
	protected boolean isSubCategory(final Category category) {
		
		return category != null && category.hasParent();
	}

	/**
	 * Determine if a category is a category in a virtual catalog.
	 *
	 * @param category - the category that needs to be checked
	 * @return <code>true</code> if this category is in a virtual catalog
	 */
	protected boolean isLinked(final Category category) {
		
		return category != null && category.isLinked();
	}

	/**
	 * Returns true if the state policy for the given category determines that the category is editable.
	 * 
	 * @param category the category to check
	 * @return true if the state policy for the given category determines that the category is editable
	 */
	protected boolean isEditable(final Category category) {
		boolean editable = false;
		
		StatePolicy statePolicy = getStatePolicy();
		
		if (statePolicy != null) {
			
			statePolicy.init(category);
			
			//Create sub-category is enabled only for Non-linked categories
			
			if (category != null) {
				editable = (EpState.EDITABLE == statePolicy.determineState(getDefaultContainer()));
			}
		}
		
		return editable;
	}

	/**
	 * Determines whether the given category can be moved up or down.
	 * @param category  the category which is to be reordered
	 * @return  true if the category can be moved up or down
	 */
	protected boolean canMoveCategory(final Category category) {
		return (category != null) && isEditable(category);
	}

	public CategoryService getCategoryService() {
		return categoryService;
	}
}
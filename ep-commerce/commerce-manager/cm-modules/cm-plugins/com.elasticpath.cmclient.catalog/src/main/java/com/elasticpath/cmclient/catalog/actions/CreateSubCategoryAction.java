/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.actions;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.catalog.CatalogImageRegistry;
import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.wizards.category.CreateCategoryWizard;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.core.registry.ObjectRegistry;
import com.elasticpath.cmclient.core.registry.ObjectRegistryListener;
import com.elasticpath.cmclient.core.service.CatalogEventService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.changeset.ChangeSetMemberAction;

/**
 * This class is responsible for carrying out the action of creating a sub-category.
 */
public class CreateSubCategoryAction extends AbstractCatalogViewAction implements ObjectRegistryListener {

	private static final Logger LOG = Logger.getLogger(CreateSubCategoryAction.class);

	private static final String ACTION_NAME = "createSubCategoryAction";  //$NON-NLS-1$

	private Category category;
	
	/**
	 * Constructs a create sub-category action.
	 */
	public CreateSubCategoryAction() {
		super(CatalogMessages.get().CatalogBrowseView_Action_CreateSubCategory, CatalogImageRegistry.CATEGORY_CREATE);
		this.setEnabled(false);
		ObjectRegistry.getInstance().addObjectListener(this);
	}
	
	@Override
	public void objectAdded(final String key, final Object object) {
		if (ChangeSetHelper.OBJECT_REG_ACTIVE_CHANGE_SET.equals(key)) {
			this.setEnabled(isEditable(this.category));
		}
	}

	@Override
	public void objectRemoved(final String key, final Object object) {
		if (ChangeSetHelper.OBJECT_REG_ACTIVE_CHANGE_SET.equals(key)) {
			this.setEnabled(isEditable(this.category));
		}
	}
	
	@Override
	public void objectUpdated(final String key, 
								final Object oldValue,
								final Object newValue) {
		this.setEnabled(isEditable(this.category));
	}

	@Override
	protected void pageSelectionChanged(final ISelection selection) {
		if (!(selection instanceof IStructuredSelection)) {
			return;
		}

		final Object selectedObject = ((IStructuredSelection) selection).getFirstElement();

		if (!(selectedObject instanceof Category)) {
			category = null;
			this.setEnabled(false);
			return;
		}
		this.category = (Category) selectedObject;

		setEnabled(isEditable(selectedObject));
	}
	
	/**
	 * IsEditable checks whether the the create sub category button should be enabled.
	 * @param object is the object to check
	 * @return true if editable, false otherwise
	 */
	protected boolean isEditable(final Object object) {
		boolean editable = false;
		
		StatePolicy statePolicy = getStatePolicy();
		
		if (statePolicy != null) {
			
			statePolicy.init(object);
			
			//Create sub-category is enabled only for Non-linked categories
			editable = (EpState.EDITABLE == statePolicy.determineState(getDefaultContainer()));
		}
		
		return editable;
	}
	
	@Override
	public void run() {
		
		final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

		LOG.debug("CreateCategoryAction called."); //$NON-NLS-1$
		final Category subCategory = ServiceLocator.getService(ContextIdNames.CATEGORY);
		
		// Set the sub-category's catalog object with the parent category's catalog (reloaded as we require all fields).
		CatalogService catalogService = ServiceLocator.getService(ContextIdNames.CATALOG_SERVICE);
		Catalog catalog = catalogService.getCatalog(category.getCatalog().getUidPk());		
		subCategory.setCatalog(catalog);
		
		// This is a sub-category
		subCategory.setParent(category);
		subCategory.setVirtual(category.isVirtual());
		
		// "Visible" check-box is unchecked by default
		subCategory.setHidden(true);

		// Create and open the create category wizard
		if (Window.OK == CreateCategoryWizard.showWizard(shell, subCategory)) {
			// Set sub-category's ordering
			subCategory.setOrdering(getMaximumOrdering(category) + 1);
			
			final Category persistedCategory = getCategoryService().saveOrUpdate(subCategory);

			getChangeSetHelper().addObjectToChangeSet(persistedCategory, ChangeSetMemberAction.ADD);
			
			// Also add the sub-category to all categories that link to the selected one
			final List<Category> linkedCategories = getCategoryService().findLinkedCategories(category.getUidPk());
			for (Category currCategory : linkedCategories) {
				Category linkedCategory = getCategoryService().addLinkedCategory(persistedCategory.getUidPk(),
						currCategory.getUidPk(), currCategory.getCatalog().getUidPk());
				getChangeSetHelper().addObjectToChangeSet(linkedCategory, ChangeSetMemberAction.ADD);
			}
			
			// Fire an event to refresh the browse list view
			final ItemChangeEvent<Category> event = new ItemChangeEvent<>(this, persistedCategory, ItemChangeEvent.EventType.ADD);
			CatalogEventService.getInstance().notifyCategoryChanged(event);
		}
	}

	private int getMaximumOrdering(final Category parentCategory) {
		return getCategoryService().findMaxChildOrdering(parentCategory);
	}

	@Override
	protected boolean isAuthorized() {
		return this.category != null;
	}
	
	@Override
	public String getTargetIdentifier() {
		return ACTION_NAME;
	}
}

/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.actions;

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
import com.elasticpath.service.changeset.ChangeSetMemberAction;

/**
 * This class is responsible for carrying out the action of creating a category.
 */
public class CreateCategoryAction  extends AbstractCatalogViewAction implements ObjectRegistryListener {

	private static final Logger LOG = Logger.getLogger(CreateCategoryAction.class);
	
	private static final String ACTION_NAME = "createCategoryAction";  //$NON-NLS-1$

	private Catalog catalog;

	/**
	 * Constructs a create category action.
	 */
	public CreateCategoryAction() {
		super(CatalogMessages.get().CatalogBrowseView_Action_CreateCategory, CatalogImageRegistry.CATEGORY_CREATE);
		setEnabled(false);  
		ObjectRegistry.getInstance().addObjectListener(this);
	}
	
	@Override
	public void objectAdded(final String key, final Object object) {
		if (ChangeSetHelper.OBJECT_REG_ACTIVE_CHANGE_SET.equals(key)) {
			this.setEnabled(isEditable(this.catalog));
		}
	}

	@Override
	public void objectRemoved(final String key, final Object object) {
		if (ChangeSetHelper.OBJECT_REG_ACTIVE_CHANGE_SET.equals(key)) {
			this.setEnabled(isEditable(this.catalog));
		}
	}
	
	@Override
	public void objectUpdated(final String key,
								final Object oldValue,
								final Object newValue) {
		this.setEnabled(isEditable(this.catalog));
	}

	@Override
	protected void pageSelectionChanged(final ISelection selection) {
		if (!(selection instanceof IStructuredSelection)) {
			return;
		}

		final Object selectedObject = ((IStructuredSelection) selection).getFirstElement();

		if (!(selectedObject instanceof Catalog)) {
			this.catalog = null;
			this.setEnabled(false);
			return;
		} 
		
		this.catalog = (Catalog) selectedObject;
		
		setEnabled(isEditable(selectedObject));
	}
	
	/**
	 * IsEditable checks whether the the create category button should be enabled.
	 * @param object is the object to check
	 * @return true if editable, false otherwise
	 */
	protected boolean isEditable(final Object object) {
		boolean editable = false;
		
		StatePolicy statePolicy = getStatePolicy();
		
		if (statePolicy != null) {
			
			statePolicy.init(object);
			
			//Create category is enabled only for catalog
			editable = (EpState.EDITABLE == statePolicy.determineState(getDefaultContainer()));
		}
		
		return editable;
	}
	
	@Override
	public void run() {
		final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		
		LOG.debug("CreateCategoryAction called."); //$NON-NLS-1$
		final Category category = ServiceLocator.getService(ContextIdNames.CATEGORY);
		// Set the category's catalog object with the selected catalog
		category.setCatalog(this.catalog);
		// Set the category's virtual status
		if ((this.catalog).isMaster()) {
			category.setVirtual(false);
		} else {
			category.setVirtual(true);
		}
		// This is not a sub-category
		category.setParent(null);
		// "Visible" check-box is unchecked by default
		category.setHidden(true);
		
		// Create and open the create category wizard
		
		if (Window.OK == CreateCategoryWizard.showWizard(shell, category)) {
			// Set category's ordering
			category.setOrdering(getMaximumOrdering(catalog) + 1);
			
			// Persist the category

			getCategoryService().add(category);
			getChangeSetHelper().addObjectToChangeSet(category, ChangeSetMemberAction.ADD);
			
			// Fire an event to refresh the browse list view
			final ItemChangeEvent<Category> event = new ItemChangeEvent<>(this, category, ItemChangeEvent.EventType.ADD);
			CatalogEventService.getInstance().notifyCategoryChanged(event);
		}
	}
	
	/**
	 * Returns the number of categories assigned to this catalog, without counting sub-categories.
	 *
	 * @param catalog the <code>Catalog</code> object to check
	 * @return the number of categories assigned to this catalog, without counting sub-categories
	 */
	private int getMaximumOrdering(final Catalog catalog) {
		return getCategoryService().findMaxRootOrdering(catalog.getUidPk());
	}

	@Override
	protected boolean isAuthorized() {
		return this.catalog != null;
	}
	
	@Override
	public String getTargetIdentifier() {
		return ACTION_NAME;
	}

}
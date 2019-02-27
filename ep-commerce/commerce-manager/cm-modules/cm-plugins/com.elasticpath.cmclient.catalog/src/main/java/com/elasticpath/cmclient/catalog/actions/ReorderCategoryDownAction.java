/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.elasticpath.cmclient.catalog.CatalogImageRegistry;
import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPermissions;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.core.registry.ObjectRegistry;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.service.CatalogEventService;
import com.elasticpath.domain.catalog.Category;

/**
 * This class is responsible for carrying out the action of reordering categories down.
 */
public class ReorderCategoryDownAction extends AbstractCatalogViewAction {

	private static final Logger LOG = Logger.getLogger(ReorderCategoryDownAction.class);

	private Category category;


	/**
	 * Constructs a create sub-category action.
	 */
	public ReorderCategoryDownAction() {
		super(CatalogMessages.get().CatalogBrowseView_Action_ReorderCategoryDown, CatalogImageRegistry.MOVE_DOWN);
		this.setEnabled(false);
		ObjectRegistry.getInstance().addObjectListener(this);
	}

	@Override
	protected void pageSelectionChanged(final ISelection selection) {
		if (!(selection instanceof IStructuredSelection)) {
			return;
		}

		final Object selectedObject = ((IStructuredSelection) selection).getFirstElement();

		// Reorder down is enabled for all categories, except linked categories that are not at the root level
		if ((selectedObject instanceof Category)) {
			category = (Category) selectedObject;
			setEnabled(canReorderCategoryDown(category));
		} else {
			setEnabled(false);
		}
	}

	@Override
	public void objectAdded(final String key, final Object object) {
		if (ChangeSetHelper.OBJECT_REG_ACTIVE_CHANGE_SET.equals(key)) {
			this.setEnabled(canReorderCategoryDown(this.category));
		}
	}

	@Override
	public void objectRemoved(final String key, final Object object) {
		if (ChangeSetHelper.OBJECT_REG_ACTIVE_CHANGE_SET.equals(key)) {
			this.setEnabled(canReorderCategoryDown(this.category));
		}
	}

	@Override
	public void objectUpdated(final String key,
							  final Object oldValue,
							  final Object newValue) {
		this.setEnabled(canReorderCategoryDown(this.category));
	}

	/**
	 * Determines whether the given category can be reordered downward.
	 *
	 * @param category the category which is to be reordered
	 * @return true if the category can be reordered down
	 */
	boolean canReorderCategoryDown(final Category category) {
		return canMoveCategory(category);
	}

	@Override
	public void run() {
		if (this.category == null) {
			LOG.error("ReorderCategoryDownAction should not be enabled"); //$NON-NLS-1$
			return;
		}
		LOG.debug("ReorderCategoryDownAction called."); //$NON-NLS-1$

		// Call category service to reorder category down
		getCategoryService().updateCategoryOrderDown(this.category);

		// Fire an event to refresh the browse list view
		final ItemChangeEvent<Category> event = new ItemChangeEvent<>(this, this.category, ItemChangeEvent.EventType.CHANGE);
		CatalogEventService.getInstance().notifyCategoryChanged(event);
	}

	@Override
	protected boolean isAuthorized() {
		if (this.category == null) {
			return false;
		}
		if (this.category.getCatalog() == null) {
			LOG.error("Category's catalog is null."); //$NON-NLS-1$
			return false;
		}
		AuthorizationService authorizationService = AuthorizationService.getInstance();
		if (category.isLinked()
				&& authorizationService.isAuthorizedWithPermission(CatalogPermissions.MANAGE_VIRTUAL_CATALOG_LINK_CATEGORY)
				&& authorizationService.isAuthorizedForCatalog(this.category.getCatalog())) {
			return true;
		} else if (!category.isLinked() && authorizationService.isAuthorizedWithPermission(CatalogPermissions.CATEGORY_MANAGE)
				&& authorizationService.isAuthorizedForCatalog(this.category.getCatalog())) {
			return true;
		}
		return false;
	}

	@Override
	public String getTargetIdentifier() {
		return "reorderCategoryDownAction"; //$NON-NLS-1$
	}
}
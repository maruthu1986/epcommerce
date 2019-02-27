/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.actions;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.elasticpath.cmclient.catalog.CatalogImageRegistry;
import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.core.EpUiException;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.core.registry.ObjectRegistryListener;
import com.elasticpath.cmclient.core.service.CatalogEventService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.domain.catalog.Category;

/**
 * This class is responsible for carrying out the action of including a linked-category.
 */
public class IncludeLinkedCategoryAction extends AbstractCatalogViewAction implements IRunnableWithProgress, ObjectRegistryListener {

	private static final String ACTION_NAME = "includeLinkedCategoryAction"; //$NON-NLS-1$

	private static final Logger LOG = Logger.getLogger(IncludeLinkedCategoryAction.class);

	private Category category;

	/**
	 * Constructs a create sub-category action.
	 */
	public IncludeLinkedCategoryAction() {
		super(CatalogMessages.get().CatalogBrowseView_Action_Include, CatalogImageRegistry.CATEGORY_INCLUDE);
		this.setEnabled(false);
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

		// Include is enabled only for linked categories that are not included already and their parent is not excluded
		if (selectedObject instanceof Category) {
			this.category = ((Category) selectedObject);
			setEnabled(isEditable(selectedObject));
		} else {
			setEnabled(false);
		}
	}

	/**
	 * IsEditable checks whether the the remove linked category button should be enabled.
	 *
	 * @param object is the object to check
	 * @return true if editable, false otherwise
	 */
	protected boolean isEditable(final Object object) {
		if (object == null) {
			return false;
		}
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
		if (this.category == null) {
			LOG.error("IncludeLinkedCategoryAction should be disabled."); //$NON-NLS-1$
			return;
		}

		LOG.debug("IncludeLinkedCategoryAction called."); //$NON-NLS-1$

		ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(null);
		try {
			progressDialog.run(true, false, this);
		} catch (InvocationTargetException | InterruptedException e) {
			LOG.error("Exception occurred on including a linked category.", e); //$NON-NLS-1$
			throw new EpUiException(e);
		}
		// Fire an event to refresh the browse list view
		final ItemChangeEvent<Category> event = new ItemChangeEvent<>(this, this.category, ItemChangeEvent.EventType.CHANGE);
		CatalogEventService.getInstance().notifyCategoryChanged(event);
	}

	@Override
	public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		if (this.category == null) {
			LOG.error("IncludeLinkedCategoryAction should be disabled."); //$NON-NLS-1$
			return;
		}
		monitor.beginTask(CatalogMessages.get().IncludeLinkedCategory_ProgressMessage, IProgressMonitor.UNKNOWN);

		// Call category service to include category
		getCategoryService().addLinkedCategoryProducts(this.category);

		monitor.done();
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
		return true;
	}

	@Override
	public String getTargetIdentifier() {
		return ACTION_NAME;
	}
}
/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.actions;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.catalog.CatalogImageRegistry;
import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.core.EpUiException;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.core.registry.ObjectRegistry;
import com.elasticpath.cmclient.core.registry.ObjectRegistryListener;
import com.elasticpath.cmclient.core.service.CatalogEventService;
import com.elasticpath.cmclient.core.ui.dialog.CategoryFinderDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.commons.exception.DuplicateKeyException;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;

/**
 * This class is responsible for carrying out the action of adding a linked category.
 */
public class AddLinkedCategoryAction extends AbstractCatalogViewAction implements IRunnableWithProgress, ObjectRegistryListener {

	private static final String ACTION_NAME = "addLinkedCategoryAction"; //$NON-NLS-1$

	private static final Logger LOG = Logger.getLogger(AddLinkedCategoryAction.class);

	private Category selectedCategory;

	private Catalog catalog;

	/**
	 * Constructs an add linked category action.
	 */
	public AddLinkedCategoryAction() {
		super(CatalogMessages.get().CatalogBrowseView_Action_AddLinkedCategory, CatalogImageRegistry.CATEGORY_LINKED_ADD);
		this.setEnabled(false);
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
			this.setEnabled(false);
			return;
		}

		final Object selectedObject = ((IStructuredSelection) selection).getFirstElement();
		if (!(selectedObject instanceof Catalog)) {
			setEnabled(false);
			return;
		}
		this.catalog = (Catalog) selectedObject;

		setEnabled(isEditable(selectedObject));
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
		if (this.catalog == null) {
			LOG.error("AddLinkedCategoryAction should be disabled"); //$NON-NLS-1$
			return;
		}

		final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

		LOG.debug("AddLinkedCategoryAction called."); //$NON-NLS-1$

		// Create and open a category finder dialog
		final CategoryFinderDialog categoryFinderDialog = new CategoryFinderDialog(shell, true);

		// Do not search for linked categories; can only link to non-linked categories
		categoryFinderDialog.setSearchedLinkedCategories(false);

		if (Window.OK == categoryFinderDialog.open()) {
			selectedCategory = (Category) categoryFinderDialog.getSelectedObject();
			ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(null);
			try {
				// to maintain a reference to the Display, use the UI thread, do not spawn a new one
				boolean spawn = false;
				progressDialog.run(spawn, false, this);
			} catch (InvocationTargetException | InterruptedException e) {
				LOG.error("Exception occurred on creating a linked category.", e); //$NON-NLS-1$
				throw new EpUiException(e);
			}

			// Fire an event to refresh the browse list view
			final ItemChangeEvent<Catalog> event = new ItemChangeEvent<>(this, catalog, ItemChangeEvent.EventType.ADD);
			CatalogEventService.getInstance().notifyCatalogChanged(event);
			final ItemChangeEvent<Category> linkedCategoryEvent = new ItemChangeEvent<>(this, selectedCategory,
					ItemChangeEvent.EventType.ADD);
			CatalogEventService.getInstance().notifyCategoryChanged(linkedCategoryEvent);
		}
	}

	@Override
	public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		monitor.beginTask(CatalogMessages.get().AddLinkedCategory_ProgressMessage, IProgressMonitor.UNKNOWN);

		try {
			// Call CategoryService to link the category (i.e. link all sub-categories and products)
			getCategoryService().addLinkedCategory(selectedCategory.getUidPk(), -1, catalog.getUidPk());

		} catch (DuplicateKeyException e) {
			final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			shell.getDisplay().syncExec(() -> MessageDialog.openError(shell, CatalogMessages.get().AddLinkedCategory_Error_DuplicateCode_DiagTitle,
					CatalogMessages.get().AddLinkedCategory_Error_DuplicateCode_DiagDesc));
		}

		monitor.done();
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
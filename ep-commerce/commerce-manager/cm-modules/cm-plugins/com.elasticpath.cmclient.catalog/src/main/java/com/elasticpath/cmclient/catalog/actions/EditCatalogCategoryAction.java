/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.dialogs.catalog.VirtualCatalogDialog;
import com.elasticpath.cmclient.catalog.editors.catalog.CatalogEditor;
import com.elasticpath.cmclient.catalog.editors.catalog.CatalogEditorInput;
import com.elasticpath.cmclient.catalog.editors.category.CategoryEditor;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.EpUiException;
import com.elasticpath.cmclient.core.editors.GuidEditorInput;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.EpUnsupportedOperationException;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.changeset.ChangeSetService;

/**
 * This class is responsible for carrying out the action of editing a catalog or category.
 */
public class EditCatalogCategoryAction extends AbstractCatalogViewAction {

	private static final Logger LOG = Logger.getLogger(EditCatalogCategoryAction.class);

	private Object catalogCategory;

	private final CatalogService catalogService = (CatalogService) ServiceLocator.getService(
			ContextIdNames.CATALOG_SERVICE);	
	
	/**
	 * Constructs a edit catalog or category action.
	 */
	public EditCatalogCategoryAction() {
		super(CatalogMessages.get().CatalogBrowseView_Action_OpenCatalogCategory, CoreImageRegistry.IMAGE_OPEN);
		this.setEnabled(false);
		addPolicyActionContainer("editCatalogCategoryAction"); //$NON-NLS-1$
	}

	@Override
	public String getTargetIdentifier() {
		return "editCatalogCategoryAction"; //$NON-NLS-1$
	}

	/**
	 * Listens to changes in the selection on the workbench page. Enable the edit action when a catalog or category is selected, else disable the
	 * action.
	 * 
	 * @param selection a representation of what has been selected
	 */
	@Override
	protected void pageSelectionChanged(final ISelection selection) {
		if (!(selection instanceof IStructuredSelection)) {
			this.setEnabled(false);
			return;
		}

		final IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		final Object selectedObject = structuredSelection.getFirstElement();

		if (selectedObject instanceof Category || selectedObject instanceof Catalog) {
			this.catalogCategory = selectedObject;
			this.setEnabled(canEditCategoryCatalog(catalogCategory));
		} else {
			this.setEnabled(false);
		}
	}
	
	/**
	 * Determines whether the given category or catalog can be edited.
	 * This implementation returns true.
	 * @param catalogCategory the catalog or category being edited
	 * @return true always.
	 */
	boolean canEditCategoryCatalog(final Object catalogCategory) {
		return true;
	}

	@Override
	public void run() {
		try {
			if (catalogCategory instanceof Catalog) {
				final Catalog selectedCatalog = (Catalog) catalogCategory;
				
				if (selectedCatalog.isMaster()) {
					final CatalogEditorInput editorInput = new CatalogEditorInput(selectedCatalog.getUidPk());
					final IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					workbenchPage.openEditor(editorInput, CatalogEditor.PART_ID);
				} else {					
					Catalog catalog = catalogService.findByCode(selectedCatalog.getCode());
					
					final VirtualCatalogDialog virtualCatalogDialog = new VirtualCatalogDialog();
					virtualCatalogDialog.setObjectGuid(catalog.getGuid());
					
					virtualCatalogDialog.open();
				}
			} else if (catalogCategory instanceof Category) {
				final Category selectedCategory = (Category) catalogCategory;
				
				ChangeSetService changeSetService = ServiceLocator.getService(ContextIdNames.CHANGESET_SERVICE);
				String categoryGuid = changeSetService.resolveObjectGuid(selectedCategory);
				
				final GuidEditorInput editorInput = new GuidEditorInput(categoryGuid, Category.class);
				final IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				workbenchPage.openEditor(editorInput, CategoryEditor.PART_ID);
			} else {
				// should never get here
				throw new EpUnsupportedOperationException("Cannot edit item that is neither a Catalog or Category."); //$NON-NLS-1$
			}
		} catch (final PartInitException e) {
			LOG.error(String.format("Could not edit <%1$S>", catalogCategory), e); //$NON-NLS-1$
			throw new EpUiException(String.format("Could not edit <%1$S>", catalogCategory), e); //$NON-NLS-1$
		}
	}

	@Override
	protected boolean isAuthorized() {
		return this.catalogCategory != null;
	}	
}
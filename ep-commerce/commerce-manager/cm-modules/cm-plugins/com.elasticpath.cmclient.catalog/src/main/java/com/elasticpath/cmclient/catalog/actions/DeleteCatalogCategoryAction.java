/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.editors.catalog.CatalogEditorInput;
import com.elasticpath.cmclient.catalog.editors.category.CategoryEditor;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.EpUiException;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.helpers.CategoryListener;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.core.helpers.ProductListener;
import com.elasticpath.cmclient.core.service.CatalogEventService;
import com.elasticpath.cmclient.core.ui.util.EditorUtil;
import com.elasticpath.common.pricing.service.PriceListAssignmentHelperService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.EpUnsupportedOperationException;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.search.SynonymGroup;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.catalog.CategoryTypeService;
import com.elasticpath.service.changeset.ChangeSetMemberAction;
import com.elasticpath.service.changeset.ChangeSetService;
import com.elasticpath.service.search.SynonymGroupService;
import com.elasticpath.service.store.StoreService;

/**
 * This class is responsible for carrying out the action of deleting a catalog or category.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity" })
public class DeleteCatalogCategoryAction extends AbstractCatalogViewAction implements ProductListener, CategoryListener {

	private final ChangeSetHelper changeSetHelper;
	private final CatalogService catalogService = ServiceLocator.getService(ContextIdNames.CATALOG_SERVICE);
	private final CategoryService categoryService;
	/**
	 * Constructs a edit catalog category action.
	 */
	public DeleteCatalogCategoryAction() {
		super(CatalogMessages.get().CatalogBrowseView_Action_DeleteCatalogCategory, CoreImageRegistry.IMAGE_REMOVE);
		CatalogEventService.getInstance().addProductListener(this);
		CatalogEventService.getInstance().addCategoryListener(this);
		this.changeSetHelper = getChangeSetHelper();
		this.categoryService = getCategoryService();
	}

	@Override
	protected void pageSelectionChanged(final ISelection selection) {
		this.applyStatePolicy(getStatePolicy());  // this code have to move to parent for changes of policy dependent object
	}

	/**
	 * Fired when the product in the ProductEditor is actually saved. Allows us to update our enabled status.
	 */
	@Override
	public void productChanged(final ItemChangeEvent<Product> event) {
		this.applyStatePolicy(getStatePolicy());
	}
	
	/**
	 * Fired when an existing product is added to a category (AddExistingProductAction).
	 */
	@Override
	public void categoryChanged(final ItemChangeEvent<Category> event) {
		this.applyStatePolicy(getStatePolicy());
	}
	
	/**
	 * Determines whether the given catalog is in use by a Store.
	 * @param catalog the catalog to check
	 * @return true if the given catalog is in use by a store.
	 */
	boolean isInUseByStore(final Catalog catalog) {
		final StoreService storeService = ServiceLocator.getService(
				ContextIdNames.STORE_SERVICE);
		final Collection<Long> catalogUids = new ArrayList<>();
		catalogUids.add(catalog.getUidPk());
		return !storeService.findStoresWithCatalogUids(catalogUids).isEmpty();
	}
	
	/**
	 * Determines whether the given catalog is in use by a CM User.
	 * @param catalog the catalog to check
	 * @return true if it's in use
	 */
	boolean isInUseByCmUser(final Catalog catalog) {
		return catalogService.catalogInUse(catalog.getUidPk());
	}
	
	@Override
	public void run() {
		final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		final IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		final Locale defaultLocale = CorePlugin.getDefault().getDefaultLocale();

		Object selectedObject = this.getDependentObject();
		
		if (selectedObject instanceof Catalog) {
			final Catalog selectedCatalog = (Catalog) selectedObject;
			
			if (!canCatalogBeDeleted(shell, workbenchPage, selectedCatalog)) {
				return;
			}

			final boolean confirmed = MessageDialog.openConfirm(shell, CatalogMessages.get().CatalogBrowseView_Action_DeleteCatalogDialogTitle,

					NLS.bind(CatalogMessages.get().CatalogBrowseView_Action_DeleteCatalogDialogText,
					selectedCatalog.getName()));

			if (confirmed) {
				deleteCatalog(selectedCatalog);
			}
		} else if (selectedObject instanceof Category) {
			final Category selectedCategory = (Category) selectedObject;
			
			if (!canCategoryBeDeleted(shell, workbenchPage, defaultLocale, selectedCategory)) {
				return;
			}

			final boolean confirmed = MessageDialog.openConfirm(shell, CatalogMessages.get().CatalogBrowseView_Action_DeleteCategoryDialogTitle,

					NLS.bind(CatalogMessages.get().CatalogBrowseView_Action_DeleteCategoryDialogText,
					selectedCategory.getDisplayName(defaultLocale)));

			if (confirmed) {
				deleteCategory(selectedCategory);
			}
		} else {
			// should never get here
			throw new EpUnsupportedOperationException("Cannot delete item that is neither a Catalog or Category."); //$NON-NLS-1$
		}
	}

	private void deleteCategory(final Category selectedCategory) {
		// also add to change set as a delete action
		changeSetHelper.addObjectToChangeSet(selectedCategory, ChangeSetMemberAction.DELETE);

		// Remove the Category Tree
		categoryService.removeCategoryTree(selectedCategory.getUidPk());

		// Fire an event to refresh the browse list view
		final ItemChangeEvent<Category> event = new ItemChangeEvent<>(this, selectedCategory, ItemChangeEvent.EventType.REMOVE);
		CatalogEventService.getInstance().notifyCategoryChanged(event);
	}

	private boolean canCategoryBeDeleted(final Shell shell, final IWorkbenchPage workbenchPage, final Locale defaultLocale,
			final Category selectedCategory) {
		// get the complex guid to find open category editor
		ChangeSetService changeSetService = ServiceLocator.getService(ContextIdNames.CHANGESET_SERVICE);
		String categoryGuid = changeSetService.resolveObjectGuid(selectedCategory);

		// Cannot delete a category if its editor is open
		for (IEditorReference editorRef : workbenchPage.getEditorReferences()) {
			try {
				if (EditorUtil.isSameEditor(editorRef, CategoryEditor.PART_ID)
						&& EditorUtil.isSameEntity(categoryGuid, editorRef)) {
					MessageDialog.openWarning(shell, CatalogMessages.get().CatalogBrowseView_Action_DeleteCategory,
						NLS.bind(CatalogMessages.get().CatalogBrowseView_Action_DeleteCategory_CloseEditor,
						new Object[]{selectedCategory.getCode(),
						selectedCategory.getDisplayName(defaultLocale)}));
					return false;
				}
			} catch (PartInitException e) {
				throw new EpUiException("Could not get category editor input", e); //$NON-NLS-1$
			}
		}
		
		return true;
	}

	private void deleteCatalog(final Catalog selectedCatalog) {
		// First, remove all CategoryTypes associated with this Catalog
		final CategoryTypeService categoryTypeService = ServiceLocator.getService(
				ContextIdNames.CATEGORY_TYPE_SERVICE);
		categoryTypeService.removeCategoryTypes(selectedCatalog.getUidPk());

		// Remove the Synonym Groups, which shouldn't be referenced elsewhere
		final SynonymGroupService synonymGroupService = ServiceLocator.getService(
				ContextIdNames.SYNONYM_GROUP_SERVICE);
		Collection <SynonymGroup> allSynonymGroups = synonymGroupService.findAllSynonymGroupForCatalog(selectedCatalog.getUidPk());
		for (SynonymGroup synGroup : allSynonymGroups) {
			synonymGroupService.remove(synGroup);
		}
		
		final PriceListAssignmentHelperService priceListAssignmentHelperService 
			= ServiceLocator.getService(ContextIdNames.PRICE_LIST_ASSIGNMENT_HELPER_SERVICE);
		
		priceListAssignmentHelperService.deletePriceListAssignmentsByCatalogCode(selectedCatalog.getCode());

		if (!selectedCatalog.isMaster()) {
			// also add to change set as a delete action (virtual catalogs only, master are not included in change sets)
			changeSetHelper.addObjectToChangeSet(selectedCatalog, ChangeSetMemberAction.DELETE);
		}

		// Then, remove the Catalog itself
		final CatalogService catalogService = ServiceLocator.getService(ContextIdNames.CATALOG_SERVICE);
		catalogService.remove(selectedCatalog);

		// Fire an event to refresh the browse list view
		final ItemChangeEvent<Catalog> event = new ItemChangeEvent<>(this, selectedCatalog, ItemChangeEvent.EventType.REMOVE);
		CatalogEventService.getInstance().notifyCatalogChanged(event);
	}

	private boolean canCatalogBeDeleted(final Shell shell, final IWorkbenchPage workbenchPage, final Catalog selectedCatalog) {
		// Cannot delete a catalog if its editor is open
		for (IEditorReference editorRef : workbenchPage.getEditorReferences()) {
			try {
				if (editorRef.getEditorInput() instanceof CatalogEditorInput
						&& (((CatalogEditorInput) editorRef.getEditorInput()).getCatalog().getUidPk() == selectedCatalog.getUidPk())) {
					MessageDialog.openWarning(shell, CatalogMessages.get().CatalogBrowseView_Action_DeleteCatalogErrorTitle,
						NLS.bind(CatalogMessages.get().CatalogBrowseView_Action_DeleteCatalog_CloseEditor,
						new Object[]{selectedCatalog.getName()}));
					return false;
				}
			} catch (PartInitException e) {
				throw new EpUiException("Could not get catalog editor input", e); //$NON-NLS-1$
			}
		}

		// Cannot delete a catalog if it is currently used by a Store
		if (isInUseByStore(selectedCatalog)) {
			MessageDialog.openWarning(shell, CatalogMessages.get().CatalogBrowseView_Action_DeleteCatalogErrorTitle,
				NLS.bind(CatalogMessages.get().CatalogBrowseView_Action_DeleteCatalog_InUseByStore,
				new Object[]{selectedCatalog.getName()}));
			return false;
		}
		// Cannot delete a catalog if it is currently used by a CM user
		if (isInUseByCmUser(selectedCatalog)) {
			MessageDialog.openWarning(shell, CatalogMessages.get().CatalogBrowseView_Action_DeleteCatalogErrorTitle,
				NLS.bind(CatalogMessages.get().CatalogBrowseView_Action_DeleteCatalog_InUse,
				new Object[]{selectedCatalog.getName()}));
			return false;
		}
		
		return true;
	}

	@Override
	protected boolean isAuthorized() {
		// fix for AbstractCatalogViewAction.setEnabled() method. Have to removed after refactoring policy
		return true;
	}

	@Override
	public String getTargetIdentifier() {
		return "deleteCatalogCategory"; //$NON-NLS-1$
	}

	@Override
	public void productSearchResultReturned(final SearchResultEvent<Product> event) {
		// No-op - only interested in productChanged
	}
	
	@Override
	public void categorySearchResultReturned(final SearchResultEvent<Category> event) {
		// No-op - only interested in categoryChanged
	}
}
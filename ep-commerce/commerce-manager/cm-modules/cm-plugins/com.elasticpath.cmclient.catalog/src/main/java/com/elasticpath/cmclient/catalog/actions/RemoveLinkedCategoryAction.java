/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.catalog.CatalogImageRegistry;
import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.editors.category.CategoryEditor;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.EpUiException;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.core.registry.ObjectRegistryListener;
import com.elasticpath.cmclient.core.service.CatalogEventService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.util.EditorUtil;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.changeset.ChangeSetObjectStatus;
import com.elasticpath.service.catalog.CategoryLookup;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.changeset.ChangeSetMemberAction;

/**
 * This class is responsible for carrying out the action of removing a linked category.
 */
@SuppressWarnings({ "PMD.PrematureDeclaration" })
public class RemoveLinkedCategoryAction extends AbstractCatalogViewAction implements IRunnableWithProgress, ObjectRegistryListener {

	private static final Logger LOG = Logger.getLogger(RemoveLinkedCategoryAction.class);

	private Category category;

	private final ChangeSetHelper changeSetHelper;

	/**
	 * Constructs a edit catalog category action.
	 */
	public RemoveLinkedCategoryAction() {
		super(CatalogMessages.get().CatalogBrowseView_Action_RemoveLinkedCategory, CatalogImageRegistry.CATEGORY_LINKED_REMOVE);
		this.changeSetHelper = getChangeSetHelper();
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
		
		if (selectedObject instanceof Category) {
			this.category = (Category) selectedObject;
			setEnabled(isEditable(selectedObject));
		} else {
			setEnabled(false);
		}
	}
	
	/**
	 * IsEditable checks whether the the remove linked category button should be enabled.
	 * @param object is the object to check
	 * @return true if editable, false otherwise
	 */
	protected boolean isEditable(final Object object) {
		boolean editable = false;
		
		StatePolicy statePolicy = getStatePolicy();
		
		if (statePolicy != null) {
			
			statePolicy.init(object);
			
			//Create sub-category is enabled only for Non-linked categories
			
			if (category != null) {
				editable = (EpState.EDITABLE == statePolicy.determineState(getDefaultContainer()));
			}
		}
		
		return editable;
	}


	@Override
	public void run() {
		if (category == null) {
			LOG.error("RemoveLinkedCategoryAction should be disabled"); //$NON-NLS-1$
			return;
		}
		final Locale defaultLocale = CorePlugin.getDefault().getDefaultLocale();

		final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

		List<Category> categoryAndAllChildren = getCategoryAndAllChildren(category);
		
		if (!checkIfDescendentsInOtherChangeSetsAndDisplayWarning(categoryAndAllChildren, defaultLocale)) {
			return;
		}
		
		if (!checkForOpenEditorsAndDisplayWarning(categoryAndAllChildren, defaultLocale)) {
			return;
		}

		if (confirmRemove(category, defaultLocale, shell)) {
			ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(null);
			try {
				//The first parameter "fork" must be false
				//it must be running in the same UI thread because it may need refresh some other editors, like change set editor.
				progressDialog.run(false, false, this);
			} catch (InvocationTargetException | InterruptedException e) {
				LOG.error("Exception occurred on deleting a linked category.", e); //$NON-NLS-1$
				throw new EpUiException(e);
			}

			// Fire an event to refresh the browse list view
			final ItemChangeEvent<Category> event = new ItemChangeEvent<>(this, this.category, ItemChangeEvent.EventType.REMOVE);
			CatalogEventService.getInstance().notifyCategoryChanged(event);
		}
	}

	@Override
	public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		if (this.category == null) {
			LOG.error("RemoveLinkedCategoryAction should be disabled."); //$NON-NLS-1$
			return;
		}
		final CategoryService categoryService = ServiceLocator.getService(
				ContextIdNames.CATEGORY_SERVICE);

		// Remove the Category Tree
		monitor.beginTask(CatalogMessages.get().RemoveLinkedCategory_ProgressMessage, IProgressMonitor.UNKNOWN);
		final List<Category> categoryAndAllChildren = getCategoryAndAllChildren(category);
		updateChangeSetStatusForChildren(categoryAndAllChildren);

		categoryService.removeLinkedCategoryTree(this.category);
		
		monitor.done();
	}


	private boolean confirmRemove(final Category linkedCategory, final Locale defaultLocale, final Shell shell) {
		return MessageDialog.openConfirm(shell, CatalogMessages.get().CatalogBrowseView_Action_RemoveLinkedCatDialogTitle,

				NLS.bind(CatalogMessages.get().CatalogBrowseView_Action_RemoveLinkedCatDialogText,
				linkedCategory.getDisplayName(defaultLocale)));
	}

	/**
	 * By default the category may not have it's children fully loaded, specifically the children's children may be null
	 * when they shouldn't be. This method fully loads the tree of sub-categories, including the supplied category,
	 * into a list.
	 *
	 * @param category the category to start from
	 * @return list of all sub-categories in tree rooted at supplied category, fully loaded.
	 */
	private List<Category> getCategoryAndAllChildren(final Category category) {
		List<Category> allChildren = new ArrayList<>();
		for (Category child : getCategoryLookup().findChildren(category)) {
			allChildren.addAll(getCategoryAndAllChildren(child));
		}
		allChildren.add(category);
		return allChildren;
	}
	
	/**
	 * Check to see if the supplied linkedCategory is open in an editor, and if it is, warn the user with a dialog.
	 *
	 * @param linkedCategories the categories to check
	 * @param defaultLocale for displaying category name in warning dialog.
	 * @return false if editor was open and warning displayed. True if no editor open.
	 */
	private boolean checkForOpenEditorsAndDisplayWarning(final List<Category> linkedCategories, final Locale defaultLocale) {
		for (Category linkedCategory : linkedCategories) {
			final IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			for (IEditorReference editorRef : workbenchPage.getEditorReferences()) {
				try {
					if (EditorUtil.isSameEditor(editorRef, CategoryEditor.PART_ID) 
							&& EditorUtil.isSameEntity(linkedCategory.getCompoundGuid(), editorRef)) {
						MessageDialog.openWarning(null,
								CatalogMessages.get().CatalogBrowseView_Action_RemoveLinkedCategory,

								NLS.bind(CatalogMessages.get().CatalogBrowseView_Action_DeleteCategory_CloseEditor,
								new Object[]{linkedCategory.getGuid(), linkedCategory.getDisplayName(defaultLocale)}));
								return false;
					}
				} catch (PartInitException e) {
					LOG.error(e.getStackTrace());
					throw new EpUiException("Could not get linked category editor input", e); //$NON-NLS-1$
				}
			}
		}
		return true;
		
	}
	
	/**
	 * Check if any of the linkedCategories in the supplied list are in a change set other than the active one,
	 * if they are, display a warning message.
	 *
	 * @param linkedCategories the list of categories to check
	 * @param defaultLocale locale for displaying category name in warning message.
	 * @return true if change sets disabled, or if none of the categories are in other changesets. False otherwise.
	 */
	private boolean checkIfDescendentsInOtherChangeSetsAndDisplayWarning(final List<Category> linkedCategories, final Locale defaultLocale) {
		if (!changeSetHelper.isChangeSetsEnabled()) {
			return true;
		}
		
		for (Category linkedCategory : linkedCategories) {
			ChangeSetObjectStatus status = changeSetHelper.getChangeSetObjectStatus(linkedCategory);
			if (status.isLocked() && !status.isMember(changeSetHelper.getActiveChangeSet().getGuid())) {
				MessageDialog.openWarning(null,
						CatalogMessages.get().CatalogBrowseView_Action_CanNotRemove,

						NLS.bind(CatalogMessages.get().CatalogBrowseView_Action_RemoveLinkedCategories,
						new Object[]{linkedCategory.getGuid(), linkedCategory.getDisplayName(defaultLocale)}));
				return false;
			}
		}
		
		return true;
	}

	/**
	 * Updates the list of supplied linkedCategories to be marked as deletes if they are
	 * in the currently active change set.
	 *
	 * @param linkedCategories the list of categories.
	 */
	private void updateChangeSetStatusForChildren(final List<Category> linkedCategories) {
		if (!changeSetHelper.isChangeSetsEnabled()) {
			return;
		}
		
		for (Category linkedCategory : linkedCategories) {
			if (!linkedCategory.isLinked()) {
				throw new IllegalArgumentException("Category(uidPk=" + linkedCategory.getUidPk() //$NON-NLS-1$  
						+ ") is not a linked category."); //$NON-NLS-1$ 
			}
			
			if (changeSetHelper.isMemberOfActiveChangeset(linkedCategory)) {
				changeSetHelper.addObjectToChangeSet(linkedCategory, ChangeSetMemberAction.DELETE);
			}
		}
	}
	
	@Override
	protected boolean isAuthorized() {
		if (this.category == null) {
			return false;
		}
		if (this.category.getCatalog() == null) {
			LOG.error("Category's Catalog is null."); //$NON-NLS-1$
			return false;
		}
		
		return true;
	}

	@Override
	public String getTargetIdentifier() {
		return "removeLinkedCategoryAction"; //$NON-NLS-1$
	}

	protected CategoryLookup getCategoryLookup() {
		return ServiceLocator.getService(ContextIdNames.CATEGORY_LOOKUP);
	}
}
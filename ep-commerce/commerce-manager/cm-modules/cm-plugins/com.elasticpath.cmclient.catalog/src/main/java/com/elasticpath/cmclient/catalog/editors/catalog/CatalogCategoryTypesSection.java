/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.editors.catalog;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.progress.IProgressService;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.dialogs.categorytype.CategoryTypeDialog;
import com.elasticpath.cmclient.catalog.editors.catalog.tablelabelprovider.CatalogCategoryTypeTableLabelProviderDecorator;
import com.elasticpath.cmclient.catalog.editors.catalog.tablelabelprovider.ChangeSetTableLabelProviderDecorator;
import com.elasticpath.cmclient.catalog.editors.catalog.tablelabelprovider.TableLabelProviderAdapter;
import com.elasticpath.cmclient.catalog.editors.model.CatalogModel;
import com.elasticpath.cmclient.core.EpUiException;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.ItemChangeEvent.EventType;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.core.service.CatalogEventService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEpTableSection;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.service.catalog.CategoryTypeService;
import com.elasticpath.service.changeset.ChangeSetMemberAction;

/**
 * Implements a section of the <code>CatalogCategoryPage</code> providing {@link CategoryType}s provided within the catalog.
 */
public class CatalogCategoryTypesSection extends AbstractPolicyAwareEpTableSection<CategoryType> {

	private static final int NAME_COLUMN_WIDTH = 140;
	private static final String CATALOG_CATEGORY_TYPES_TABLE = "Catalog Category Types"; //$NON-NLS-1$

	private final CategoryTypeService categoryTypeService = ServiceLocator.getService(ContextIdNames.CATEGORY_TYPE_SERVICE);
	private final ChangeSetHelper changeSetHelper = ServiceLocator.getService(ChangeSetHelper.BEAN_ID);

	private final ChangeSetColumnDecorator changeSetColumnDecorator = new ChangeSetColumnDecorator();
	private List<CategoryType> catalogCategoryTypes;

	/**
	 * Default constructor.
	 *
	 * @param formPage the form page
	 * @param editor the editor
	 */
	public CatalogCategoryTypesSection(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor);
	}

	@Override
	protected String getSectionType() {
		return CatalogMessages.get().CatalogCategoryTypes_ButtonText;
	}

	@Override
	protected void initializeTable(final IEpTableViewer table) {
		table.getSwtTableViewer().setComparator(new Comparator());

		addColumns(table);

		getViewer().setContentProvider(new TableContentProvider());

		addLabelProvider();

		final List<CategoryType> readOnlyCategoryTypeList = categoryTypeService.findAllCategoryTypeFromCatalog(getModel().getCatalog().getUidPk());
		catalogCategoryTypes = new ArrayList<>(readOnlyCategoryTypeList.size());
		catalogCategoryTypes.addAll(readOnlyCategoryTypeList);

		refreshViewerInput();
	}

	private void addColumns(final IEpTableViewer table) {
		if (changeSetColumnDecorator.isDecoratable()) {
			changeSetColumnDecorator.addLockColumn(table);
			changeSetColumnDecorator.addActionColumn(table);
		}

		table.addTableColumn(CatalogMessages.get().CatalogCategoryTypesSection_TableNameColumn, NAME_COLUMN_WIDTH);
	}

	private void addLabelProvider() {
		if (changeSetColumnDecorator.isDecoratable()) {
			getViewer().setLabelProvider(new CatalogCategoryTypeTableLabelProviderDecorator(
					new ChangeSetTableLabelProviderDecorator<>(
							new TableLabelProviderAdapter(), getModel().getCategoryTypeTableItems())));
		} else {
			getViewer().setLabelProvider(new CatalogCategoryTypeTableLabelProviderDecorator(new TableLabelProviderAdapter()));
		}
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		// nothing to bind
	}

	@Override
	protected EpState getEditorTableState() {
		return getStatePolicy().determineState(getEpTableSectionControlPane());
	}

	@Override
	public void commit(final boolean onSave) {
		if (onSave) {
			final Set<CategoryType> additionCategoryTypes = getAddedItems();
			for (final CategoryType categoryType : additionCategoryTypes) {
				categoryTypeService.add(categoryType);
				changeSetHelper.addObjectToChangeSet(categoryType, ChangeSetMemberAction.ADD);
				CatalogEventService.getInstance().notifyCategoryTypeChanged(new ItemChangeEvent<>(this, categoryType, EventType.ADD));
			}
			additionCategoryTypes.clear();

			final Set<CategoryType> modifiedCategoryTypes = getModifiedItems();
			for (final CategoryType categoryType : modifiedCategoryTypes) {
				categoryTypeService.update(categoryType);
				changeSetHelper.addObjectToChangeSet(categoryType, ChangeSetMemberAction.EDIT);
				CatalogEventService.getInstance().notifyCategoryTypeChanged(new ItemChangeEvent<>(this, categoryType, EventType.CHANGE));
			}
			modifiedCategoryTypes.clear();

			final Set<CategoryType> removedCategoryTypes = getRemovedItems();
			for (final CategoryType categoryType : removedCategoryTypes) {
				if (categoryType.isPersisted()) {
					changeSetHelper.addObjectToChangeSet(categoryType, ChangeSetMemberAction.DELETE);
					categoryTypeService.remove(categoryType);
					CatalogEventService.getInstance().notifyCategoryTypeChanged(
							new ItemChangeEvent<>(this, categoryType, EventType.REMOVE));
				}
			}
			removedCategoryTypes.clear();
			refreshViewerInput();
			super.commit(onSave);
		}
	}

	@Override
	public CatalogModel getModel() {
		return (CatalogModel) super.getModel();
	}

	@Override
	protected String getRemoveDialogTitle() {
		return CatalogMessages.get().CatalogCategoryTypesSection_RemoveDialog_title;
	}

	@Override
	protected String getRemoveDialogDescription(final CategoryType item) {
		return
			NLS.bind(CatalogMessages.get().CatalogCategoryTypesSection_RemoveDialog_description,
			getItemName(item));
	}

	@Override
	protected CategoryType addItemAction() {
		final CategoryTypeDialog dialog = new CategoryTypeDialog(null, getModel());
		if (dialog.open() == Window.OK) {
			final CategoryType categoryType = dialog.getCategoryType();
			categoryType.setCatalog(getModel().getCatalog());
			return categoryType;
		}
		return null;
	}

	@Override
	protected boolean editItemAction(final CategoryType object) {
		final CategoryTypeDialog dialog = new CategoryTypeDialog(object, getModel());
		return dialog.open() == Window.OK;
	}

	@Override
	protected boolean removeItemAction(final CategoryType object) {
		final ParameterPasser passer = new ParameterPasser();
		passer.canRemove = false;
		final IProgressService progressService = PlatformUI.getWorkbench().getProgressService();
		try {
			// we can use the passer variable here because this is a blocking job
			progressService.busyCursorWhile(new IRunnableWithProgress() {
				@Override
				public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					if (categoryTypeService.isInUse(object.getUidPk())) {
						passer.canRemove = false;
					} else {
						passer.canRemove = true;
						updateChangeSetListsOnRemove(object);
					}
				}

				private void updateChangeSetListsOnRemove(final CategoryType object) {
					final Set<CategoryType> additionList = getAddedItems();
					if (isNewObjectAndInList(object, additionList)) {
						additionList.remove(object);
						catalogCategoryTypes.remove(object);
					} else {
						getModel().getCategoryTypeTableItems().addRemovedItem(object);
					}
				}
				private boolean isNewObjectAndInList(final CategoryType object, final Set<CategoryType> list) {
					return list.contains(object) && !object.isPersisted();
				}
			});
		} catch (final InvocationTargetException | InterruptedException e) {
			throw new EpUiException("Error removing: " + e.getMessage(), e); //$NON-NLS-1$
		}

		if (!passer.canRemove) {
			MessageDialog.openError(getPage().getSite().getShell(), CatalogMessages.get().CatalogCategoryTypesSection_ErrorDialog_InUse_title,

					NLS.bind(CatalogMessages.get().CatalogCategoryTypesSection_ErrorDialog_InUse_desc,
					getItemName(object)));
		}
		return passer.canRemove;
	}


	@Override
	protected String getItemName(final CategoryType categoryType) {
		return String.format("%1$s", categoryType.getName()); //$NON-NLS-1$
	}

	@Override
	protected void addAddedItem(final CategoryType item) {
		super.addAddedItem(item);
		catalogCategoryTypes.add(item);
		markDirty();
	}

	@Override
	protected void addModifiedItem(final CategoryType item) {
		super.addModifiedItem(item);
		markDirty();
	}

	@Override
	protected void addRemovedItem(final CategoryType item) {
		super.addRemovedItem(item);
		if (!changeSetHelper.isChangeSetsEnabled()) {
			catalogCategoryTypes.remove(item);
		}
		markDirty();
	}

	/**
	 * Content provider for the table.
	 */
	private class TableContentProvider implements IStructuredContentProvider {

		@Override
		public Object[] getElements(final Object inputElement) {
			return catalogCategoryTypes.toArray();
		}

		@Override
		public void dispose() {
			// not used
		}

		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
			// not used
		}
	}

	/**
	 * Parameter holder class for passing a parameter between threads.
	 */
	private final class ParameterPasser {
		private boolean canRemove;
	}

	/**
	 * Category types specific sorter to sort on the name.
	 */
	private class Comparator extends ViewerComparator {
		@Override
		public int compare(final Viewer viewer, final Object object1, final Object object2) {
			final CategoryType categoryType1 = (CategoryType) object1;
			final CategoryType categoryType2 = (CategoryType) object2;
			return categoryType1.getName().compareToIgnoreCase(categoryType2.getName());
		}
	}

	@Override
	public void selectionChanged(final SelectionChangedEvent event) {
		final CategoryType categoryType = (CategoryType) ((IStructuredSelection) getViewer().getSelection()).getFirstElement();

		applyStatePolicyForCategoryType(getStatePolicy(), categoryType);
	}

	private void applyStatePolicyForCategoryType(final StatePolicy statePolicy, final CategoryType categoryType) {
		setStatePolicy(statePolicy);
		updatePoliciesWithDependentObject(categoryType);
		applyStatePolicy();
	}

	@Override
	public String getTargetIdentifier() {
		return "catalogCategoryTypesSection"; //$NON-NLS-1$
	}

	@Override
	public void refresh() {
		// do nothing
	}

	@Override
	protected String getTableName() {
		return CATALOG_CATEGORY_TYPES_TABLE;
	}
}

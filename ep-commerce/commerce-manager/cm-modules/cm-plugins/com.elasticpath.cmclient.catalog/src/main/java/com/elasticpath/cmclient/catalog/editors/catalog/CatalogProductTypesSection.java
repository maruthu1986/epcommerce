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
import com.elasticpath.cmclient.catalog.editors.catalog.tablelabelprovider.CatalogProductTypeTableLabelProviderDecorator;
import com.elasticpath.cmclient.catalog.editors.catalog.tablelabelprovider.ChangeSetTableLabelProviderDecorator;
import com.elasticpath.cmclient.catalog.editors.catalog.tablelabelprovider.TableLabelProviderAdapter;
import com.elasticpath.cmclient.catalog.editors.model.CatalogModel;
import com.elasticpath.cmclient.catalog.wizards.product.ProductTypeAddEditWizard;
import com.elasticpath.cmclient.core.EpUiException;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.ItemChangeEvent.EventType;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.core.service.CatalogEventService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.wizard.EpWizardDialog;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEpTableSection;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.service.catalog.ProductTypeService;
import com.elasticpath.service.changeset.ChangeSetMemberAction;

/**
 * Implements a section of the <code>CatalogCategoryPage</code> providing {@link ProductType}s within a catalog.
 */
public class CatalogProductTypesSection extends AbstractPolicyAwareEpTableSection<ProductType> {

	private static final String CATALOG_PRODUCT_TYPES_TABLE = "Catlog Product Types"; //$NON-NLS-1$
	private final ProductTypeService productTypeService = ServiceLocator.getService(ContextIdNames.PRODUCT_TYPE_SERVICE);
	private final ChangeSetHelper changeSetHelper = ServiceLocator.getService(ChangeSetHelper.BEAN_ID);

	private List<ProductType> catalogProductTypes;
	private ProductTypeAddEditWizard addEditWizard;

	private final ChangeSetColumnDecorator changeSetColumnDecorator = new ChangeSetColumnDecorator();

	/**
	 * Default constructor.
	 *
	 * @param formPage the form page
	 * @param editor the editor
	 */
	public CatalogProductTypesSection(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor);
	}

	@Override
	protected String getSectionType() {
		return CatalogMessages.get().CatalogProductTypesSection_ButtonText;
	}

	@Override
	protected void initializeTable(final IEpTableViewer table) {
		table.getSwtTableViewer().setComparator(new Comparator());

		addColumns(table);

		getViewer().setContentProvider(new TableContentProvider());

		addLabelProvider();

		final List<ProductType> readOnlyProductTypeList = productTypeService.findAllProductTypeFromCatalog(getModel().getCatalog().getUidPk());
		catalogProductTypes = new ArrayList<>(readOnlyProductTypeList.size());
		catalogProductTypes.addAll(readOnlyProductTypeList);

		refreshViewerInput();
	}

	private void addColumns(final IEpTableViewer table) {
		if (changeSetColumnDecorator.isDecoratable()) {
			changeSetColumnDecorator.addLockColumn(table);
			changeSetColumnDecorator.addActionColumn(table);
		}

		final String[] columnNames = {CatalogMessages.get().CatalogProductTypesSection_TableNameColumn,
				CatalogMessages.get().CatalogProductTypesSection_TableMultipleSkusColumn};
		final int[] columnWidths = {140, 100};

		for (int i = 0; i < columnNames.length; ++i) {
			table.addTableColumn(columnNames[i], columnWidths[i]);
		}
	}

	private void addLabelProvider() {
		if (changeSetColumnDecorator.isDecoratable()) {
			getViewer().setLabelProvider(
					new CatalogProductTypeTableLabelProviderDecorator(new ChangeSetTableLabelProviderDecorator<>(
							new TableLabelProviderAdapter(), getModel().getProductTypeTableItems())));
		} else {
			getViewer().setLabelProvider(new CatalogProductTypeTableLabelProviderDecorator(new TableLabelProviderAdapter()));
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
			final Set<ProductType> additionProductTypes = getAddedItems();
			for (final ProductType productType : additionProductTypes) {
				productTypeService.add(productType);
				changeSetHelper.addObjectToChangeSet(productType, ChangeSetMemberAction.ADD);
				CatalogEventService.getInstance().notifyProductTypeChanged(new ItemChangeEvent<>(this, productType, EventType.ADD));
			}
			additionProductTypes.clear();

			final Set<ProductType> modifiedProductTypes = getModifiedItems();
			for (final ProductType productType : modifiedProductTypes) {
				productTypeService.update(productType);
				changeSetHelper.addObjectToChangeSet(productType, ChangeSetMemberAction.EDIT);
				CatalogEventService.getInstance().notifyProductTypeChanged(new ItemChangeEvent<>(this, productType, EventType.CHANGE));
			}
			modifiedProductTypes.clear();

			final Set<ProductType> removedProductTypes = getRemovedItems();
			for (final ProductType productType : removedProductTypes) {
				if (productType.isPersisted()) {
					changeSetHelper.addObjectToChangeSet(productType, ChangeSetMemberAction.DELETE);
					productTypeService.remove(productType);
					CatalogEventService.getInstance().notifyProductTypeChanged(new ItemChangeEvent<>(this, productType, EventType.REMOVE));
				}
			}
			removedProductTypes.clear();
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
		return CatalogMessages.get().CatalogProductTypesSection_RemoveDialog_title;
	}

	@Override
	protected String getRemoveDialogDescription(final ProductType item) {
		return
			NLS.bind(CatalogMessages.get().CatalogProductTypesSection_RemoveDialog_description,
			getItemName(item));
	}

	@Override
	protected ProductType addItemAction() {
		final ProductTypeAddEditWizard wizard = getAddEditWizard(null);
		final EpWizardDialog dialog = new EpWizardDialog(getEditor().getEditorSite().getShell(), wizard);
		if (dialog.open() == Window.OK) {
			final ProductType productType = wizard.getProductType();
			productType.setCatalog(getModel().getCatalog());
			return productType;
		}
		return null;
	}

	@Override
	protected boolean editItemAction(final ProductType object) {
		final ProductTypeAddEditWizard wizard = getAddEditWizard(object);
		final EpWizardDialog dialog = new EpWizardDialog(getEditor().getEditorSite().getShell(), wizard);
		return dialog.open() == Window.OK;
	}

	@Override
	protected boolean removeItemAction(final ProductType object) {
		final ParameterPasser passer = new ParameterPasser();
		passer.canRemove = false;
		final IProgressService progressService = PlatformUI.getWorkbench().getProgressService();
		try {
			// we can use the passer variable here because this is a blocking job
			progressService.busyCursorWhile(new IRunnableWithProgress() {
				@Override
				public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					if (productTypeService.isInUse(object.getUidPk())) {
						passer.canRemove = false;
					} else {
						passer.canRemove = true;
						updateChangeSetListsOnRemove(object);
					}
				}

				private void updateChangeSetListsOnRemove(final ProductType object) {
					final Set<ProductType> additionList = getAddedItems();
					if (isNewObjectAndInList(object, additionList)) {
						additionList.remove(object);
						catalogProductTypes.remove(object);
					} else {
						getModel().getProductTypeTableItems().addRemovedItem(object);
					}
				}

				private boolean isNewObjectAndInList(final ProductType object, final Set<ProductType> list) {
					return list.contains(object) && !object.isPersisted();
				}

			});
		} catch (final InvocationTargetException | InterruptedException e) {
			throw new EpUiException("Error removing: " + e.getMessage(), e); //$NON-NLS-1$
		}

		if (!passer.canRemove) {
			MessageDialog.openError(getPage().getSite().getShell(), CatalogMessages.get().CatalogProductTypesSection_ErrorDialog_InUse_title,

					NLS.bind(CatalogMessages.get().CatalogProductTypesSection_ErrorDialog_InUse_desc,
					getItemName(object)));
		}
		return passer.canRemove;
	}

	@Override
	protected String getItemName(final ProductType productType) {
		return String.format("%1$s", productType.getName()); //$NON-NLS-1$
	}

	@Override
	protected void addAddedItem(final ProductType item) {
		super.addAddedItem(item);
		catalogProductTypes.add(item);
		markDirty();
	}

	@Override
	protected void addModifiedItem(final ProductType item) {
		super.addModifiedItem(item);
		markDirty();
	}

	@Override
	protected void addRemovedItem(final ProductType item) {
		super.addRemovedItem(item);
		if (!changeSetHelper.isChangeSetsEnabled()) {
			catalogProductTypes.remove(item);
		}
		markDirty();
	}

	private ProductTypeAddEditWizard getAddEditWizard(final ProductType productType) {
		if (addEditWizard == null) {
			addEditWizard = new ProductTypeAddEditWizard(productType, getModel(), catalogProductTypes);
		}
		return new ProductTypeAddEditWizard(productType, getModel(), catalogProductTypes);
	}

	/**
	 * Content provider for the table.
	 */
	private class TableContentProvider implements IStructuredContentProvider {

		@Override
		public Object[] getElements(final Object inputElement) {
			return catalogProductTypes.toArray();
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
	 * Product types specific sorter to sort on the name.
	 */
	private class Comparator extends ViewerComparator {

		@Override
		public int compare(final Viewer viewer, final Object object1, final Object object2) {
			final ProductType productType1 = (ProductType) object1;
			final ProductType productType2 = (ProductType) object2;
			return productType1.getName().compareToIgnoreCase(productType2.getName());
		}
	}

	@Override
	public void selectionChanged(final SelectionChangedEvent event) {
		final ProductType productType = (ProductType) ((IStructuredSelection) getViewer().getSelection()).getFirstElement();

		applyStatePolicyForProductType(getStatePolicy(), productType);
	}

	private void applyStatePolicyForProductType(final StatePolicy statePolicy, final ProductType productType) {
		setStatePolicy(statePolicy);
		updatePoliciesWithDependentObject(productType);
		applyStatePolicy();
	}

	@Override
	public String getTargetIdentifier() {
		return "catalogProductTypesSection"; //$NON-NLS-1$
	}

	@Override
	public void refresh() {
		// do nothing
	}

	@Override
	protected String getTableName() {
		return CATALOG_PRODUCT_TYPES_TABLE;
	}
}

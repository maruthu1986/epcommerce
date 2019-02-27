/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.editors.catalog;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.progress.IProgressService;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.dialogs.catalog.CatalogAttributesAddEditDialog;
import com.elasticpath.cmclient.catalog.editors.catalog.tablelabelprovider.CatalogAttributeTableLabelProviderDecorator;
import com.elasticpath.cmclient.catalog.editors.catalog.tablelabelprovider.ChangeSetTableLabelProviderDecorator;
import com.elasticpath.cmclient.catalog.editors.catalog.tablelabelprovider.TableLabelProviderAdapter;
import com.elasticpath.cmclient.catalog.editors.model.CatalogModel;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.EpUiException;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.comparator.AttributeViewerComparatorByNameIgnoreCase;
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
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeUsage;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.service.attribute.AttributeService;
import com.elasticpath.service.changeset.ChangeSetMemberAction;

/**
 * Implements a section of the <code>CatalogAttributesPage</code> providing {@link Attribute}s provided within the catalog.
 */
@SuppressWarnings({ "PMD.GodClass" })
public class CatalogAttributesSection extends AbstractPolicyAwareEpTableSection<Attribute> {

	private static final int HEIGHT_HINT = 100;
	private static final String CATALOG_ATTRIBUTES_TABLE = "Catalog Attributes"; //$NON-NLS-1$

	private final ChangeSetHelper changeSetHelper = ServiceLocator.getService(ChangeSetHelper.BEAN_ID);

	private final ChangeSetColumnDecorator changeSetColumnDecorator = new ChangeSetColumnDecorator();

	private final AttributeService attributeService;

	private List<Attribute> catalogAttributes;

	/**
	 * Parameter holder class for passing a parameter between threads.
	 */
	private final class ParameterPasser {
		private boolean canRemove;
	}

	/**
	 * Content provider for the table.
	 */
	private class TableContentProvider implements IStructuredContentProvider {

		@Override
		public void dispose() {
			// not used
		}

		@Override
		public Object[] getElements(final Object inputElement) {
			return catalogAttributes.toArray();
		}

		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
			// not used
		}
	}

	/**
	 * Default constructor.
	 *
	 * @param formPage the form page
	 * @param editor the editor
	 */
	public CatalogAttributesSection(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor);
		attributeService = ServiceLocator.getService(ContextIdNames.ATTRIBUTE_SERVICE);
	}

	@Override
	protected void addAddedItem(final Attribute item) {
		super.addAddedItem(item);
		catalogAttributes.add(item);
		markDirty();
	}

	@Override
	protected Attribute addItemAction() {
		final CatalogAttributesAddEditDialog dialog = new CatalogAttributesAddEditDialog(getSection().getShell(), null, false,
				getModel());

		if (dialog.open() == Window.OK) {
			final Attribute attribute = dialog.getAttribute();
			final Catalog catalogFromModel = getModel().getCatalog();
			attribute.setCatalog(catalogFromModel);
			return attribute;
		}
		return null;
	}

	@Override
	protected void addModifiedItem(final Attribute item) {
		super.addModifiedItem(item);
		markDirty();
	}

	@Override
	protected void addRemovedItem(final Attribute item) {
		super.addRemovedItem(item);
		if (!changeSetHelper.isChangeSetsEnabled()) {
			catalogAttributes.remove(item);
		}
		markDirty();
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		// nothing to bind
	}

	@Override
	public void commit(final boolean onSave) {
		if (onSave) {
			final Set<Attribute> additionAttributes = getAddedItems();
			for (final Attribute attribute : additionAttributes) {
				attributeService.add(attribute);
				changeSetHelper.addObjectToChangeSet(attribute, ChangeSetMemberAction.ADD);
				CatalogEventService.getInstance().notifyAttributeChanged(new ItemChangeEvent<>(this, attribute, EventType.ADD));
			}
			additionAttributes.clear();

			final Set<Attribute> modifiedAttributes = getModifiedItems();
			for (final Attribute attribute : modifiedAttributes) {
				attributeService.update(attribute);
				changeSetHelper.addObjectToChangeSet(attribute, ChangeSetMemberAction.EDIT);
				CatalogEventService.getInstance().notifyAttributeChanged(new ItemChangeEvent<>(this, attribute, EventType.CHANGE));
			}
			modifiedAttributes.clear();

			final Set<Attribute> deletedAttributes = getRemovedItems();
			for (final Attribute attribute : deletedAttributes) {
				if (attribute.isPersisted()) {
					changeSetHelper.addObjectToChangeSet(attribute, ChangeSetMemberAction.DELETE);
					attributeService.remove(attribute);
					CatalogEventService.getInstance().notifyAttributeChanged(new ItemChangeEvent<>(this, attribute, EventType.REMOVE));
				}
			}
			deletedAttributes.clear();
			refreshViewerInput();
			super.commit(onSave);
		}
	}

	@Override
	public void doubleClick(final DoubleClickEvent event) {
		final Attribute element = (Attribute) ((IStructuredSelection) event.getSelection()).getFirstElement();
		getEpTableSectionControlPane().setPolicyDependent(element);
		if (element.isGlobal() || getEditorTableState() != EpState.EDITABLE) {
			return;
		}
		if (editItemAction(element)) {
			addModifiedItem(element);
			refreshViewerInput();
		}

	}

	@Override
	protected boolean editItemAction(final Attribute object) {
		final CatalogAttributesAddEditDialog dialog = new CatalogAttributesAddEditDialog(getSection().getShell(), object, false,
				getModel());
		return dialog.open() == Window.OK;
	}

	@Override
	protected EpState getEditorTableState() {
		return getStatePolicy().determineState(getEpTableSectionControlPane());
	}

	@Override
	protected String getItemName(final Attribute attribute) {
		return String.format("%1$s - %2$s - %3$s", //$NON-NLS-1$
				attribute.getKey(), attribute.getName(), CoreMessages.get().getMessage(attribute.getAttributeType().getNameMessageKey()));
	}

	@Override
	public CatalogModel getModel() {
		return (CatalogModel) super.getModel();
	}

	@Override
	protected String getRemoveDialogDescription(final Attribute item) {
		return
			NLS.bind(CatalogMessages.get().CatalogAttributesSection_RemoveDialog_description,
			getItemName(item));
	}

	@Override
	protected String getRemoveDialogTitle() {
		return CatalogMessages.get().CatalogAttributesSection_RemoveDialog_title;
	}

	@Override
	protected String getSectionType() {
		return CatalogMessages.get().CatalogAttributesSection_ButtonText;
	}

	@Override
	protected void initializeTable(final IEpTableViewer table) {
		table.getSwtTableViewer().setComparator(new AttributeViewerComparatorByNameIgnoreCase());

		addColumns(table);

		getViewer().setContentProvider(new TableContentProvider());

		addLabelProvider();

		final Collection<Integer> catalogUsageTypes = getCatalogAttributeUsageTypes();
		final Catalog catalogFromModel = getModel().getCatalog();
		final Collection<Attribute> readOnlyCatalogAttributes = attributeService.findAllCatalogAndGlobalAttributesByType(
				catalogFromModel.getUidPk(), catalogUsageTypes);
		catalogAttributes = new ArrayList<>(readOnlyCatalogAttributes.size());
		catalogAttributes.addAll(readOnlyCatalogAttributes);

		refreshViewerInput();
	}

	private void addColumns(final IEpTableViewer table) {
		if (changeSetColumnDecorator.isDecoratable()) {
			changeSetColumnDecorator.addLockColumn(table);
			changeSetColumnDecorator.addActionColumn(table);
		}

		final String[] columnNames = { CatalogMessages.get().CatalogAttributesSection_TableAttributeKey,
				CatalogMessages.get().CatalogAttributesSection_TableAttributeName,
				CatalogMessages.get().CatalogAttributesSection_TableAttributeType,
				CatalogMessages.get().CatalogAttributesSection_TableAttributeUsage,
				CatalogMessages.get().CatalogAttributesSection_TableAttributeRequired,
				CatalogMessages.get().CatalogAttributesSection_TableAttributeGlobal };
		final int[] columnWidths = { 120, 180, 120, 80, 80, 80 };

		for (int i = 0; i < columnNames.length; ++i) {
			table.addTableColumn(columnNames[i], columnWidths[i]);
		}
	}

	private void addLabelProvider() {
		if (changeSetColumnDecorator.isDecoratable()) {
			getViewer().setLabelProvider(new CatalogAttributeTableLabelProviderDecorator(
					new ChangeSetTableLabelProviderDecorator<>(
							new TableLabelProviderAdapter(), getModel().getAttributeTableItems())));
		} else {
			getViewer().setLabelProvider(new CatalogAttributeTableLabelProviderDecorator(new TableLabelProviderAdapter()));
		}
	}

	/**
	 * Gets the catalog related attribute usage types.
	 *
	 * @return a collection of usage types
	 */
	protected Collection<Integer> getCatalogAttributeUsageTypes() {
		return Arrays.asList(AttributeUsage.CATEGORY, AttributeUsage.PRODUCT, AttributeUsage.SKU);
	}

	@Override
	protected boolean removeItemAction(final Attribute object) {
		final ParameterPasser passer = new ParameterPasser();
		passer.canRemove = false;
		final IProgressService progressService = PlatformUI.getWorkbench().getProgressService();
		try {
			// we can use the passer variable here because this is a blocking job
			progressService.busyCursorWhile(new IRunnableWithProgress() {
				@Override
				public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					if (isInUse(object.getUidPk())) {
						passer.canRemove = false;
					} else {
						passer.canRemove = true;
						updateChangeSetListsOnRemove(object);
					}
				}

				private void updateChangeSetListsOnRemove(final Attribute object) {
					final Set<Attribute> additionList = getAddedItems();
					if (isNewObjectAndInList(object, additionList)) {
						additionList.remove(object);
						catalogAttributes.remove(object);
					} else {
						getModel().getAttributeTableItems().addRemovedItem(object);
					}
				}
				private boolean isNewObjectAndInList(final Attribute object, final Set<Attribute> list) {
					return list.contains(object) && !object.isPersisted();
				}
			});
		} catch (final InvocationTargetException | InterruptedException e) {
			throw new EpUiException("Error removing: " + e.getMessage(), e); //$NON-NLS-1$
		}

		if (!passer.canRemove) {
			MessageDialog.openError(getPage().getSite().getShell(), CatalogMessages.get().CatalogAttributesSection_ErrorDialog_InUse_title,

					NLS.bind(CatalogMessages.get().CatalogAttributesSection_ErrorDialog_InUse_desc,
					getItemName(object)));
		}

		return passer.canRemove;
	}

	private boolean isInUse(final long uidPk) {
		for (final Long attribute : attributeService.getAttributeInUseUidList()) {
			if (attribute == uidPk) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void selectionChanged(final SelectionChangedEvent event) {
		final Attribute attribute = (Attribute) ((IStructuredSelection) getViewer().getSelection()).getFirstElement();
		updatePoliciesWithDependentObject(attribute);
		applyStatePolicyWithDependentObject(getStatePolicy(), attribute);
	}

	private void applyStatePolicyWithDependentObject(final StatePolicy statePolicy, final Object object) {
		setStatePolicy(statePolicy);
		statePolicy.init(object);
		applyStatePolicy();
	}

	@Override
	protected Object getLayoutData() {
		return new GridData(GridData.FILL, GridData.FILL, true, true);
	}

	@Override
	protected Layout getLayout() {
		return new GridLayout(1, false);
	}

	@Override
	protected void createControls(final Composite parent, final FormToolkit toolkit) {
		super.createControls(parent, toolkit);

		final GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true, true);
		layoutData.heightHint = HEIGHT_HINT;
		getViewer().getTable().getParent().setLayoutData(layoutData);
	}

	@Override
	public String getTargetIdentifier() {
		return "catalogAttributeSection"; //$NON-NLS-1$
	}

	@Override
	public void refresh() {
		// do nothing
	}

	@Override
	protected String getTableName() {
		return CATALOG_ATTRIBUTES_TABLE;
	}

}

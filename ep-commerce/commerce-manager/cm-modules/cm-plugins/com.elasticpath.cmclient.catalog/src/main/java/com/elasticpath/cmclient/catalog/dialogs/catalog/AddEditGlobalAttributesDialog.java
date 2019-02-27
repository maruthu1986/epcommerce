/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.dialogs.catalog;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

import com.elasticpath.cmclient.catalog.CatalogImageRegistry;
import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPermissions;
import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.EpUiException;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.comparator.AttributeViewerComparatorByNameIgnoreCase;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.ItemChangeEvent.EventType;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.service.CatalogEventService;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.service.attribute.AttributeService;

/**
 * Dialog for adding/editing global attributes.
 */
@SuppressWarnings({"PMD.GodClass"})
public class AddEditGlobalAttributesDialog extends AbstractEpDialog implements ISelectionChangedListener, IDoubleClickListener,
		SelectionListener {

	private static final int KEY_COLUMN_INDEX = 0;

	private static final int NAME_COLUMN_INDEX = 1;

	private static final int TYPE_COLUMN_INDEX = 2;

	private static final int USAGE_COLUMN_INDEX = 3;

	private static final int REQUIRED_COLUMN_INDEX = 4;

	private static final Object DUMMY_OBJECT = new Object();

	private static final String GLOBAL_ATTRIBUTES_TABLE = "Global Attributes"; //$NON-NLS-1$

	private final AttributeService attributeService;

	private List<Attribute> globalAttributes;

	private TableViewer tableViewer;

	private Button addButton;

	private Button editButton;

	private Button removeButton;

	private final Collection<Attribute> addedItems = new ArrayList<>();

	private final Collection<Attribute> modifiedItems = new ArrayList<>();

	private final Collection<Attribute> removedItems = new ArrayList<>();

	private boolean dirty;

	/**
	 * Constructor for adding/creating a global attributes.
	 *
	 * @param parentShell the parent shell
	 */
	public AddEditGlobalAttributesDialog(final Shell parentShell) {
		super(parentShell, 2, false);
		attributeService = ServiceLocator.getService(ContextIdNames.ATTRIBUTE_SERVICE);
	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite parent) {
		EpState epState = getState();

		final IEpLayoutData tableData = parent.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);

		final IEpTableViewer table = parent.addTableViewer(false, epState, tableData, GLOBAL_ATTRIBUTES_TABLE);
		tableViewer = table.getSwtTableViewer();
		tableViewer.addSelectionChangedListener(this);
		tableViewer.addDoubleClickListener(this);

		initializeTable(table);

		final String buttonText = CatalogMessages.get().CatalogAttributesSection_ButtonText;
		final IEpLayoutComposite buttonsComposite = parent.addGridLayoutComposite(1, false, null);
		editButton = buttonsComposite.addPushButton(
			NLS.bind(CoreMessages.get().Button_Edit,
			buttonText), CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_EDIT), epState, null);
		editButton.setEnabled(false);
		editButton.addSelectionListener(this);
		addButton = buttonsComposite.addPushButton(
			NLS.bind(CoreMessages.get().Button_Add,
			buttonText), CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_ADD), epState, null);
		addButton.addSelectionListener(this);
		removeButton = buttonsComposite.addPushButton(
			NLS.bind(CoreMessages.get().Button_Remove,
			buttonText), CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_REMOVE), epState, null);
		removeButton.setEnabled(false);
		removeButton.addSelectionListener(this);

		if (epState != EpState.EDITABLE) {
			addButton.setEnabled(false);
			editButton.setEnabled(false);
			removeButton.setEnabled(false);
		}
	}

	private EpState getState() {
		EpState epState = EpState.READ_ONLY;
		if (isAuthorized()) {
			epState = EpState.EDITABLE;
		}
		return epState;
	}

	private boolean isAuthorized() {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(CatalogPermissions.GLOBAL_ATTRIBUTE_EDIT);
	}

	private void initializeTable(final IEpTableViewer table) {
		table.getSwtTableViewer().setComparator(new AttributeViewerComparatorByNameIgnoreCase());
		final String[] columnNames = {CatalogMessages.get().CatalogAttributesSection_TableAttributeKey,
				CatalogMessages.get().CatalogAttributesSection_TableAttributeName,
				CatalogMessages.get().CatalogAttributesSection_TableAttributeType,
				CatalogMessages.get().CatalogAttributesSection_TableAttributeUsage,
				CatalogMessages.get().CatalogAttributesSection_TableAttributeRequired};
		final int[] columnWidths = {80, 120, 80, 80, 80};

		for (int i = 0; i < columnNames.length; ++i) {
			table.addTableColumn(columnNames[i], columnWidths[i]);
		}

		table.setContentProvider(new TableContentProvider());
		table.setLabelProvider(new TableLabelProvider());

		Collection<Attribute> readOnlyCatalogAttributes = attributeService.findAllGlobalAttributes();
		globalAttributes = new ArrayList<>(readOnlyCatalogAttributes.size());
		globalAttributes.addAll(readOnlyCatalogAttributes);
		refreshViewerInput();
	}

	/**
	 * Updates the table viewers input. This is done via setting the input to a dummy object (if
	 * previous input is <code>null</code>) or setting it to the previous input.
	 */
	private void refreshViewerInput() {
		if (tableViewer.getInput() == null) {
			tableViewer.setInput(DUMMY_OBJECT);
		} else {
			tableViewer.setInput(tableViewer.getInput());
		}
	}

	@Override
	protected String getPluginId() {
		return CatalogPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return globalAttributes;
	}

	@Override
	public void populateControls() {
		// nothing to do
	}

	@Override
	public void selectionChanged(final SelectionChangedEvent event) {
		if (getState() != EpState.EDITABLE) {
			return;
		}
		final boolean enabled = !event.getSelection().isEmpty();
		removeButton.setEnabled(enabled);
		editButton.setEnabled(enabled);
	}

	@Override
	public void widgetSelected(final SelectionEvent selectionEvent) {
		if (selectionEvent.getSource() == addButton) {
			final Attribute addedItem = addItemAction();
			if (addedItem != null) {
				addAddedItem(addedItem);
				tableViewer.add(addedItem);
				refreshViewerInput();
			}
		} else if (selectionEvent.getSource() == editButton) {
			editAttribute();
		} else if (selectionEvent.getSource() == removeButton) {
			final IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
			if (selection.isEmpty()) {
				return;
			}

			final Attribute removedItem = (Attribute) selection.getFirstElement();
			final boolean answerYes = MessageDialog.openConfirm(getShell(), CatalogMessages.get().CatalogAttributesSection_RemoveDialog_title,
					getRemoveDialogDescription(removedItem));
			if (answerYes && removeItemAction(removedItem)) {
				addRemovedItem(removedItem);
				tableViewer.remove(removedItem);
				refreshViewerInput();
			}
		}
	}

	private void editAttribute() {
		final Attribute editItem = (Attribute) ((IStructuredSelection) tableViewer.getSelection()).getFirstElement();
		editItemAction(editItem);
		addModifiedItem(editItem);
		refreshViewerInput();
	}

	private Attribute addItemAction() {
		CatalogAttributesAddEditDialog dialog = new CatalogAttributesAddEditDialog(getShell(), null, true,
				null);
		if (dialog.open() == Window.OK) {
			return dialog.getAttribute();
		}
		return null;
	}

	private void editItemAction(final Attribute object) {
		CatalogAttributesAddEditDialog dialog = new CatalogAttributesAddEditDialog(getShell(), object, true,
				null);
		dialog.open();
	}

	private boolean removeItemAction(final Attribute object) {
		final ParameterPasser passer = new ParameterPasser();
		passer.canRemove = false;
		final IProgressService progressService = PlatformUI.getWorkbench().getProgressService();
		try {
			// we can use the passer variable here because this is a blocking job
			progressService.busyCursorWhile(monitor -> passer.canRemove = !isInUse(object.getUidPk()));
		} catch (InvocationTargetException | InterruptedException e) {
			throw new EpUiException("Error removing: " + e.getMessage(), e); //$NON-NLS-1$
		}

		if (!passer.canRemove) {
			MessageDialog.openError(getShell(), CatalogMessages.get().CatalogAttributesSection_ErrorDialog_InUse_title,

					NLS.bind(CatalogMessages.get().CatalogAttributesSection_ErrorDialog_InUse_desc,
					getItemName(object)));
		}
		return passer.canRemove;
	}

	private String getRemoveDialogDescription(final Attribute attribute) {
		return
			NLS.bind(CatalogMessages.get().CatalogAttributesSection_RemoveDialog_description,
			getItemName(attribute));
	}

	private String getItemName(final Attribute attribute) {
		return String.format("%1$s - %2$s - %3$s", //$NON-NLS-1$
				attribute.getKey(), attribute.getName(), CoreMessages.get()
						.getMessage(attribute.getAttributeType().getNameMessageKey()));
	}

	private void addAddedItem(final Attribute item) {
		getAddedItems().add(item);
		globalAttributes.add(item);
		markDirty();
	}

	private void addModifiedItem(final Attribute item) {
		if (!getAddedItems().contains(item)) {
			getModifiedItems().add(item);
		}
		markDirty();
	}

	private void addRemovedItem(final Attribute item) {
		getModifiedItems().remove(item);
		if (!getAddedItems().remove(item)) {
			getRemovedItems().add(item);
		}
		globalAttributes.remove(item);
		markDirty();
	}

	private Collection<Attribute> getAddedItems() {
		return addedItems;
	}

	private Collection<Attribute> getRemovedItems() {
		return removedItems;
	}

	private Collection<Attribute> getModifiedItems() {
		return modifiedItems;
	}


	private void markDirty() {
		dirty = true;
	}

	@Override
	protected void okPressed() {
		if (dirty) {
			for (Attribute attribute : getAddedItems()) {
				attributeService.add(attribute);
				CatalogEventService.getInstance().notifyAttributeChanged(
						new ItemChangeEvent<>(this, attribute, EventType.ADD));
			}
			getAddedItems().clear();

			for (Attribute attribute : getModifiedItems()) {
				final Attribute updatedAttribute = attributeService.update(attribute);
				CatalogEventService.getInstance().notifyAttributeChanged(
						new ItemChangeEvent<>(this, updatedAttribute, EventType.CHANGE));
			}
			getModifiedItems().clear();

			for (Attribute attribute : getRemovedItems()) {
				attributeService.remove(attribute);
				CatalogEventService.getInstance().notifyAttributeChanged(
						new ItemChangeEvent<>(this, attribute, EventType.REMOVE));
			}
			getRemovedItems().clear();
		}
		super.okPressed();
	}

	@Override
	protected void bindControls() {
		// nothing to bind
	}

	@Override
	protected String getInitialMessage() {
		return ""; //$NON-NLS-1$
	}

	@Override
	protected String getTitle() {
		return CatalogMessages.get().AddEditGlobalAttributesDialog_Title;
	}

	@Override
	protected Image getWindowImage() {
		return CatalogImageRegistry.getImage(CatalogImageRegistry.ATTRIBUTE_EDIT);
	}

	@Override
	protected String getWindowTitle() {
		return CatalogMessages.get().AddEditGlobalAttributesDialog_WindowTitle;
	}

	@Override
	public void doubleClick(final DoubleClickEvent event) {
		if (isAuthorized()) {
			editAttribute();
		}
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		// not used
	}

	/**
	 * Content provider for the table.
	 */
	private class TableContentProvider implements IStructuredContentProvider {

		@Override
		public Object[] getElements(final Object inputElement) {
			return globalAttributes.toArray();
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
	 * Label provider for the table.
	 */
	private class TableLabelProvider extends LabelProvider implements ITableLabelProvider {

		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			final Attribute attribute = (Attribute) element;

			switch (columnIndex) {
				case KEY_COLUMN_INDEX:
					return attribute.getKey();
				case NAME_COLUMN_INDEX:
					return attribute.getName();
				case TYPE_COLUMN_INDEX:
					return CoreMessages.get().getMessage(attribute.getAttributeType().getNameMessageKey());
				case USAGE_COLUMN_INDEX:
					return attribute.getAttributeUsage().toString();
				case REQUIRED_COLUMN_INDEX:
					if (attribute.isRequired()) {
						return CoreMessages.get().YesNoForBoolean_true;
					}
					return CoreMessages.get().YesNoForBoolean_false;
				default:
					return ""; //$NON-NLS-1$
			}
		}
	}

	/**
	 * Parameter holder class for passing a parameter between threads.
	 */
	private final class ParameterPasser {
		private boolean canRemove;

		public boolean canCanRemove() {
			return canRemove;
		}
	}

	private boolean isInUse(final long uidPk) { // TODO: refactor it to attributeService.isInUse (at CatalogAttributesSection too)
		for (Long attribute : attributeService.getAttributeInUseUidList()) {
			if (attribute == uidPk) {
				return true;
			}
		}
		return false;
	}

}

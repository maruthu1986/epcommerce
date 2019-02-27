/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.policy.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.editors.TableItems;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.policy.PolicyPlugin;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.StatePolicyTarget;
import com.elasticpath.cmclient.policy.StatePolicyTargetListener;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;

/**
 * An abstract base class for page sections displaying a table with add/remove/edit buttons on the
 * right hand side. This page section is policy aware.
 *
 * @param <T> The type of object stored within the table
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.GodClass" })
public abstract class AbstractPolicyAwareEpTableSection <T> extends  AbstractCmClientEditorPageSectionPart implements SelectionListener,
ISelectionChangedListener, IDoubleClickListener, StatePolicyTarget {

	private final Map<String, PolicyActionContainer> policyTargetContainers = new HashMap<>();

	private StatePolicy statePolicy;

	private static ListenerList listenerList;

	static {
		listenerList = new ListenerList(ListenerList.IDENTITY);
	}

	private static final Object DUMMY_OBJECT = new Object();

	private IPolicyTargetLayoutComposite controlPane;

	private final FormPage formPage;

	private final ControlModificationListener controlModificationListener;

	private Button addButton;

	private Button editButton;

	private Button removeButton;

	private TableViewer tableViewer;

	/** Internal class to hold the collections of added, modified, and removed objects. */
	private TableItems<T> tableItems;

	private PolicyActionContainer epTableSectionControlPane;

	private PolicyActionContainer epTableSectionControls;

	/**
	 *  The Policy action container for the edit button.
	 */
	private PolicyActionContainer epTableSectionEditButton;

	/**
	 *  The Policy action container for the add button.
	 */
	private PolicyActionContainer epTableSectionAddButton;

	/**
	 *  The Policy action container for the remove button.
	 */
	private PolicyActionContainer epTableSectionRemoveButton;

	private EditorTableSelectionProvider editorTableSelectionProvider;

	private final InternalObjectRegistryListener objectRegistryListener = new InternalObjectRegistryListener();
	private static final Logger LOG = Logger.getLogger(AbstractPolicyAwareEpTableSection.class);


	/**
	 * Default Constructor.
	 *
	 * @param formPage the form page
	 * @param editor the editor
	 */
	public AbstractPolicyAwareEpTableSection(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor, ExpandableComposite.EXPANDED);
		PolicyPlugin.getDefault().registerStatePolicyTarget(this);
		fireStatePolicyTargetActivated();
		this.tableItems = new TableItems<>();
		this.formPage = formPage;
		controlModificationListener = editor;
		if (editor instanceof EditorTableSelectionProvider) {
			this.editorTableSelectionProvider = (EditorTableSelectionProvider) editor;
		} else {
			LOG.error("Unable to set Table Selection Provider for Catalog Section "); //$NON-NLS-1$
		}
	}

	@Override
	protected void createControls(final Composite parent, final FormToolkit toolkit) {
		controlPane = PolicyTargetCompositeFactory.wrapLayoutComposite(CompositeFactory
			.createGridLayoutComposite(parent, 2, false));
		final IEpLayoutData tableData = controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true,
			true);

		epTableSectionControlPane = addPolicyActionContainer("epTableSectionControlPane"); //$NON-NLS-1$

		final IEpTableViewer table = controlPane.addTableViewer(false, tableData, epTableSectionControlPane, getTableName());
		tableViewer = table.getSwtTableViewer();
		tableViewer.addSelectionChangedListener(this);
		tableViewer.addDoubleClickListener(this);

		if (editorTableSelectionProvider != null) {
			tableViewer.addSelectionChangedListener(editorTableSelectionProvider.getEditorTableSelectionProvider());
		}

		initializeTable(table);
		addButtons();
		applyStatePolicy(getStatePolicy());
	}

	@Override
	public void dispose() {
		removeSelectionListenerFromButton(addButton);
		removeSelectionListenerFromButton(editButton);
		removeSelectionListenerFromButton(removeButton);
		PolicyPlugin.getDefault().unregisterStatePolicyTarget(this);
		getObjectRegistryListener().deRegisterListener();
		tableViewer.removeSelectionChangedListener(this);
		super.dispose();
	}

	private void removeSelectionListenerFromButton(final Button button) {
		if (!button.isDisposed()) {
			button.removeSelectionListener(this);
		}
	}


	/**
	 * Adds buttons to the right hand side of the section.
	 */
	protected void addButtons() {
		epTableSectionControls = addPolicyActionContainer("epTableSectionControls"); //$NON-NLS-1$

		final IPolicyTargetLayoutComposite buttonsComposite = controlPane.addGridLayoutComposite(1, false, null, epTableSectionControls);
		epTableSectionEditButton = addPolicyActionContainer("epTableSectionEditButton"); //$NON-NLS-1$
		editButton = buttonsComposite.addPushButton(
			NLS.bind(CoreMessages.get().Button_Edit,
			getSectionType()), CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_EDIT), null, epTableSectionEditButton);
		editButton.addSelectionListener(this);

		epTableSectionAddButton = addPolicyActionContainer("epTableSectionAddButton"); //$NON-NLS-1$
		addButton = buttonsComposite.addPushButton(
			NLS.bind(CoreMessages.get().Button_Add,
			getSectionType()), CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_ADD), null, epTableSectionAddButton);
		addButton.addSelectionListener(this);

		epTableSectionRemoveButton = addPolicyActionContainer("epTableSectionRemoveButton"); //$NON-NLS-1$
		removeButton = buttonsComposite.addPushButton(
			NLS.bind(CoreMessages.get().Button_Remove,
			getSectionType()), CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_REMOVE), null, epTableSectionRemoveButton);
		removeButton.addSelectionListener(this);
	}

	@Override
	public void populateControls() {
		// Set the modification listener *after* setting the control values
		// so that controls aren't considered to be modified when the initial value is set
		controlPane.setControlModificationListener(controlModificationListener);
	}

	/**
	 * Selection change handler.
	 * @param event the selection change event.
	 */
	@Override
	public abstract void selectionChanged(final SelectionChangedEvent event);

	/**
	 * Initializes a table with columns and default data, etc.
	 *
	 * @param table the table that should be initialized
	 */
	protected abstract void initializeTable(final IEpTableViewer table);

	/**
	 * Returns the current state of the table.
	 *
	 * @return the current state of the table using state policies.
	 */
	protected abstract EpState getEditorTableState();

	/**
	 * This value is the displayable value on the buttons on the right hand side. I.e. if the
	 * table deals with 'Brands' return 'Brand' for each button to show 'Add Brand...', 'Remove
	 * Brand...', 'Edit Brand...'
	 *
	 * @return displayable value on buttons
	 */
	protected abstract String getSectionType();

	/**
	 * Get this editor's underlying {@link TableViewer}.
	 *
	 * @return this editor's underlying TableViewer
	 */
	protected TableViewer getViewer() {
		return tableViewer;
	}

	/**
	 * Get this editor's underlying {@link FormPage}.
	 *
	 * @return this editor's underlying {@link FormPage}
	 */
	protected FormPage getPage() {
		return formPage;
	}

	/**
	 * Gets this editor's underlying add {@link Button}.
	 *
	 * @return the editor's add {@link Button}
	 */
	protected Button getAddButton() {
		return addButton;
	}

	/**
	 * Gets this editor's underlying remove {@link Button}.
	 *
	 * @return the editor's underlying remove {@link Button}
	 */
	protected Button getRemoveButton() {
		return removeButton;
	}

	/**
	 * Gets this editor's underlying edit {@link Button}.
	 *
	 * @return the editor's underlying edit {@link Button}
	 */
	protected Button getEditButton() {
		return editButton;
	}

	/**
	 * Widget default selection handler.
	 *
	 * @param selectionEvent the selection event.
	 */
	@Override
	public void widgetDefaultSelected(final SelectionEvent selectionEvent) {
		// not used
	}

	/**
	 * Widget selected handler.
	 *
	 * @param selectionEvent the selection event.
	 */
	@Override
	public void widgetSelected(final SelectionEvent selectionEvent) {
		if (selectionEvent.getSource() == addButton) {
			final T addedItem = addItemAction();
			if (addedItem != null) {
				addAddedItem(addedItem);
				getViewer().add(addedItem);
				refreshViewerInput();
			}
		} else if (selectionEvent.getSource() == editButton) {
			final T editItem = (T) ((IStructuredSelection) getViewer().getSelection()).getFirstElement();
			if (editItemAction(editItem)) {
				addModifiedItem(editItem);
				refreshViewerInput();
			}
		} else if (selectionEvent.getSource() == removeButton) {
			final IStructuredSelection selection = (IStructuredSelection) getViewer().getSelection();
			if (selection.isEmpty()) {
				return;
			}

			final T removedItem = (T) selection.getFirstElement();
			final boolean answerYes = MessageDialog.openConfirm(getPage().getSite().getShell(), getRemoveDialogTitle(),
					getRemoveDialogDescription(removedItem));
			if (answerYes && removeItemAction(removedItem)) {
				addRemovedItem(removedItem);
			}
			applyStatePolicy(getStatePolicy());
		}
	}

	/**
	 * Double click handler.
	 *
	 * @param event the double click event
	 */
	@Override
	public void doubleClick(final DoubleClickEvent event) {
		// if the editor is in read-only mode, do not allow this action
		final T element = (T) ((IStructuredSelection) event.getSelection()).getFirstElement();
		getEpTableSectionControlPane().setPolicyDependent(element);
		if (getEditorTableState() != EpState.EDITABLE) {
			return;
		}
		if (editItemAction(element)) {
			addModifiedItem(element);
			refreshViewerInput();
		}
	}

	/**
	 * Action that is executed when the add button is pressed. This action is executed before the
	 * returned object is added to the list provided by {@link #getAddedItems()}. This table need
	 * not be refreshed from this action (but the item will still need to be added).
	 *
	 * @return the object created as a result
	 */
	protected abstract T addItemAction();

	/**
	 * Action that is executed when the edit button is pressed. This action is executed before the
	 * passed object is added to the list provided by {@link #getModifiedItems()}. The table need
	 * not be refreshed from this action.
	 *
	 * @param object the object to be edited
	 * @return whether the object was edited
	 */
	protected abstract boolean editItemAction(final T object);

	/**
	 * Action that is executed when the remove button is pressed. This is executed only after the
	 * user confirms the item is to be deleted. This action is executed before the returned object
	 * is added to the list provided by {@link #getRemovedItems()}. The table need not be
	 * refreshed from this action.
	 *
	 * @param object the object to be deleted
	 * @return whether the removal was successful
	 */
	protected abstract boolean removeItemAction(final T object);

	/**
	 * Returns the default title for the remove item dialog. Override this method to change the text.
	 *
	 * @return the default title for the remove item dialog
	 */
	protected String getRemoveDialogTitle() {
		return CoreMessages.get().RemoveDialog_Title;
	}

	/**
	 * Returns the default message for removal of the given item. Override this method to change the text.
	 *
	 * @param item the item to remove
	 * @return the default message for removal of the given item
	 */
	protected String getRemoveDialogDescription(final T item) {
		return
			NLS.bind(CoreMessages.get().RemoveDialog_Message,
			getItemName(item));
	}

	/**
	 * The name of the item that is display when asked to remove.
	 *
	 * @param object the object to be removed
	 * @return the object name to be displayed when asked to remove
	 */
	protected String getItemName(final T object) {
		return object.toString();
	}

	/**
	 * Updates the table viewers input. This is done via setting the input to a dummy object (if
	 * previous input is <code>null</code>) or setting it to the previous input.
	 */
	protected void refreshViewerInput() {

		if (tableViewer != null && tableViewer.getContentProvider() != null) {
			if (tableViewer.getInput() == null) {
				tableViewer.setInput(DUMMY_OBJECT);
			} else {
				tableViewer.setInput(tableViewer.getInput());
			}
		}
	}

	/**
	 * Adds an item to the set of items added.
	 *
	 * @param item the item to add
	 */
	protected void addAddedItem(final T item) {
		this.tableItems.addAddedItem(item);
	}

	/**
	 * Adds an item to the set of items modified. If the item was previously added, it's not added
	 * to this set, it remains in the added item set.
	 *
	 * @param item the item that modified
	 */
	protected void addModifiedItem(final T item) {
		this.tableItems.addModifiedItem(item);
	}

	/**
	 * Adds an item to the set of items removed. The item is also removed from the set of items
	 * added (if present) and of items modified (if present)
	 *
	 * @param item the item that was removed
	 */
	protected void addRemovedItem(final T item) {
		this.tableItems.addRemovedItem(item);
	}

	/**
	 * Lists items that have been removed.
	 *
	 * @return items that have been removed
	 */
	protected Set<T> getAddedItems() {
		return this.tableItems.getAddedItems();
	}

	/**
	 * Lists items that have been removed.
	 *
	 * @return items that have been removed
	 */
	protected Set<T> getRemovedItems() {
		return this.tableItems.getRemovedItems();
	}

	/**
	 * Lists items that have been added or modified (<i>not</i> removed).
	 *
	 * @return items that have been added or modified
	 */
	protected Set<T> getModifiedItems() {
		return this.tableItems.getModifiedItems();
	}

	/**
	 * Returns the editor page to which the section belongs.
	 *
	 * @return the page
	 */
	public FormPage getFormPage() {
		return formPage;
	}


	@Override
	public void addGovernableListener(final StatePolicyTargetListener governableListener) {
		listenerList.add(governableListener);
	}

	@Override
	public void removeGovernableListener(final StatePolicyTargetListener listener) {
		listenerList.remove(listener);
	}

	@Override
	public abstract String getTargetIdentifier();

	@Override
	public void applyStatePolicy(final StatePolicy statePolicy) {
		this.statePolicy = statePolicy;
		statePolicy.init(getDependentObject());
		reapplyStatePolicy();
		
	}

	private void reapplyStatePolicy() {
		applyStatePolicy();
		if (tableViewer != null) {
			refreshViewerInput();
		}
	}

	@Override
	public PolicyActionContainer addPolicyActionContainer(final String name) {
		final PolicyActionContainer container = new PolicyActionContainer(name);
		getPolicyActionContainers().put(name, container);
		return container;
	}

	@Override
	public Map<String, PolicyActionContainer> getPolicyActionContainers() {
		return policyTargetContainers;
	}

	/**
	 * Apply the already stored state policy.
	 */
	public void applyStatePolicy() {
		if (statePolicy != null) {
			for (final PolicyActionContainer container : getPolicyActionContainers().values()) {
				statePolicy.apply(container);
			}
		}
	}

	/**
	 * Fire the activation event to all listeners.
	 */
	private void fireStatePolicyTargetActivated() {
		for (final Object listener : listenerList.getListeners()) {
			((StatePolicyTargetListener) listener).statePolicyTargetActivated(this);
		}
	}

	/**
	 *
	 * @return the statePolicy
	 */
	public StatePolicy getStatePolicy() {
		return statePolicy;
	}

	/**
	 *
	 * @param statePolicy the statePolicy to set
	 */
	public void setStatePolicy(final StatePolicy statePolicy) {
		this.statePolicy = statePolicy;
	}

	/**
	 *
	 * @return the epTableSectionEditButton
	 */
	public PolicyActionContainer getEpTableSectionEditButton() {
		return epTableSectionEditButton;
	}

	/**
	 *
	 * @return the epTableSectionAddButton
	 */
	public PolicyActionContainer getEpTableSectionAddButton() {
		return epTableSectionAddButton;
	}

	/**
	 *
	 * @return the epTableSectionRemoveButton
	 */
	public PolicyActionContainer getEpTableSectionRemoveButton() {
		return epTableSectionRemoveButton;
	}


	private InternalObjectRegistryListener getObjectRegistryListener() {
		return objectRegistryListener;
	}


	/**
	 * @return the epTableSectionControls
	 */
	public PolicyActionContainer getEpTableSectionControlPane() {
		return epTableSectionControlPane;
	}

	
	/**
	 *  Updates the relevant policies with the given dependent object. 
	 *
	 * @param object the object to use.
	 */
	protected void updatePoliciesWithDependentObject(final Object object) {
		getEpTableSectionEditButton().setPolicyDependent(object);
		getEpTableSectionRemoveButton().setPolicyDependent(object);
	}

	/**
	 * Returns the name for the table which is used for testing.
	 * @return table name
	 */
	protected abstract String getTableName();

	/**
	 * Internal listener for the object registry. 
	 */
	private class InternalObjectRegistryListener extends AbstractObjectListener {
		
		@Override
		public void eventFired(final String key) {
			if (ChangeSetHelper.OBJECT_REG_ACTIVE_CHANGE_SET.equals(key)) {
				reapplyStatePolicy();
			}
		}
		
	}

	/**
	 * Gets the table items.
	 * @return the table items.
	 */
	public TableItems<T> getTableItems() {
		return tableItems;
	}

	/**
	 * Sets the table items.
	 * @param tableItems the table items 
	 */
	public void setTableItems(final TableItems<T> tableItems) {
		this.tableItems = tableItems;
	}

}

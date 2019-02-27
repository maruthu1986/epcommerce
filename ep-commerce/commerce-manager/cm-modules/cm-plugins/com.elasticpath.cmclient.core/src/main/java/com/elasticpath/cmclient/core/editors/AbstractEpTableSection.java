/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.editors;

import java.util.Set;

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
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;

/**
 * An abstract base class for page sections displaying a table with add/remove/edit buttons on the
 * right hand side.
 *
 * @param <T> The type of object stored within the table
 */
public abstract class AbstractEpTableSection<T> extends AbstractCmClientEditorPageSectionPart implements SelectionListener,
		ISelectionChangedListener, IDoubleClickListener {

	private static final Object DUMMY_OBJECT = new Object();

	private final String tableName;

	private IEpLayoutComposite controlPane;

	private final FormPage formPage;

	private final ControlModificationListener controlModificationListener;

	private Button addButton;

	private Button editButton;

	private Button removeButton;

	private TableViewer tableViewer;

	/** Internal class to hold the collections of added, modified, and removed objects. */
	private final TableItems<T> tableItems;

	/**
	 * Default constructor.
	 *
	 * @param formPage the form page
	 * @param editor the editor
	 * @param tableName name of the table
	 */
	public AbstractEpTableSection(final FormPage formPage, final AbstractCmClientFormEditor editor, final String tableName) {
		super(formPage, editor, ExpandableComposite.EXPANDED);
		this.tableName = tableName;
		this.tableItems = new TableItems<T>();
		this.formPage = formPage;
		controlModificationListener = editor;
	}

	@Override
	protected void createControls(final Composite parent, final FormToolkit toolkit) {
		EpState epState = getEditorState();

		controlPane = CompositeFactory.createGridLayoutComposite(parent, 2, false);
		final IEpLayoutData tableData = controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);

		final IEpTableViewer table = controlPane.addTableViewer(false, epState, tableData, tableName);
		tableViewer = table.getSwtTableViewer();
		tableViewer.addSelectionChangedListener(this);
		tableViewer.addDoubleClickListener(this);

		initializeTable(table);
		addButtons(epState);
	}

	@Override
	public void dispose() {
		removeSelectionListenerFromButton(addButton);
		removeSelectionListenerFromButton(editButton);
		removeSelectionListenerFromButton(removeButton);

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
	 *
	 * @param epState the current security state
	 */
	protected void addButtons(final EpState epState) {
		final IEpLayoutComposite buttonsComposite = controlPane.addGridLayoutComposite(1, false, null);
		editButton = buttonsComposite.addPushButton(
			NLS.bind(CoreMessages.get().Button_Edit,
			getSectionType()), CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_EDIT), epState, null);
		editButton.setEnabled(false);
		editButton.addSelectionListener(this);
		addButton = buttonsComposite.addPushButton(
			NLS.bind(CoreMessages.get().Button_Add,
			getSectionType()), CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_ADD), epState, null);
		addButton.addSelectionListener(this);
		removeButton = buttonsComposite.addPushButton(
			NLS.bind(CoreMessages.get().Button_Remove,
			getSectionType()), CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_REMOVE), epState, null);
		removeButton.setEnabled(false);
		removeButton.addSelectionListener(this);

		if (epState != EpState.EDITABLE) {
			addButton.setEnabled(false);
			editButton.setEnabled(false);
			removeButton.setEnabled(false);
		}
	}

	@Override
	public void populateControls() {
		// Set the modification listener *after* setting the control values
		// so that controls aren't considered to be modified when the initial value is set
		controlPane.setControlModificationListener(controlModificationListener);
	}

	@Override
	public void selectionChanged(final SelectionChangedEvent event) {
		if (getEditorState() != EpState.EDITABLE) {
			return;
		}
		final boolean enabled = !event.getSelection().isEmpty();
		removeButton.setEnabled(enabled);
		editButton.setEnabled(enabled);
	}

	/**
	 * Initializes a table with columns and default data, etc.
	 *
	 * @param table the table that should be initialized
	 */
	protected abstract void initializeTable(final IEpTableViewer table);

	/**
	 * Returns the current state of the editor.
	 *
	 * @return the current state of the editor
	 */
	protected abstract EpState getEditorState();

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

	@Override
	public void widgetDefaultSelected(final SelectionEvent selectionEvent) {
		// not used
	}

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
				getViewer().remove(removedItem);
				refreshViewerInput();
			}
		}
	}

	@Override
	public void doubleClick(final DoubleClickEvent event) {
		// if the editor is in read-only mode, do not allow this action
		if (getEditorState() != EpState.EDITABLE) {
			return;
		}
		final T element = (T) ((IStructuredSelection) event.getSelection()).getFirstElement();
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
		if (tableViewer.getInput() == null) {
			tableViewer.setInput(DUMMY_OBJECT);
		} else {
			tableViewer.setInput(tableViewer.getInput());
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
}

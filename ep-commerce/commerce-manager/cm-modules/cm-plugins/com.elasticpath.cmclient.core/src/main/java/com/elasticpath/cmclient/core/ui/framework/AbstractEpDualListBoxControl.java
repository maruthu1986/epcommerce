/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.ui.framework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;

/**
 * Abstract implementation of a dual list box control.
 * <p>
 * The sub controls of this component are created right when createControls() is invoked.
 * Constructing an instance of this class is not enough.
 * <p>
 * <i>Example usage:<br>
 * public class MyDualListBoxControl extends AbstractEpDualListBoxControl&lt;MyDomainClass&gt; {<br>
 * ...<br>
 * }<br>
 *
 * MyDualListBoxControl myDualListBox = new MyDualListBoxControl(...);<br>
 * myDualListBox.createControls();<br>
 * </i>
 * @param <T> the target model class
 */
@SuppressWarnings({ "PMD.GodClass", "PMD.TooManyMethods" })
public abstract class AbstractEpDualListBoxControl<T> {

	/**
	 * Style bit for default style.
	 */
	public static final int NONE = 0;

	/**
	 * Style bit for enabling buttons for adding/removing all the elements ('<<' and '>>').
	 */
	public static final int ALL_BUTTONS = 1;

	/**
	 * Style bit for up/down buttons for moving the elements in the assigned listbox.
	 */
	public static final int UP_DOWN_BUTTONS = 2;

	/**
	 * Style bit for enabling multi-selection mode.
	 */
	public static final int MULTI_SELECTION = 4;

	/**
	 * Style bit for enabling buttons that add to the list.
	 */
	public static final int DISABLE_REMOVAL_BUTTONS = 8;

	/**
	 * Style bit for enabling empty up and down button pane.
	 */
	public static final int EMPTY_UP_DOWN_BUTTON_PANEL = 16;

	private static final int FOUR_COLUMNS = 4;

	private static final int THREE_COLUMNS = 3;

	private static final int TABLE_HEIGHT = 100;

	private static final int TABLE_WIDTH = 200;

	private static final Logger LOG = Logger.getLogger(AbstractEpDualListBoxControl.class);

	private Button addOneButton;

	private Button removeOneButton;

	private final T model;

	private Button addAllButton;

	private Button removeAllButton;

	private Button moveUpButton;

	private Button moveDownButton;

	private TableViewer availableTableViewer;

	private TableViewer assignedTableViewer;

	private EpState epState;

	private final List<IDualListChangeListener> listeners = new LinkedList<IDualListChangeListener>();

	private final IEpLayoutComposite parentComposite;

	private final IEpLayoutData data;

	private final String availableTitle;

	private final String assignedTitle;

	private final int style;

	/**
	 * Constructor.
	 *
	 * @param parentComposite the parent composite
	 * @param model the model object
	 * @param availableTitle the title for the Available listbox
	 * @param assignedTitle the title for the Assigned listbox
	 * @param style style bits - (ALL_BUTTONS, UP_DOWN_BUTTONS, MULTI_SELECTION). The different styles can be combined using OR('|').
	 * @param data EP layout data
	 * @param epState EpState (EDITABLE, READ_ONLY, DISABLED)
	 */
	public AbstractEpDualListBoxControl(final IEpLayoutComposite parentComposite, final T model, final String availableTitle,
			final String assignedTitle, final int style, final IEpLayoutData data, final EpState epState) {
		this.model = model;
		this.epState = epState;
		this.style = style;
		this.parentComposite = parentComposite;
		this.data = data;
		this.availableTitle = availableTitle;
		this.assignedTitle = assignedTitle;
	}

	private boolean isStyleEnabled(final int styleBitsToCheck) {
		return (this.style & styleBitsToCheck) != 0;
	}

	/**
	 * Creates the controls.
	 */
	public void createControls() {
		addEpContents(parentComposite, data, availableTitle, assignedTitle);
		customizeControls();
	}

	/**
	 * This method could be overridden by client to customise the controls.
	 */
	protected void customizeControls() {
		// nothing to customise by default
	}

	private void addEpContents(final IEpLayoutComposite parentComposite, final IEpLayoutData data,
			final String availableTitle, final String assignedTitle) {
		final IEpLayoutComposite controlPane;
		if (isStyleEnabled(UP_DOWN_BUTTONS) || isStyleEnabled(EMPTY_UP_DOWN_BUTTON_PANEL)) {
			controlPane = parentComposite.addGridLayoutComposite(FOUR_COLUMNS, false, data);
		} else {
			controlPane = parentComposite.addGridLayoutComposite(THREE_COLUMNS, false, data);
		}
		createControls(controlPane, availableTitle, assignedTitle);
	}

	/**
	 * Creates the UI controls of the dual list view.
	 */
	private void createControls(final IEpLayoutComposite controlPane, final String availableTitle, final String assignedTitle) {
		final IEpLayoutData listViewerData = controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);

		final boolean multiSelection = isStyleEnabled(MULTI_SELECTION);

		final IEpListViewer epAvailableListViewer = controlPane.addListViewer(availableTitle, multiSelection, epState, listViewerData);

		epAvailableListViewer.setContentProvider(new DualListBoxContentProvider());
		epAvailableListViewer.setLabelProvider(getLabelProvider());
		epAvailableListViewer.setInput(getAvailable());
		availableTableViewer = epAvailableListViewer.getSwtTableViewer();
		availableTableViewer.getTable().setLayoutData(createTableViewerGridData());

		// Composite with buttons
		final IEpLayoutComposite buttonsComposite = controlPane.addGridLayoutComposite(1, false, controlPane.createLayoutData(IEpLayoutData.FILL,
				IEpLayoutData.CENTER));
		buttonsComposite.addEmptyComponent(null);

		addOneButton = buttonsComposite.addPushButton(">", epState, null); //$NON-NLS-1$
		addOneButton.setToolTipText(CoreMessages.get().button_Add);
		addOneButton.addSelectionListener(selectionListener);
		if (!isStyleEnabled(DISABLE_REMOVAL_BUTTONS)) {
			removeOneButton = buttonsComposite.addPushButton("<", epState, null); //$NON-NLS-1$
			removeOneButton.setToolTipText(CoreMessages.get().button_Remove);
			removeOneButton.addSelectionListener(selectionListener);
		}
		if (isStyleEnabled(ALL_BUTTONS)) {
			buttonsComposite.addEmptyComponent(null);

			addAllButton = buttonsComposite.addPushButton(">>", epState, null); //$NON-NLS-1$
			addAllButton.setToolTipText(CoreMessages.get().button_AddAll);
			addAllButton.addSelectionListener(selectionListener);
			if (!isStyleEnabled(DISABLE_REMOVAL_BUTTONS)) {
				removeAllButton = buttonsComposite.addPushButton("<<", epState, null); //$NON-NLS-1$
				removeAllButton.setToolTipText(CoreMessages.get().button_RemoveAll);
				removeAllButton.addSelectionListener(selectionListener);
			}
		}
		// List box
		final IEpListViewer epAssignedListViewer = controlPane.addListViewer(assignedTitle, multiSelection, epState, listViewerData);
		epAssignedListViewer.setContentProvider(new DualListBoxContentProvider());
		epAssignedListViewer.setLabelProvider(getLabelProvider());
		epAssignedListViewer.setInput(getAssigned());
		assignedTableViewer = epAssignedListViewer.getSwtTableViewer();
		assignedTableViewer.getTable().setLayoutData(createTableViewerGridData());

		if (isStyleEnabled(UP_DOWN_BUTTONS) || isStyleEnabled(EMPTY_UP_DOWN_BUTTON_PANEL)) {
			createUpDownButtons(controlPane);
		}

		if (getAvailableFilter() != null) {
			availableTableViewer.addFilter(getAvailableFilter());
		}

		if (getAssignedFilter() != null) {
			assignedTableViewer.addFilter(getAssignedFilter());
		}
	}

	private void createUpDownButtons(final IEpLayoutComposite controlPane) {
		assignedTableViewer.addSelectionChangedListener(new SelectionChangedListenerImpl());

		final IEpLayoutComposite moveButtonsComposite = controlPane.addGridLayoutComposite(1, false,
				controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER));
		moveButtonsComposite.addEmptyComponent(null);

		final Image upImage = CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_UP_ARROW);
		moveUpButton = moveButtonsComposite.addPushButton("", upImage, EpState.READ_ONLY, null); //$NON-NLS-1$
		moveUpButton.setToolTipText(CoreMessages.get().button_MoveUp);
		moveUpButton.addSelectionListener(selectionListener);

		final Image downImage = CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_DOWN_ARROW);
		moveDownButton = moveButtonsComposite.addPushButton("", downImage, EpState.READ_ONLY, null); //$NON-NLS-1$
		moveDownButton.setToolTipText(CoreMessages.get().button_MoveDown);
		moveDownButton.addSelectionListener(selectionListener);
		
		if (isStyleEnabled(EMPTY_UP_DOWN_BUTTON_PANEL)) {
			disableUpDownButtons();
		}
	}

	private void disableUpDownButtons() {
		moveUpButton.setVisible(false);
		moveDownButton.setVisible(false);
	}

	private GridData createTableViewerGridData() {
		final GridData tableViewerData = new GridData(GridData.FILL, GridData.FILL, true, true);
		tableViewerData.heightHint = TABLE_HEIGHT;
		tableViewerData.widthHint = TABLE_WIDTH;
		return tableViewerData;
	}

	/**
	 * Should return the label provider set to the both list viewers.
	 *
	 * @return implementation of ILabelProvider
	 * @see org.eclipse.jface.viewers.LabelProvider
	 */
	protected abstract ILabelProvider getLabelProvider();

	/**
	 * Returns the model object.
	 *
	 * @return the model object
	 */
	protected T getModel() {
		return model;
	}

	/**
	 * Sets a sorter to the both list boxes.
	 *
	 * @param sorter the viewer sorter
	 */
	public void setSorter(final ViewerSorter sorter) {
		availableTableViewer.setSorter(sorter);
		assignedTableViewer.setSorter(sorter);
	}

	/**
	 * Add the "Available Listbox" selected objects from the model object.
	 *
	 * @param selection the selection in the Available listbox
	 * @return boolean depending if assigning to model was successful
	 */
	protected abstract boolean assignToModel(final IStructuredSelection selection);

	/**
	 * Remove the "Assigned Listbox" selected objects from the model object.
	 *
	 * @param selection the selection in the Assigned listbox
	 * @return boolean depending if removing from model was successful
	 */
	protected abstract boolean removeFromModel(final IStructuredSelection selection);

	/**
	 * Move the "Assigned Listbox" selected objects up in the list.
	 *
	 * @param index the selected element index
	 * @return boolean depending if moving model element up was successful
	 */
	protected boolean moveModelElementUp(final int index) {
		if (getAssigned() instanceof List) {
			final List<  ?  > assigned = (List<  ?  >) getAssigned();
			Collections.rotate(assigned.subList(index - 1, index + 1), -1);
			assignedTableViewer.refresh();
			return true;
		}
		return false;
	}

	/**
	 * Move the "Assigned Listbox" selected objects down in the list.
	 *
	 * @param index the selected element index
	 * @return boolean depending if moving model element down was successful
	 */
	protected boolean moveModelElementDown(final int index) {
		if (getAssigned() instanceof List) {
			final List< ? > assigned = (List< ? >) getAssigned();
			Collections.rotate(assigned.subList(index, index + 2), 1);
			assignedTableViewer.refresh();
			return true;
		}
		return false;
	}

	/**
	 * Refilter the available list.
	 */
	private void reFilterAvailable() {
		if (getAvailableFilter() != null) {
			availableTableViewer.resetFilters();
			availableTableViewer.addFilter(getAvailableFilter());
		}
	}

	/**
	 * Gets all the Available objects from the database.
	 *
	 * @return available objects to assign
	 */
	public abstract Collection< ? > getAvailable();

	/**
	 * Gets all of the current model users's assigned objects.
	 *
	 * @return the current user's assigned objects
	 */
	public abstract Collection< ? > getAssigned();

	/**
	 * Gets a reference to a filter that will be used to filter what shows up in the Available listviewer.
	 *
	 * @return the AvailableListViewer's Filter
	 */
	public abstract ViewerFilter getAvailableFilter();

	/**
	 * Get assigned filter.
	 * @return viewer filter
	 */
	public ViewerFilter getAssignedFilter() {
		return null;

	}

	/**
	 * Refreshes button states.
	 * @param strSelection selected button
	 */
	protected void updateButtons(final IStructuredSelection strSelection) {
		if (!isStyleEnabled(UP_DOWN_BUTTONS)) {
			return;
		}
		if (strSelection == null || strSelection.isEmpty()) {
			EpControlFactory.changeEpState(moveUpButton, EpState.READ_ONLY);
			EpControlFactory.changeEpState(moveDownButton, EpState.READ_ONLY);
		} else {
			final int lastIndex = getAssigned().size() - 1;
			if (strSelection.size() > 1) {
				EpControlFactory.changeEpState(moveUpButton, EpState.READ_ONLY);
				EpControlFactory.changeEpState(moveDownButton, EpState.READ_ONLY);
			} else {
				final boolean enableUpButton = epState == EpState.EDITABLE && assignedTableViewer.getTable().getSelectionIndex() != 0;
				final boolean enableDownButton = epState == EpState.EDITABLE && assignedTableViewer.getTable().getSelectionIndex() != lastIndex;
				EpState enableUpButtonState = EpState.READ_ONLY;
				if (enableUpButton) {
					enableUpButtonState = EpState.EDITABLE;
				}
				EpControlFactory.changeEpState(moveUpButton, enableUpButtonState);
				EpState enableDownButtonState = EpState.READ_ONLY;
				if (enableDownButton) {
					enableDownButtonState = EpState.EDITABLE;
				}
				EpControlFactory.changeEpState(moveDownButton, enableDownButtonState);
			}
		}
	}

	/**
	 * Changes the state of the UI controls used by the dual list box.
	 *
	 * @param epState EpState
	 */
	public void changeState(final EpState epState) {
		this.epState = epState;
		EpControlFactory.changeEpState(addOneButton, epState);
		EpControlFactory.changeEpState(addAllButton, epState);
		EpControlFactory.changeEpState(removeOneButton, epState);
		EpControlFactory.changeEpState(removeAllButton, epState);
	}

	/**
	 * Sets the preferred height of the dual list controls.
	 *
	 * @param height the height in pixels
	 */
	public void setPreferredHeight(final int height) {
		final Object availableLayoutData = parentComposite.getSwtComposite().getLayoutData();
		if (availableLayoutData instanceof TableWrapData) {
			((TableWrapData) availableLayoutData).heightHint = height;
		} else
			if (availableLayoutData instanceof GridData) {
			((GridData) availableLayoutData).heightHint = height;
		}
	}

	/**
	 * Content provider for the list viewers.
	 */
	protected class DualListBoxContentProvider implements IStructuredContentProvider {

		@Override
		public void dispose() {
			// Do nothing
		}

		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
			// Do nothing
		}

		@Override
		public Object[] getElements(final Object inputElement) {
			return ((Collection< ? >) inputElement).toArray();
		}
	}

	/**
	 * Internal class for handling the selection events.
	 */
	private final SelectionListener selectionListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(final SelectionEvent event) {
			boolean didChange = false;

			if (event.getSource() == addOneButton) {
				didChange = handleAddOneEvent();
			} else if (event.getSource() == removeOneButton) {
				didChange = handleRemoveOneEvent();
			} else if (event.getSource() == addAllButton) {
				didChange = handleAddAllEvent();
			} else if (event.getSource() == removeAllButton) {
				didChange = handleRemoveAllEvent();
			} else if (event.getSource() == moveUpButton) {
				didChange = handleUpEvent();
			} else if (event.getSource() == moveDownButton) {
				didChange = handleDownEvent();
			}

			if (didChange) {

				if (controlModificationListener != null) {
					controlModificationListener.controlModified();
				}

				fireChangeEvent();
				updateButtons((IStructuredSelection) assignedTableViewer.getSelection());
			}
		}
	};

	private ControlModificationListener controlModificationListener;

		/**
		 * The action to execute when the remove all button is clicked.
		 * @return boolean depending on whether the removal of the elements from the model was successful
		 */
		protected boolean handleRemoveAllEvent() {

			LOG.debug("REMOVE_ALL button pressed"); //$NON-NLS-1$
			final List<Object> elementsToRemove = new ArrayList<Object>();
			final Iterator< ? > assigned = AbstractEpDualListBoxControl.this.getAssigned().iterator();
			while (assigned.hasNext()) {
				elementsToRemove.add(assigned.next());
			}
			final boolean success = AbstractEpDualListBoxControl.this.removeFromModel(new StructuredSelection(elementsToRemove));
			AbstractEpDualListBoxControl.this.reFilterAvailable();
			availableTableViewer.refresh();
			assignedTableViewer.refresh();

			return success;
		}

		/**
		 * The action to execute when the add all button is clicked.
		 * @return boolean depending on whether the addition of the elements to the model was successful
		 */
		protected boolean handleAddAllEvent() {

			LOG.debug("ADD_ALL button pressed"); //$NON-NLS-1$
			Object[] elements = new Object[0];
			if (getAvailableFilter() != null) {
				final Object[] availableArray = AbstractEpDualListBoxControl.this.getAvailable().toArray();
				elements = getAvailableFilter().filter(availableTableViewer, (Object) null, availableArray);
			}
			final boolean success = AbstractEpDualListBoxControl.this.assignToModel(new StructuredSelection(Arrays.asList(elements)));
			AbstractEpDualListBoxControl.this.reFilterAvailable();
			availableTableViewer.refresh();
			assignedTableViewer.refresh();

			return success;
		}

		/**
		 * The action to execute when the remove button is clicked.
		 * @return boolean depending on whether the removal of the element from the model was successful
		 */
		protected boolean handleRemoveOneEvent() {

			LOG.debug("REMOVE button pressed"); //$NON-NLS-1$
			// Remove the selection from the model
			final boolean success = AbstractEpDualListBoxControl.this.removeFromModel((IStructuredSelection) assignedTableViewer.getSelection());
			// Reset the input to the contentProvider.
			AbstractEpDualListBoxControl.this.reFilterAvailable();
			availableTableViewer.refresh();
			assignedTableViewer.refresh();
			return success;
		}

		/**
		 * The action to execute when the add button is clicked.
		 * @return boolean depending on whether the addition of the element to the model was successful
		 */
		protected boolean handleAddOneEvent() {

			LOG.debug("ADD button pressed"); //$NON-NLS-1$
			// We can cast to IStructuredSelection since we're using a Structured Viewer.
			// Add the selection to the model
			final boolean success = AbstractEpDualListBoxControl.this.assignToModel((IStructuredSelection) availableTableViewer.getSelection());
			// Reset the input to the contentProvider.
			AbstractEpDualListBoxControl.this.reFilterAvailable();
			availableTableViewer.refresh();
			assignedTableViewer.refresh();
			return success;
		}

		/**
		 * The action to execute when the move element down button is clicked.
		 * @return boolean depending on whether the move of the element down in the model was successful
		 */
		private boolean handleDownEvent() {
			final IStructuredSelection strSelection = (IStructuredSelection) assignedTableViewer.getSelection();
			if (strSelection != null && !strSelection.isEmpty()) {
				return AbstractEpDualListBoxControl.this.moveModelElementDown(assignedTableViewer.getTable().getSelectionIndex());
			}
			return false;
		}

		/**
		 * The action to execute when the move element up button is clicked.
		 * @return boolean depending on whether the move of the element up in the model was successful
		 */
		private boolean handleUpEvent() {
			final IStructuredSelection strSelection = (IStructuredSelection) assignedTableViewer.getSelection();
			if (strSelection != null && !strSelection.isEmpty()) {
				return AbstractEpDualListBoxControl.this.moveModelElementUp(assignedTableViewer.getTable().getSelectionIndex());
			}
			return false;
		}

	/**
	 * Selection changed listener.
	 */
	private class SelectionChangedListenerImpl implements ISelectionChangedListener {

		/**
		 * @param event
		 */
		public void selectionChanged(final SelectionChangedEvent event) {
			final IStructuredSelection strSelection = (IStructuredSelection) event.getSelection();
			updateButtons(strSelection);
		}
	}

	/**
	 * Registers a change listener.
	 *
	 * @param listener the listener
	 */
	public void registerChangeListener(final IDualListChangeListener listener) {
		listeners.add(listener);
	}

	/**
	 * Check if the list actually changed, and if it did then fire a
	 * change event to all listeners that are subscribed.
	 */
	private void fireChangeEvent() {
		for (final IDualListChangeListener listener : listeners) {
			listener.listChanged();
		}
	}

	/**
	 * @return assigned table viewer
	 */
	public TableViewer getAssignedTableViewer() {
		return assignedTableViewer;
	}

	/**
	 * @return available table viewer
	 */
	public TableViewer getAvailableTableViewer() {
		return availableTableViewer;
	}

	/**
	 * Sets the listener that will be notified on list change events (add, remove objects).
	 *
	 * @param controlModificationListener the modification listener
	 */
	public void setControlModificationListener(final ControlModificationListener controlModificationListener) {
		this.controlModificationListener = controlModificationListener;
	}
}
/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.dialog.value.dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.helpers.IValueRetriever;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;

/**
 * The dialog class for editing values of type short text with multi value enabled.
 */
@SuppressWarnings({ "PMD.GodClass" })
public class ShortTextMultiValueDialog extends AbstractValueDialog<List<String>>
		implements SelectionListener, ISelectionChangedListener,
		IValueRetriever {

	private static final String MULTI_VALUE_TABLE = "Multi Value Table"; //$NON-NLS-1$

	/**
	 * Content provider for price list.
	 */
	class ShortTextValueContentProvider implements IStructuredContentProvider {

		@Override
		public void dispose() {
			// Empty
		}

		/**
		 * Returns an array of elements to display.
		 *
		 * @param inputElement
		 *            the input element (A Product)
		 * @return an array of product price tiers
		 */
		public Object[] getElements(final Object inputElement) {
			return (Object[]) inputElement;
		}

		/**
		 * Called when the viewer input is changed.
		 *
		 * @param viewer
		 *            the viewer
		 * @param oldInput
		 *            the old input
		 * @param newInput
		 *            the new input
		 */
		public void inputChanged(final Viewer viewer, final Object oldInput,
				final Object newInput) {
			// No action required
		}
	}

	/**
	 * Provides the column image for the price table. The image may soon be
	 * removed.
	 */
	class ShortTextValueLabelProvider extends LabelProvider implements
			ITableLabelProvider {

		/**
		 * Get the column image.
		 *
		 * @param element
		 *            not used
		 * @param columnIndex
		 *            the column to create an image for
		 * @return the image
		 */
		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			return (String) element;
		}
	}

	private static final int COLUMN_WIDTH = 350;
	private Button addButton;
	private Button editButton;
	private IEpLayoutComposite mainComposite;
	private Button removeButton;
	private String selectedValue;
	private IEpTableViewer shortTextValueTableViewer;

	/**
	 * The constructor of the dialog class.
	 * @param parentShell the parent of the this dialog.
	 * @param value the values to be edited.
	 * @param editMode true for edit mode, false for add short text values.
	 * @param valueRequired true if value is required, false is value is not required
	 */
	public ShortTextMultiValueDialog(final Shell parentShell,
			final List<String> value, final boolean editMode, final boolean valueRequired) {
		super(parentShell, value, editMode, valueRequired);
	}

	/**
	 * The constructor of the dialog class.
	 * @param parentShell the parent of the this dialog.
	 * @param value the values to be edited.
	 * @param editMode true for edit mode, false for add short text values.
	 * @param valueRequired true if value is required, false is value is not required
	 * @param label the label for this value
	 * @param isLabelBold true if label needs to be bold, false for normal font labels
	 */
	public ShortTextMultiValueDialog(final Shell parentShell,
		final List<String> value, final boolean editMode,
			final boolean valueRequired, final String label, final boolean isLabelBold) {
		super(parentShell, value, editMode, valueRequired, label, isLabelBold);
	}

	/**
	 * Determines whether value already exists in the short text values.
	 * @param value value to be checked within the list
	 * @return true if exists, false if it does not exist
	 */
	private boolean isValueDuplicate(final String value) {
		return this.getValue() != null && this.getValue().contains(value);
	}

	/**
	 * Determines whether value is valid.
	 * @param value value to be checked within the list
	 * @return true if not null and not empty, false otherwise
	 */
	private boolean isValueValid(final String value) {
		return (value != null) && (!"".equals(value.trim())); //$NON-NLS-1$
	}

	private void addAction() {
		final ShortTextDialog dialog = new ShortTextDialog(getShell(),
				null, false, false, CoreMessages.get().AttributeType_ShortText, true);
		final int result = dialog.open();
		if (result == Window.OK) {

			final String dialogValue = dialog.getValue();

			if (isValueValid(dialogValue)) {
				if (isValueDuplicate(dialogValue)) {
					createValueExistsErrorDialog();
					return;
				}
				if (this.getValue() == null) {
					this.setValue(new ArrayList<String>());
				}
				this.getValue().add(dialogValue);

				refreshViewer();

			} else {
				createValueRequiredErrorDialog();
			}

		}
	}

	/**
	 * Creates an error dialog if the new value that is to be added, or edited from
	 * an old value already exists in the short text values.
	 */
	private void createValueExistsErrorDialog() {
		MessageDialog.openError(mainComposite.getSwtComposite().getShell(),
			CoreMessages.get().ValidationError_Duplicate,
				CoreMessages.get().ValidationError_Duplicate);
	}

	/**
	 * Creates an error dialog if the new value that is to be added, or edited from
	 * an old value is null or an empty String.
	 */
	private void createValueRequiredErrorDialog() {
		MessageDialog.openError(mainComposite.getSwtComposite().getShell(),
			CoreMessages.get().EpValidatorFactory_ValueRequired,
				CoreMessages.get().EpValidatorFactory_ValueRequired);
	}

	/**
	 * Adds buttons to the right hand side of the section.
	 *
	 * @param epState the current security state.
	 * @param buttonComposite the composite of the button area.
	 */
	protected void addButtons(final IEpLayoutComposite buttonComposite,
			final EpState epState) {
		IEpLayoutData buttonData = buttonComposite.createLayoutData(
				IEpLayoutData.FILL, IEpLayoutData.FILL);
		editButton = buttonComposite.addPushButton(
				CoreMessages.get().ShortTextMultiValueDialog_EditButton,
				CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_EDIT),
				epState, buttonData);
		editButton.setEnabled(false);
		editButton.addSelectionListener(this);
		addButton = buttonComposite.addPushButton(
				CoreMessages.get().ShortTextMultiValueDialog_AddButton,
				CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_ADD),
				epState, buttonData);
		addButton.addSelectionListener(this);
		removeButton = buttonComposite.addPushButton(
				CoreMessages.get().ShortTextMultiValueDialog_RemoveButton,
				CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_REMOVE),
				epState, buttonData);
		removeButton.setEnabled(false);
		removeButton.addSelectionListener(this);

		if (epState != EpState.EDITABLE) {
			addButton.setEnabled(false);
			editButton.setEnabled(false);
			removeButton.setEnabled(false);
		}
	}

	@Override
	protected void bindControls() {
		// do nothing here.
	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {
		final IEpLayoutData tableData = dialogComposite.createLayoutData(
				IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, true);

		shortTextValueTableViewer = dialogComposite.addTableViewer(false,
			EpState.READ_ONLY, tableData, MULTI_VALUE_TABLE);

		final IEpLayoutComposite buttonsComposite = dialogComposite
				.addGridLayoutComposite(1, false, null);
		addButtons(buttonsComposite, EpState.EDITABLE);
		mainComposite = dialogComposite;

	}

	private void customizeButtonLabel() {
		getOkButton().setText(CoreMessages.get().AbstractEpDialog_ButtonOK);
		getOkButton().setImage(null);
		getOkButton().setAlignment(SWT.CENTER);
		getOkButton().redraw();
	}

	/**
	 * Dispose.
	 */
	public void dispose() {
		addButton.removeSelectionListener(this);
		editButton.removeSelectionListener(this);
		removeButton.removeSelectionListener(this);
	}

	private void editAction() {
		final ShortTextDialog dialog = new ShortTextDialog(getShell(),
				getSelectedValue(), true, false, CoreMessages.get().AttributeType_ShortText, true);
		final int result = dialog.open();

		if (result == Window.OK) {

			final String dialogValue = dialog.getValue();

			if (isValueValid(dialogValue)) {

				final String selectedValue = this.getSelectedValue();

				if (!dialogValue.equals(selectedValue)) {
					if (isValueDuplicate(dialogValue)) {
						createValueExistsErrorDialog();
						return;
					}
					List<String> values = this.getValue();
					final boolean success = editActionSetValueIfNotNull(dialogValue, selectedValue, values);
					if (!success) {
						// if we are here then selection did not match. add maybe??
						values.add(dialogValue);
					}
				}

				refreshViewer();

			} else {
				createValueRequiredErrorDialog();
			}

		}

	}

	/**
	 * replace edited value if it is found.
	 * @param dialogValue the new value
	 * @param selectedValue the old value
	 * @param values the list of values
	 * @return true if value was replaced, false otherwise
	 */
	private boolean editActionSetValueIfNotNull(final String dialogValue,
			final String selectedValue, final List<String> values) {
		if (values != null) {
			for (int i = 0; i < values.size(); i++) {
				if (selectedValue.equals(values.get(i))) {
					values.set(i, dialogValue);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	protected String getInitialMessage() {
		// no initial message is required for the dialog.
		return null;
	}

	/**
	 * The getter of the selectedValue attribute.
	 * @return the value of the selectedValue field of the dialog which is the selected entry of the value table.
	 */
	public String getSelectedValue() {
		return selectedValue;
	}

	private Object getTableInput() {
		if (this.getValue() != null) {
			return this.getValue().toArray();
		}
		return null;
	}


	@Override
	protected void populateControls() {
		shortTextValueTableViewer.addTableColumn(
				CoreMessages.get().ShortTextMultiValueDialog_Value,
				COLUMN_WIDTH);
		shortTextValueTableViewer
				.setContentProvider(new ShortTextValueContentProvider());
		shortTextValueTableViewer
				.setLabelProvider(new ShortTextValueLabelProvider());
		customizeButtonLabel();
		// addColumnHeader();
		shortTextValueTableViewer.setInput(getTableInput());
		shortTextValueTableViewer.getSwtTableViewer()
				.addSelectionChangedListener(this);

	}

	private void refreshViewer() {
		shortTextValueTableViewer.setInput(getTableInput());
		shortTextValueTableViewer.getSwtTableViewer().refresh();
		mainComposite.getSwtComposite().layout(true);
		mainComposite.getSwtComposite().redraw();
	}

	@Override
	public void selectionChanged(final SelectionChangedEvent event) {
		final IStructuredSelection selection = (IStructuredSelection) (shortTextValueTableViewer
				.getSwtTableViewer().getSelection());
		if (selection == null || selection.isEmpty()) {
			return;
		}
		setSelectedValue((String) selection.getFirstElement());
		updateButtonStatus();
	}

	/**
	 * The setter of selectedValue field.
	 * @param selectedValue the vale to be setted.
	 */
	public void setSelectedValue(final String selectedValue) {
		this.selectedValue = selectedValue;
	}

	private void updateButtonStatus() {
		editButton.setEnabled(true);
		removeButton.setEnabled(true);

	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		widgetSelected(event);
	}

	@Override
	public void widgetSelected(final SelectionEvent event) {

		if (event.getSource() == addButton) {
			addAction();
		}
		if (event.getSource() == removeButton) {
			removeAction();
		}
		if (event.getSource() == editButton) {
			editAction();
		}
	}

	private void removeAction() {
		String theString = getSelectedValue();

		final boolean confirm = MessageDialog.openConfirm(getShell(),
				CoreMessages.get().ShortTextMultiValueDialog_RemoveConfirmTitle,

				NLS.bind(CoreMessages.get().ShortTextMultiValueDialog_RemoveConfirmMsg,
				theString));

		if (!confirm) {
			return;
		}

		List<String> values = ShortTextMultiValueDialog.this.getValue();
		if (values != null) {
			for (int i = 0; i < values.size(); i++) {
				if (theString.equals(values.get(i))) {
					values.remove(i);
					refreshViewer();
					return;
				}
			}
		}
		
	}

	@Override
	protected String getAddTitle() {
		return CoreMessages.get().ShortTextMultiValueDialog_AddTitle;
	}

	@Override
	protected String getAddWindowTitle() {
		return CoreMessages.get().ShortTextMultiValueDialog_AddWindowTitle;
	}

	@Override
	protected String getEditTitle() {
		return CoreMessages.get().ShortTextMultiValueDialog_EditTitle;
	}

	@Override
	protected String getEditWindowTitle() {
		return CoreMessages.get().ShortTextMultiValueDialog_EditWindowTitle;
	}
}

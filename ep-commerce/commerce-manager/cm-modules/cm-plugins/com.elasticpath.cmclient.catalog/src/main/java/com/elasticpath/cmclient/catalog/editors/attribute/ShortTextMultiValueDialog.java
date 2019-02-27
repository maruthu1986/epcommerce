/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.editors.attribute;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.helpers.IValueRetriever;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.domain.attribute.AttributeMultiValueType;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.AttributeValueWithType;
import com.elasticpath.domain.attribute.impl.AbstractAttributeValueImpl;

/**
 * The dialog class for editing attribute values of type short text with multi value enabled.
 * @author rliu
 *
 */
@SuppressWarnings({"restriction", "PMD.GodClass", "PMD.PrematureDeclaration"})
public class ShortTextMultiValueDialog extends AbstractEpDialog
		implements SelectionListener, ISelectionChangedListener,
		IValueRetriever {

	private static final String SHORT_TEXT_TABLE = "Short Text";
	private static final int TABLE_HEIGHT = 125;

	/**
	 * Provides the column image for the price table. The image may soon be
	 * removed.
	 */
	class ShortTextValueLabelProvider extends LabelProvider implements
			ITableLabelProvider {

		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			// final PriceTier priceTier = (PriceTier) element;
			//	
			if (element == null) {
				return CatalogMessages.get().Product_NotAvailable;
			}
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
	private List<String> values;
	private final AttributeMultiValueType multiValueType;
	
	/**
	 * The constructor of the dialog class.
	 * @param parentShell the parent of the this dialog.
	 * @param attributeValue the attributeValue object to be edited.
	 */
	public ShortTextMultiValueDialog(final Shell parentShell, final AttributeValue attributeValue) {
		super(parentShell, 2, false);
		multiValueType = attributeValue.getAttribute().getMultiValueType();
		values = ((AttributeValueWithType) attributeValue).getShortTextMultiValues();
	}

	private void addAction() {
		final ShortTextDialog dialog = new ShortTextDialog(getShell(),
				null, false);
		final int result = dialog.open();
		if (result != Window.OK) {
			return;
		}
		final String addedValue = dialog.getValue();

		if (addedValue == null) {
			return;
		}

		if (isValueDuplicate(addedValue)) {
			createValueExistsErrorDialog();
		} else {
			List<String> newValues = new ArrayList<>();
			newValues.addAll(getShortTextValues());
			newValues.add(addedValue);
			setShortTextValues(newValues);
			refreshViewer();
		}
	}

	/**
	 * Creates an error dialog if the new value that is to be added, or edited from
	 * an old value already exists in the short text values.
	 */
	private void createValueExistsErrorDialog() {
		MessageDialog.openError(mainComposite.getSwtComposite().getShell(), 
				CatalogMessages.get().ShortTextMultiValueDialog_ErrorDialog_Dupl_Title,
				CatalogMessages.get().ShortTextMultiValueDialog_ErrorDialog_Dupl_Body);
	}

	/**
	 * Determines whether value already exists in the short text values.
	 * @param addedValue value to be checked within the list
	 * @return true if exists, false if it does not exist
	 */
	private boolean isValueDuplicate(final String addedValue) {
		return getShortTextValues().contains(addedValue);
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
				CatalogMessages.get().ShortTextMultiValueDialog_EditValue,
				CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_EDIT),
				epState, buttonData);
		editButton.setEnabled(false);
		editButton.addSelectionListener(this);
		addButton = buttonComposite.addPushButton(
				CatalogMessages.get().ShortTextMultiValueDialog_AddValue,
				CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_ADD),
				epState, buttonData);
		addButton.addSelectionListener(this);
		removeButton = buttonComposite.addPushButton(
				CatalogMessages.get().ShortTextMultiValueDialog_RemoveValue,
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
				EpState.EDITABLE, tableData, SHORT_TEXT_TABLE);

		GridData shortTextValueLayoutData = (GridData) shortTextValueTableViewer.getSwtTable().getLayoutData();
		shortTextValueLayoutData.heightHint = TABLE_HEIGHT;

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
	 * Remove listeners.
	 */
	public void dispose() {
		addButton.removeSelectionListener(this);
		editButton.removeSelectionListener(this);
		removeButton.removeSelectionListener(this);
	}

	private void editAction() {
		final ShortTextDialog dialog = new ShortTextDialog(getShell(),
				getSelectedValue(), true);
		String theString = getSelectedValue();
		final int result = dialog.open();
		if (result != Window.OK || theString.equals(dialog.getValue())) {
			return;
		}

		//If the value already exists within the list, we should 
		//display an error message and return 
		if (isValueDuplicate(dialog.getValue())) {
			createValueExistsErrorDialog();
			return;
		} 
		
		List<String> editedList = new ArrayList<>();
		for (String element : getShortTextValues()) {
			if (element.equals(theString)) {
				editedList.add(dialog.getValue());
			} else {
				editedList.add(element);
			}
		}

		setShortTextValues(editedList);
		refreshViewer();
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

	private List<String> getShortTextValues() {
		return values;
	}

	@Override
	protected String getTitle() {
		return CatalogMessages.get().ShortTextMultiValueDialog_Title;
	}

	@Override
	public Object getValue() {
		return AbstractAttributeValueImpl.buildShortTextMultiValues(getShortTextValues(), multiValueType);
	}


	@Override
	protected Image getWindowImage() {
		// no image is required.
		return null;
	}

	@Override
	protected String getWindowTitle() {
		return CatalogMessages.get().ShortTextMultiValueDialog_WinTitle;
	}


	@Override
	protected String getPluginId() {
		return CatalogPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return getValue();
	}

	@Override
	protected void populateControls() {
		shortTextValueTableViewer.addTableColumn(
				CatalogMessages.get().ShortTextMultiValueDialog_Value,
				COLUMN_WIDTH);
		shortTextValueTableViewer
				.setContentProvider(new ArrayContentProvider());
		shortTextValueTableViewer
				.setLabelProvider(new ShortTextValueLabelProvider());
		customizeButtonLabel();
		
		shortTextValueTableViewer.setInput(getShortTextValues().toArray());
		shortTextValueTableViewer.getSwtTableViewer()
				.addSelectionChangedListener(this);

	}

	private void refreshViewer() {
		shortTextValueTableViewer.setInput(getShortTextValues().toArray());
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
	 * @param selectedValue the value to be selected.
	 */
	public void setSelectedValue(final String selectedValue) {
		this.selectedValue = selectedValue;
	}

	/**
	 * the multi short text value for the attribute.
	 * @param shortTextValues the string list to be set to the attribute.
	 */
	public void setShortTextValues(final List<String> shortTextValues) {
		values = shortTextValues;
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

		if (shortTextValueTableViewer.getSwtTable().getItemCount() == 1) {
			MessageDialog
					.openWarning(
							getShell(),
							CatalogMessages.get().ShortTextMultiValueDialog_RemoveWarningTitle,
							CatalogMessages.get().ShortTextMultiValueDialog_RemoveWarningMsg);
			return;
		}

		final boolean confirm = MessageDialog.openConfirm(getShell(),
				CatalogMessages.get().ShortTextMultiValueDialog_RemoveConfirmTitle,
				CatalogMessages.get().ShortTextMultiValueDialog_RemoveConfirmMsg
						+ getSelectedValue());

		if (!confirm) {
			return;
		}

		List<String> listAfterRemoveAction = new ArrayList<>();
		for (String element : getShortTextValues()) {
			if (element.equals(theString)) {
				continue;
			}
			listAfterRemoveAction.add(element);
		}

		setShortTextValues(listAfterRemoveAction);

		shortTextValueTableViewer.setInput(listAfterRemoveAction.toArray());
		shortTextValueTableViewer.getSwtTableViewer().refresh();
	}
}

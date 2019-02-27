/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.dialog.value.dialog;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.helpers.IValueRetriever;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;

/**
 * The dialog box for editing boolean attributes.
 */
public class BooleanDialog extends AbstractValueDialog<Boolean> implements IValueRetriever {

	/**
	 * The Combo box for boolean value editing, toggling Yes/No.
	 */
	private CCombo valueCombo;

	/**
	 * The constructor of the boolean dialog window.
	 * 
	 * @param parentShell the parent shell object of the dialog window
	 * @param value the value passed in
	 * @param editMode true for edit mode, false for add short text values.
	 * @param valueRequired true if value is required, false is value is not required
	 */
	public BooleanDialog(final Shell parentShell, 
			final Boolean value, final boolean editMode, final boolean valueRequired) {
		super(parentShell, value, editMode, valueRequired);
	}
	
	/**
	 * The constructor of the boolean dialog window.
	 * 
	 * @param parentShell the parent shell object of the dialog window
	 * @param value the boolean adapter for value passed in
	 * @param editMode true for edit mode, false for add short text values.
	 * @param valueRequired true if value is required, false is value is not required
	 * @param label the label for this value
	 * @param isLabelBold true if label needs to be bold, false for normal font labels
	 */
	public BooleanDialog(final Shell parentShell, 
			final Boolean value, final boolean editMode, final boolean valueRequired,
			final String label, final boolean isLabelBold) {
		super(parentShell, value, editMode, valueRequired, label, isLabelBold);
	}
	

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {
		super.createEpDialogContent(dialogComposite); // default label from adapter
		this.valueCombo = dialogComposite.addComboBox(EpState.EDITABLE, dialogComposite.createLayoutData(IEpLayoutData.FILL, 
				IEpLayoutData.CENTER, true, true));
	}

	@Override
	protected String getEditTitle() {
		return CoreMessages.get().BooleanDialog_EditTitle;
	}
	
	@Override
	protected String getAddTitle() {
		return CoreMessages.get().BooleanDialog_AddTitle;
	}

	@Override
	protected String getEditWindowTitle() {
		return CoreMessages.get().BooleanDialog_EditWindowTitle;
	}
	
	@Override
	protected String getAddWindowTitle() {
		return CoreMessages.get().BooleanDialog_AddWindowTitle;
	}

	@Override
	protected void populateControls() {
		this.valueCombo
				.setItems(new String[] { CoreMessages.get().Boolean_true, CoreMessages.get().Boolean_false });
		if (this.getValue() == null) {
			valueCombo.setText(CoreMessages.get().Boolean_true);
			return;
		}
		if (this.getValue()) {
			valueCombo.setText(CoreMessages.get().Boolean_true);
		} else {
			valueCombo.setText(CoreMessages.get().Boolean_false);
		}
	}

	@Override
	protected void okPressed() {
		if (this.valueCombo.getSelectionIndex() == 0) {
			// Yes
			this.setValue(Boolean.TRUE);
		} else {
			// No
			this.setValue(Boolean.FALSE);
		}
		
		super.okPressed();
	}
}

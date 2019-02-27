/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.dialog.value.dialog;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.helpers.IValueRetriever;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;

/**
 * * The dialog box for editing integer values.
 */
public class IntegerDialog extends AbstractValueDialog<Integer> implements IValueRetriever {
	/**
	 * the spinner for integer value input.
	 */
	private Spinner spinnerField;

	private static final int MAX_INTEGER_ALLOWED = 1000000000;

	/**
	 * The constructor of the integer dialog window.
	 * 
	 * @param parentShell the parent shell object of the dialog window
	 * @param value the integer value passed in
	 * @param editMode true for edit mode, false for add short text values.
	 * @param valueRequired true if value is required, false is value is not required
	 */
	public IntegerDialog(final Shell parentShell, 
			final Integer value, final boolean editMode, final boolean valueRequired) {
		super(parentShell, value, editMode, valueRequired);
		if (this.getValue() == null) {
			this.setValue(0);
		} 
	}

	/**
	 * The constructor of the integer dialog window.
	 * 
	 * @param parentShell the parent shell object of the dialog window
	 * @param value the integer value passed in
	 * @param editMode true for edit mode, false for add short text values.
	 * @param valueRequired true if value is required, false is value is not required
	 * @param label the label for this value
	 * @param isLabelBold true if label needs to be bold, false for normal font labels
	 */
	public IntegerDialog(final Shell parentShell, 
			final Integer value, final boolean editMode, 
			final boolean valueRequired, final String label, final boolean isLabelBold) {
		super(parentShell, value, editMode, valueRequired, label, isLabelBold);
		if (this.getValue() == null) {
			this.setValue(0);
		} 
	}
	
	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {
		super.createEpDialogContent(dialogComposite); // default label from adapter
		this.spinnerField = dialogComposite.addSpinnerField(EpState.EDITABLE, dialogComposite.createLayoutData(IEpLayoutData.FILL,
				IEpLayoutData.CENTER, true, true));
		this.spinnerField.setMaximum(MAX_INTEGER_ALLOWED);
	}

	@Override
	protected void okPressed() {

		this.setValue(spinnerField.getSelection());
		super.okPressed();

	}

	@Override
	protected String getEditTitle() {
		return CoreMessages.get().IntegerDialog_EditTitle;
	}
	
	@Override
	protected String getAddTitle() {
		return CoreMessages.get().IntegerDialog_AddTitle;
	}

	@Override
	protected String getEditWindowTitle() {
		return CoreMessages.get().IntegerDialog_EditWindowTitle;
	}

	@Override
	protected String getAddWindowTitle() {
		return CoreMessages.get().IntegerDialog_AddWindowTitle;
	}

	
	@Override
	protected void populateControls() {
		this.spinnerField.setSelection(this.getValue());
	}
}

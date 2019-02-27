/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.core.dialog.value.dialog;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.validation.EpUrlValidator;

/**
 * Create input dialog window for simple URL (Text Field with URL regex).
 */
public class SimpleHttpURLDialog extends ShortTextDialog {

	private final EpUrlValidator validator = new EpUrlValidator();
		
	/**
	 * @param parentShell the parent shell object to create this dialog.
	 * @param value the string value passed to create this dialog.
	 * @param editMode true for edit mode, false for add short text values.
	 * @param valueRequired true if value is required, false is value is not required
	 */
	public SimpleHttpURLDialog(final Shell parentShell, final String value, final boolean editMode, final boolean valueRequired) {
		super(parentShell, value, editMode, valueRequired);
	}

	/**
	 * @param parentShell the parent shell object to create this dialog.
	 * @param value the string value passed to create this dialog.
	 * @param editMode true for edit mode, false for add short text values.
	 * @param valueRequired true if value is required, false is value is not required
	 * @param label the label for this value
	 * @param isLabelBold true if label needs to be bold, false for normal font labels
	 */
	public SimpleHttpURLDialog(final Shell parentShell, final String value, final boolean editMode, 
			final boolean valueRequired, final String label, final boolean isLabelBold) {
		super(parentShell, value, editMode, valueRequired, label, isLabelBold);
	}

	@Override
	public void validateAction(final String stringValue) {
		super.validateAction(stringValue);
		IStatus status = validator.validate(stringValue);
		if (!status.isOK()) {
			this.setErrorMessage(status.getMessage());
			getOkButton().setEnabled(false);
			return;
		}
	}
	
	@Override
	protected String getEditTitle() {
		return CoreMessages.get().UrlDialog_EditTitle;
	}

	@Override
	protected String getAddTitle() {
		return CoreMessages.get().UrlDialog_AddTitle;
	}

	@Override
	protected String getEditWindowTitle() {
		return CoreMessages.get().UrlDialog_EditWindowTitle;
	}

	@Override
	protected String getAddWindowTitle() {
		return CoreMessages.get().UrlDialog_AddWindowTitle;
	}
}

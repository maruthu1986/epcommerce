/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.dialog.value.dialog;

import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.CoreMessages;
/**
 * * The dialog box for editing long text values.
 */
public class LongTextDialog extends ShortTextDialog {
	
	private static final int MAX_TEXT_LENGTH = 4000;
	
	private static final int TEXT_AREA_HEIGHT = 250;
	
	/**
	 * The constructor of the long text dialog window.
	 * 
	 * @param parentShell the parent shell object of the dialog window
	 * @param value the long text string adapter for value passed in
	 * @param editMode true for edit mode, false for add short text values.
	 * @param valueRequired true if value is required, false is value is not required
	 */
	public LongTextDialog(final Shell parentShell, 
			final String value, final boolean editMode, final boolean valueRequired) {
		super(parentShell, value, editMode, valueRequired);
	}
	
	/**
	 * The constructor of the long text dialog window.
	 * 
	 * @param parentShell the parent shell object of the dialog window
	 * @param value the long text string adapter for value passed in
	 * @param editMode true for edit mode, false for add short text values.
	 * @param valueRequired true if value is required, false is value is not required
	 * @param label the label for this value
	 * @param isLabelBold true if label needs to be bold, false for normal font labels
	 */
	public LongTextDialog(final Shell parentShell, 
			final String value, final boolean editMode, 
			final boolean valueRequired, final String label, final boolean isLabelBold) {
		super(parentShell, value, editMode, valueRequired, label, isLabelBold);
	}

	@Override
	protected String getEditTitle() {
		return CoreMessages.get().LongTextDialog_EditTitle;
	}
	
	@Override
	protected String getAddTitle() {
		return CoreMessages.get().LongTextDialog_AddTitle;
	}

	@Override
	protected String getEditWindowTitle() {
		return CoreMessages.get().LongTextDialog_EditWindowTitle;
	}
	
	@Override
	protected String getAddWindowTitle() {
		return CoreMessages.get().LongTextDialog_AddWindowTitle;
	}

	/**
	 * Returns max length of the text in the dialog box.
	 *  
	 * @return max length of the text in the dialog box
	 */
	protected int getMaxTextLength() {
		return MAX_TEXT_LENGTH;
	}
	
	/**
	 * Returns dialog edit area height.
	 * @return dialog edit area height
	 */
	protected int getTextAreaHeight() {
		return TEXT_AREA_HEIGHT;
	}
}

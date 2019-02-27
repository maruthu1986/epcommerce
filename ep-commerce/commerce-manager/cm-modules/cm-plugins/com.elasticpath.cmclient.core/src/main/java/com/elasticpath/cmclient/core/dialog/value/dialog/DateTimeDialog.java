/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.dialog.value.dialog;

import java.util.Date;

import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.helpers.IValueRetriever;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpDateTimePicker;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;

/**
 * Create the DateTime input dialog window (use IEpDateTimePicker.STYLE_* for different styles).
 */
public class DateTimeDialog extends AbstractValueDialog<Date> implements IValueRetriever {
	
	private IEpDateTimePicker valueText;

	private final int style;

	/**
	 * @param parentShell the parent shell passed in.
	 * @param value the Date value.
	 * @param style the styles defined in IEpDateTimePicker interface
	 * @param editMode true for edit mode, false for add short text values.
	 * @param valueRequired true if value is required, false is value is not required
	 */
	public DateTimeDialog(final Shell parentShell, 
			final Date value, final int style, final boolean editMode, final boolean valueRequired) {
		super(parentShell, value, editMode, valueRequired);
		if (this.getValue() == null) {
			this.setValue(new Date());
		}
		this.style = style;
	}
	
	/**
	 * @param parentShell the parent shell passed in.
	 * @param value the Date value.
	 * @param style the styles defined in IEpDateTimePicker interface
	 * @param editMode true for edit mode, false for add short text values.
	 * @param valueRequired true if value is required, false is value is not required
	 * @param label the label for this value
	 * @param isLabelBold true if label needs to be bold, false for normal font labels
	 */
	public DateTimeDialog(final Shell parentShell, 
			final Date value, final int style, final boolean editMode, 
			final boolean valueRequired, final String label, final boolean isLabelBold) {
		super(parentShell, value, editMode, valueRequired, label, isLabelBold);
		if (this.getValue() == null) {
			this.setValue(new Date());
		}
		this.style = style;
	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {
		super.createEpDialogContent(dialogComposite); // default label from adapter

		valueText = dialogComposite.addDateTimeComponent(style, EpState.EDITABLE, dialogComposite.createLayoutData(IEpLayoutData.FILL,
				IEpLayoutData.CENTER, true, true));

		this.valueText.setDate(this.getValue());
	}

	@Override
	protected void okPressed() {
		this.setValue(valueText.getDate());
		super.okPressed();

	}

	@Override
	protected String getEditTitle() {
		return CoreMessages.get().DateTimeDialog_EditTitle;
	}

	@Override
	protected String getAddTitle() {
		return CoreMessages.get().DateTimeDialog_AddTitle;
	}

	@Override
	protected String getEditWindowTitle() {
		return CoreMessages.get().DateTimeDialog_EditWindowTitle;
	}

	@Override
	protected String getAddWindowTitle() {
		return CoreMessages.get().DateTimeDialog_AddWindowTitle;
	}

	
	@Override
	protected void populateControls() {
		// not used
	}

}

/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.dialog.value.cell;

import java.util.Date;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpDateTimePicker;

/**
 * Date and time cell editor.
 */
public class DateTimeCellEditor extends AbstractDateCellEditor {

	/**
	 * Construct the Date and Time editor.
	 * @param parent the composite
	 * @param value the value
	 */
	public DateTimeCellEditor(final Composite parent, final Date value) {
		super(parent, value);
	}
	
	/**
	 * Construct the Date and Time editor.
	 * @param parent the composite
	 * @param value the value
	 * @param label the label
	 * @param isLabelBold the flag for style type
	 */
	public DateTimeCellEditor(final Composite parent, final Date value, final String label, final boolean isLabelBold) {
		super(parent, value, label, isLabelBold);
	}
	
	/**
	 * construct DateTimePicker with appropriate format.
	 * @param cellEditorWindow the cell editor
	 * @return date and time picker
	 */
	protected IEpDateTimePicker getDateTimePicker(final Control cellEditorWindow) {
		return EpControlFactory.getInstance().createDateTimeComponent(cellEditorWindow.getParent(),
				IEpDateTimePicker.STYLE_DATE_AND_TIME, EpState.EDITABLE);
	}

}

/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.dialog.value.cell;

import java.util.Date;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.elasticpath.cmclient.core.dialog.value.dialog.AbstractValueDialog;
import com.elasticpath.cmclient.core.ui.framework.IEpDateTimePicker;

/**
 * Date cell editor.
 */
public abstract class AbstractDateCellEditor extends AbstractCellEditor<Date> {

	/**
	 * Construct the Boolean editor.
	 * @param parent the composite
	 * @param value the value
	 */
	public AbstractDateCellEditor(final Composite parent, final Date value) {
		super(parent, value);
	}
	
	/**
	 * Construct the Boolean editor.
	 * @param parent the composite
	 * @param value the value
	 * @param label the label
	 * @param isLabelBold the flag for style type
	 */
	public AbstractDateCellEditor(final Composite parent, final Date value, final String label, final boolean isLabelBold) {
		super(parent, value, label, isLabelBold);
	}

	@Override
	protected Date openDialogBox(final Control cellEditorWindow) {
		// final EpDateTimePicker dateTimePicker = new
		// EpDateTimePicker(cellEditorWindow.getShell(),
		final IEpDateTimePicker dateTimePicker = getDateTimePicker(cellEditorWindow);

		dateTimePicker.setDate(this.getInitalValue());
		dateTimePicker.open(cellEditorWindow);

		return dateTimePicker.getDate();
	}

	@Override
	protected AbstractValueDialog<Date> getValueDialog(
			final Control cellEditorWindow) {
		throw new UnsupportedOperationException("DateTimePicker do not follow normal flow of execution"); //$NON-NLS-1$
	}
	
	/**
	 * construct DateTimePicker with appropriate format.
	 * @param cellEditorWindow the cell editor
	 * @return date and time picker
	 */
	protected abstract IEpDateTimePicker getDateTimePicker(final Control cellEditorWindow);

}

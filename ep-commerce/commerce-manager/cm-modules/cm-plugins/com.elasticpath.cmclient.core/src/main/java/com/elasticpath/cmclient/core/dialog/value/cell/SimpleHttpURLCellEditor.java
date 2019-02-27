/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.dialog.value.cell;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.elasticpath.cmclient.core.dialog.value.dialog.AbstractValueDialog;
import com.elasticpath.cmclient.core.dialog.value.dialog.SimpleHttpURLDialog;

/**
 * Simple Http URL (Text Field with URL regex) cell editor.
 */
public class SimpleHttpURLCellEditor extends AbstractCellEditor<String> {

	/**
	 * Construct the Simple Http URL (Text Field with URL regex) editor.
	 * @param parent the composite
	 * @param value the value
	 */
	public SimpleHttpURLCellEditor(final Composite parent, final String value) {
		super(parent, value);
	}
	
	/**
	 * Construct the Simple Http URL (Text Field with URL regex) editor.
	 * @param parent the composite
	 * @param value the value
	 * @param label the label
	 * @param isLabelBold the flag for style type
	 */
	public SimpleHttpURLCellEditor(final Composite parent, final String value, final String label, final boolean isLabelBold) {
		super(parent, value, label, isLabelBold);
	}
	
	/**
	 * Construct the Simple Http URL (Text Field with URL regex) editor.
	 * @param parent the composite
	 * @param value the value
	 * @param valueRequired true if value is required, false is value is not required
	 * @param label the label
	 * @param isLabelBold the flag for style type
	 */
	public SimpleHttpURLCellEditor(final Composite parent, final String value, 
			final boolean valueRequired, final String label, final boolean isLabelBold) {
		super(parent, value, valueRequired, label, isLabelBold);
	}
	
	@Override
	protected AbstractValueDialog<String> getValueDialog(
			final Control cellEditorWindow) {
		return new SimpleHttpURLDialog(
				cellEditorWindow.getShell(), this.getInitalValue(), true, this.isValueRequired(), this.getLabel(),
				this.isLabelBold());
	}

}

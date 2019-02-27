/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.dialog.value.cell;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.elasticpath.cmclient.core.dialog.value.dialog.AbstractValueDialog;
import com.elasticpath.cmclient.core.dialog.value.dialog.IntegerDialog;

/**
 * Integer cell editor.
 */
public class IntegerCellEditor extends AbstractCellEditor<Integer> {

	
	/**
	 * Construct the Integer editor.
	 * @param parent the composite
	 * @param value the value
	 */
	public IntegerCellEditor(final Composite parent, final Integer value) {
		super(parent, value);
	}
	
	/**
	 * Construct the Integer editor.
	 * @param parent the composite
	 * @param value the value
	 * @param label the label
	 * @param isLabelBold the flag for style type
	 */
	public IntegerCellEditor(final Composite parent, final Integer value, final String label, final boolean isLabelBold) {
		super(parent, value, label, isLabelBold);
	}
	
	/**
	 * Construct the Integer editor.
	 * @param parent the composite
	 * @param value the value
	 * @param valueRequired true if value is required, false is value is not required
	 * @param label the label
	 * @param isLabelBold the flag for style type
	 */
	public IntegerCellEditor(final Composite parent, final Integer value, 
			final boolean valueRequired, final String label, final boolean isLabelBold) {
		super(parent, value, valueRequired, label, isLabelBold);
	}
	
	@Override
	protected AbstractValueDialog<Integer> getValueDialog(
			final Control cellEditorWindow) {
		return new IntegerDialog(
				cellEditorWindow.getShell(), this.getInitalValue(), true, this.isValueRequired(), this.getLabel(),
				this.isLabelBold());
	}

}

/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.dialog.value.cell;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.elasticpath.cmclient.core.dialog.value.dialog.AbstractValueDialog;
import com.elasticpath.cmclient.core.dialog.value.dialog.BooleanDialog;

/**
 * Boolean cell editor.
 */
public class BooleanCellEditor extends AbstractCellEditor<Boolean>   {

	/**
	 * Construct the Boolean editor.
	 * @param parent the composite
	 * @param value the value
	 */
	public BooleanCellEditor(final Composite parent, final Boolean value) {
		super(parent, value);
	}
	
	/**
	 * Construct the Boolean editor.
	 * @param parent the composite
	 * @param value the value
	 * @param label the label
	 * @param isLabelBold the flag for style type
	 */
	public BooleanCellEditor(final Composite parent, final Boolean value, final String label, final boolean isLabelBold) {
		super(parent, value, label, isLabelBold);
	}

	/**
	 * Construct the Boolean editor.
	 * @param parent the composite
	 * @param value the value
	 * @param valueRequired true if value is required, false is value is not required
	 * @param label the label
	 * @param isLabelBold the flag for style type
	 */
	public BooleanCellEditor(final Composite parent, final Boolean value, 
			final boolean valueRequired, final String label, final boolean isLabelBold) {
		super(parent, value, valueRequired, label, isLabelBold);
	}
	
	@Override
	protected AbstractValueDialog<Boolean> getValueDialog(
			final Control cellEditorWindow) {
		return new BooleanDialog(
				cellEditorWindow.getShell(), this.getInitalValue(), true, this.isValueRequired(), this.getLabel(),
				this.isLabelBold());
	}

}
/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.dialog.value.cell;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.elasticpath.cmclient.core.dialog.value.dialog.AbstractValueDialog;
import com.elasticpath.cmclient.core.dialog.value.dialog.ShortTextMultiValueDialog;

/**
 * Short Text Multi Value cell editor.
 * Complex dialog with a sub dialog for managing a single entry.
 */
public class ShortTextMultiValueCellEditor extends AbstractCellEditor<List<String>> {

	/**
	 * Construct the Short Text Multi Value cell editor.
	 * @param parent the composite
	 * @param value the value
	 */
	public ShortTextMultiValueCellEditor(final Composite parent, final List<String> value) {
		super(parent, value);
	}
	
	/**
	 * Construct the Short Text Multi Value cell editor.
	 * @param parent the composite
	 * @param value the value
	 * @param label the label
	 * @param isLabelBold the flag for style type
	 */
	public ShortTextMultiValueCellEditor(final Composite parent, final List<String> value, final String label, final boolean isLabelBold) {
		super(parent, value, label, isLabelBold);
	}
	
	/**
	 * Construct the Short Text Multi Value cell editor.
	 * @param parent the composite
	 * @param value the value
	 * @param valueRequired true if value is required, false is value is not required
	 * @param label the label
	 * @param isLabelBold the flag for style type
	 */
	public ShortTextMultiValueCellEditor(final Composite parent, final List<String> value, 
			final boolean valueRequired, final String label, final boolean isLabelBold) {
		super(parent, value, valueRequired, label, isLabelBold);
	}
	
	@Override
	protected AbstractValueDialog<List<String>> getValueDialog(
			final Control cellEditorWindow) {
		return new ShortTextMultiValueDialog(
				cellEditorWindow.getShell(), this.getInitalValue(), true, this.isValueRequired(), this.getLabel(),
				this.isLabelBold());
	}

}

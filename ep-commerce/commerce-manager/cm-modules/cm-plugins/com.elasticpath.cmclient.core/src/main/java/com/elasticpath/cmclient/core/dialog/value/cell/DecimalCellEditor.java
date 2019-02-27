/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.dialog.value.cell;

import java.math.BigDecimal;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.elasticpath.cmclient.core.dialog.value.dialog.AbstractValueDialog;
import com.elasticpath.cmclient.core.dialog.value.dialog.DecimalDialog;

/**
 * BigDecimal cell editor.
 */
public class DecimalCellEditor extends AbstractCellEditor<BigDecimal> {

	/**
	 * Construct the BigDecimal editor.
	 * @param parent the composite
	 * @param value the value
	 */
	public DecimalCellEditor(final Composite parent, final BigDecimal value) {
		super(parent, value);
	}
	
	/**
	 * Construct the BigDecimal editor.
	 * @param parent the composite
	 * @param value the value
	 * @param label the label
	 * @param isLabelBold the flag for style type
	 */
	public DecimalCellEditor(final Composite parent, final BigDecimal value, final String label, final boolean isLabelBold) {
		super(parent, value, label, isLabelBold);
	}
	
	/**
	 * Construct the BigDecimal editor.
	 * @param parent the composite
	 * @param value the value
	 * @param valueRequired true if value is required, false is value is not required
	 * @param label the label
	 * @param isLabelBold the flag for style type
	 */
	public DecimalCellEditor(final Composite parent, final BigDecimal value, 
			final boolean valueRequired, final String label, final boolean isLabelBold) {
		super(parent, value, valueRequired, label, isLabelBold);
	}
	
	@Override
	protected AbstractValueDialog<BigDecimal> getValueDialog(
			final Control cellEditorWindow) {
		return new DecimalDialog(
				cellEditorWindow.getShell(), this.getInitalValue(), true,  this.isValueRequired(), this.getLabel(),
				this.isLabelBold());
	}

}

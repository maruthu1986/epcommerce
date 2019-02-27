/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.dialog.value.cell;

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.elasticpath.cmclient.core.dialog.value.dialog.AbstractValueDialog;

/**
 * The generic cell editor for editing values.
 * @param <E> class for which the editor applies
 */
public abstract class AbstractCellEditor<E> extends DialogCellEditor {

	private final E value;
	
	private String label;
	
	private boolean labelBold;
	
	private boolean valueRequired;

	/**
	 * Construct the editor with no label.
	 * @param parent the composite
	 * @param value the value
	 */
	public AbstractCellEditor(final Composite parent, final E value) {
		super(parent);
		this.value = value;
	}
	
	/**
	 * Construct the editor with no label.
	 * @param parent the composite
	 * @param value the value
	 * @param valueRequired true if value is required, false is value is not required
	 */
	public AbstractCellEditor(final Composite parent, final E value, final boolean valueRequired) {
		super(parent);
		this.value = value;
		this.valueRequired = valueRequired;
	}
	
	/**
	 * Construct the editor with no label.
	 * @param parent the composite
	 * @param value the value
	 * @param label the label
	 */
	public AbstractCellEditor(final Composite parent, final E value, final String label) {
		super(parent);
		this.value = value;
		this.label = label;
	}
	
	/**
	 * Construct the editor with no label.
	 * @param parent the composite
	 * @param value the value
	 * @param valueRequired true if value is required, false is value is not required
	 * @param label the label
	 */
	public AbstractCellEditor(final Composite parent, final E value, final boolean valueRequired, final String label) {
		super(parent);
		this.value = value;
		this.label = label;
		this.valueRequired = valueRequired;
	}
	
	/**
	 * Construct the editor with no label.
	 * @param parent the composite
	 * @param value the value
	 * @param label the label
	 * @param isLabelBold the flag for style type
	 */
	public AbstractCellEditor(final Composite parent, final E value, final String label, final boolean isLabelBold) {
		super(parent);
		this.value = value;
		this.label = label;
		this.labelBold = isLabelBold;
	}
	
	/**
	 * Construct the editor with no label.
	 * @param parent the composite
	 * @param value the value
	 * @param valueRequired true if value is required, false is value is not required
	 * @param label the label
	 * @param isLabelBold the flag for style type
	 */
	public AbstractCellEditor(final Composite parent, final E value, final boolean valueRequired, final String label, final boolean isLabelBold) {
		super(parent);
		this.value = value;
		this.label = label;
		this.labelBold = isLabelBold;
		this.valueRequired = valueRequired;
	}
	
	/**
	 * factory method to construct the dialog.
	 * @param cellEditorWindow the parent object
	 * @return dialog for this cell editor
	 */
	protected abstract AbstractValueDialog<E> getValueDialog(final Control cellEditorWindow);
	
	/**
	 * opens the appropriate dialog for this type of value and return the edited value back.
	 * @param cellEditorWindow the parent window
	 * @return the new value returned from dialog
	 */
	protected E openDialogBox(final Control cellEditorWindow) {
		final AbstractValueDialog<E> dialog = getValueDialog(cellEditorWindow);
		final int result = dialog.open();
		if (result == Window.OK) {
			return dialog.getValue();
		}
		return this.value;
	}
	
	/**
	 * get the initial value for dialog.
	 * @return the initial value 
	 */
	protected E getInitalValue() {
		return value;
	}
	
	/**
	 * @return label for this parameter (may be null)
	 */
	protected String getLabel() {
		return label;
	}
	
	/**
	 * @return flag for whether the label should be bold or not
	 */
	protected boolean isLabelBold() {
		return labelBold;
	}

	/**
	 * Gets flag that indicates if value in dialog is required.
	 * 
	 * @return true if required, false if not required
	 */
	protected final boolean isValueRequired() {
		return valueRequired;
	}
}

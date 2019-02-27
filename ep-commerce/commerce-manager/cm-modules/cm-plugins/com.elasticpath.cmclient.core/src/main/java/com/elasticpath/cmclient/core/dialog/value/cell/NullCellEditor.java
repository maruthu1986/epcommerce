/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.dialog.value.cell;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.elasticpath.cmclient.core.dialog.value.dialog.AbstractValueDialog;
import com.elasticpath.cmclient.core.dialog.value.dialog.NullDialog;

/**
 * Null Dialog Cell editor is used to denote a dialog for unsupported type
 * in EditingSupportCellEditorFactory.
 */
public class NullCellEditor extends AbstractCellEditor<String> {

	/**
	 * Construct the editor with no label.
	 * @param parent the composite
	 * @param value the value is ignored
	 */
	public NullCellEditor(final Composite parent, final String value) {
		super(parent, value);
	}

	@Override
	protected AbstractValueDialog<String> getValueDialog(
			final Control cellEditorWindow) {
		return new NullDialog(cellEditorWindow.getShell());
	}

}

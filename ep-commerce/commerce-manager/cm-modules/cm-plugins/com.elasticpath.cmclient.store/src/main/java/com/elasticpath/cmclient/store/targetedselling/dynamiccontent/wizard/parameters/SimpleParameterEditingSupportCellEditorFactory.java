/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.store.targetedselling.dynamiccontent.wizard.parameters;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Table;

import com.elasticpath.cmclient.core.dialog.value.cell.NullCellEditor;
import com.elasticpath.cmclient.core.dialog.value.support.DialogValueLabelProvider;
import com.elasticpath.cmclient.core.dialog.value.support.SimpleEditingSupportCellEditorFactory;
import com.elasticpath.commons.constants.ValueTypeEnum;

/**
 * Simple Factory interface used by ParameterEditingSupport in order to
 * create appropriate cell editors for provided value type and
 * additionally provide support for Image and File asset manager dialogs.
 */
public class SimpleParameterEditingSupportCellEditorFactory extends
		SimpleEditingSupportCellEditorFactory {

	/**
	 * This hook method allows to react for Image and File dialogs.
	 *
	 * @param table         the table container
	 * @param type          the type of value
	 * @param value         the value of cell
	 * @param labelProvider the label provider from abstract editing support
	 * @return the Image or File cell editor for dynamic content Asset Manager Dialog or
	 * NullCellDialog if unsupported type
	 */
	@Override
	protected CellEditor getCellEditorForUnsupported(final Table table, final ValueTypeEnum type, final Object value,
													 final DialogValueLabelProvider labelProvider) {

		CellEditor editor;
		switch (type) {
			default:
				editor = new NullCellEditor(table, value.toString());
				break;
		}
		return editor;
	}

}

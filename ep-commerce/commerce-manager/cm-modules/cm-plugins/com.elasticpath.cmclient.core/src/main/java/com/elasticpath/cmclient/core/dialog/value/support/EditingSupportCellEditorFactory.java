/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.dialog.value.support;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Table;

import com.elasticpath.commons.constants.ValueTypeEnum;

/**
 * Simple Factory interface used by AbstractDialogEditingSupport in order to 
 * create appropriate cell editors for provided value type.
 */
public interface EditingSupportCellEditorFactory {

	/**
	 * selector for the correct cell editor depending on enum type provided.
	 * 
	 * @param table the table container
	 * @param type the type of value
	 * @param value the value of cell
	 * @param valueRequired true if value is required, false is value is not required
	 * @param labelProvider the label provider from abstract editing support
	 * @return cell editor (if an unsupported valueType is provided the factory
	 *         must return a NullCellEditor).
	 */
	CellEditor getCellEditor(Table table, ValueTypeEnum type, 
			Object value, boolean valueRequired, DialogValueLabelProvider labelProvider);
	
}

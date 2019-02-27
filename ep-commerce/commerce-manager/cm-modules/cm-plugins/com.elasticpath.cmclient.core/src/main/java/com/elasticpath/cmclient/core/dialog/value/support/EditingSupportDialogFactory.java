/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.dialog.value.support;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.commons.constants.ValueTypeEnum;

/**
 * Simple Factory interface used by AbstractDialogEditingSupport in order to 
 * create appropriate dialog window for provided value type.
 */
public interface EditingSupportDialogFactory {
	
	/**
	 * Create the dialogs for different type attribute values.
	 * @param shell the parent shell
	 * @param valueType the type of value
	 * @param value the value to edit (should be castable to correct type)
	 * @param editMode the mode of dialog
	 * @param labelProvider the label provider from editing support
	 * @param valueRequired true if value is required, false is value is not required
	 * 
	 * @return the editor dialog (if an unsupported valueType is provided the factory
	 *         must return a NullDialog).
	 */
	Window getEditorDialog(Shell shell, ValueTypeEnum valueType, 
			Object value, boolean editMode, DialogValueLabelProvider labelProvider, boolean valueRequired);
	
}
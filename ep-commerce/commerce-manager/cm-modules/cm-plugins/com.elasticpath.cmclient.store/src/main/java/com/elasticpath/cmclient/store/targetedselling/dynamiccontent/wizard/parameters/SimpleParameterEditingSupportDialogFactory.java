/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.store.targetedselling.dynamiccontent.wizard.parameters;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.dialog.value.dialog.NullDialog;
import com.elasticpath.cmclient.core.dialog.value.support.DialogValueLabelProvider;
import com.elasticpath.cmclient.core.dialog.value.support.SimpleEditingSupportDialogFactory;
import com.elasticpath.commons.constants.ValueTypeEnum;

/**
 * Simple Factory interface used by ParameterEditingSupport in order to 
 * create appropriate dialog window for provided value type and 
 * additionally provide support for Image and File asset manager dialogs.
 */
public class SimpleParameterEditingSupportDialogFactory extends
		SimpleEditingSupportDialogFactory {

	/**
	 * This hook method allows to react for Image and File dialogs.
	 * @param valueType the type of value
	 * @param value the value to edit (should be castable to correct type)
	 * @param shell the parent shell
	 * @param editMode the mode of dialog
	 * @param labelProvider the label provider from editing support
	 * @return the Image or File dialog for dynamic content Asset Manager Dialog or
	 *         NullDialog dialog if unsupported type
	 */
	@Override
	protected Window getEditorDialogForUnsupported(final ValueTypeEnum valueType,
												   final Object value, final Shell shell, final boolean editMode,
												   final DialogValueLabelProvider labelProvider) {
		Window dialog;
		switch (valueType) {
		default:
			dialog = new NullDialog(shell);
			break;
		}
		return dialog;
	}
	
}

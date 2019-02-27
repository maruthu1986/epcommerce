/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.dialog.value.support;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.dialog.value.dialog.BooleanDialog;
import com.elasticpath.cmclient.core.dialog.value.dialog.DateTimeDialog;
import com.elasticpath.cmclient.core.dialog.value.dialog.DecimalDialog;
import com.elasticpath.cmclient.core.dialog.value.dialog.IntegerDialog;
import com.elasticpath.cmclient.core.dialog.value.dialog.LongTextDialog;
import com.elasticpath.cmclient.core.dialog.value.dialog.NullDialog;
import com.elasticpath.cmclient.core.dialog.value.dialog.ShortTextDialog;
import com.elasticpath.cmclient.core.dialog.value.dialog.ShortTextMultiValueDialog;
import com.elasticpath.cmclient.core.dialog.value.dialog.SimpleHttpURLDialog;
import com.elasticpath.cmclient.core.ui.dialog.CategoryFinderDialog;
import com.elasticpath.cmclient.core.ui.dialog.ProductFinderDialog;
import com.elasticpath.cmclient.core.ui.framework.IEpDateTimePicker;
import com.elasticpath.commons.constants.ValueTypeEnum;

/**
 * Simple Factory interface used by AbstractDialogEditingSupport in order to 
 * create appropriate dialog window for provided value type.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class SimpleEditingSupportDialogFactory implements EditingSupportDialogFactory {
	
	/**
	 * Create the dialogs for different type attribute values.
	 * Does not support Image and File due to limitations of use of AssetManager in core.
	 * @param shell the parent shell
	 * @param valueType the type of value
	 * @param value the value to edit (should be castable to correct type)
	 * @param editMode the mode of dialog
	 * @param labelProvider the label provider from editing support
	 * @param valueRequired true if value is required, false is value is not required
	 * 
	 * @return the editor dialog
	 */
	public Window getEditorDialog(final Shell shell, 
			final ValueTypeEnum valueType, final Object value, final boolean editMode, 
			final DialogValueLabelProvider labelProvider, final boolean valueRequired) {
		
		String label = null;
		boolean isBoldLabel = false;
		if (labelProvider != null) {
			label = labelProvider.getLabelText();
			isBoldLabel = labelProvider.isLabelBold();
		}
		
		Window dialog = null;
		switch (valueType) {
		case Boolean:
			dialog = new BooleanDialog(shell, (Boolean) value, editMode, valueRequired, label, isBoldLabel);
			break;
		case Date:
			dialog = new DateTimeDialog(shell, (Date) value, IEpDateTimePicker.STYLE_DATE, editMode, valueRequired, label, isBoldLabel);
			break;
		case Datetime:
			dialog = new DateTimeDialog(shell, (Date) value, 
					IEpDateTimePicker.STYLE_DATE | IEpDateTimePicker.STYLE_DATE_AND_TIME, valueRequired, editMode, label, isBoldLabel);
			break;
		case Decimal:
			dialog = new DecimalDialog(shell, (BigDecimal) value, editMode, valueRequired, label, isBoldLabel);
			break;
		case Integer:
			dialog = new IntegerDialog(shell, (Integer) value, editMode, valueRequired, label, isBoldLabel);
			break;
		case StringLong:
			dialog = new LongTextDialog(shell, (String) value, editMode, valueRequired, label, isBoldLabel);
			break;
		case StringShort:
			dialog = new ShortTextDialog(shell, (String) value, editMode, valueRequired, label, isBoldLabel);
			break;
		case StringShortMultiValue:
			dialog = new ShortTextMultiValueDialog(shell, (List<String>) value, editMode, valueRequired, label, isBoldLabel);
			break;
		case Product:
			dialog = new ProductFinderDialog(shell, false, false);
			break;
		case Category:
			dialog = new CategoryFinderDialog(shell, false);
			break;			
		case Url:
			dialog = new SimpleHttpURLDialog(shell, (String) value, editMode, valueRequired, label, isBoldLabel);
			break;
		case HTML:
			dialog = new LongTextDialog(shell, (String) value, editMode, valueRequired, label, isBoldLabel);
			break;			
		default:
			dialog = getEditorDialogForUnsupported(valueType, value, shell, editMode, labelProvider); 
			break;
		}
		return dialog;
	}
	
	/**
	 * Hook method that allows to react if the main case is not satisfied.
	 * @param valueType the type of value
	 * @param value the value to edit (should be castable to correct type)
	 * @param shell the parent shell
	 * @param editMode the mode of dialog
	 * @param labelProvider the label provider from editing support
	 * @return the NullDialog editor dialog
	 */
	protected Window getEditorDialogForUnsupported(final ValueTypeEnum valueType, 
			final Object value, final Shell shell, final boolean editMode, 
			final DialogValueLabelProvider labelProvider) {
		return new NullDialog(shell);
	}
	
}
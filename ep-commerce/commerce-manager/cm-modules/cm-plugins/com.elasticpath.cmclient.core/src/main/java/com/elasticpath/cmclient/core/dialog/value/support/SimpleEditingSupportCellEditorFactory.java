/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.dialog.value.support;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Table;

import com.elasticpath.cmclient.core.dialog.value.cell.BooleanCellEditor;
import com.elasticpath.cmclient.core.dialog.value.cell.CategoryCellEditor;
import com.elasticpath.cmclient.core.dialog.value.cell.DateCellEditor;
import com.elasticpath.cmclient.core.dialog.value.cell.DateTimeCellEditor;
import com.elasticpath.cmclient.core.dialog.value.cell.DecimalCellEditor;
import com.elasticpath.cmclient.core.dialog.value.cell.IntegerCellEditor;
import com.elasticpath.cmclient.core.dialog.value.cell.LongTextCellEditor;
import com.elasticpath.cmclient.core.dialog.value.cell.NullCellEditor;
import com.elasticpath.cmclient.core.dialog.value.cell.ProductCellEditor;
import com.elasticpath.cmclient.core.dialog.value.cell.ShortTextMultiValueCellEditor;
import com.elasticpath.cmclient.core.dialog.value.cell.SimpleHttpURLCellEditor;
import com.elasticpath.commons.constants.ValueTypeEnum;

/**
 * Simple Factory interface used by AbstractDialogEditingSupport in order to 
 * create appropriate cell editors for provided value type.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class SimpleEditingSupportCellEditorFactory implements
		EditingSupportCellEditorFactory {

	/**
	 * selector for the correct cell editor depending on enum type provided.
	 * Does not support Image and File due to limitations of use of AssetManager in core.
	 * 
	 * @param table the table container
	 * @param type the type of value
	 * @param value the value of cell
	 * @param valueRequired true if value is required, false is value is not required
	 * @param labelProvider the label provider from abstract editing support
	 * @return cell editor
	 */
	public CellEditor getCellEditor(final Table table, final ValueTypeEnum type, final Object value,  final boolean valueRequired,
			final DialogValueLabelProvider labelProvider) {
		
		String label = null;
		boolean isBoldLabel = false;
		if (labelProvider != null) {
			label = labelProvider.getLabelText();
			isBoldLabel = labelProvider.isLabelBold();
		}
		
		CellEditor editor = null;
		switch (type) {
		case StringShort:
			editor = new TextCellEditor(table);			
			// NOTE: editor = new ShortTextCellEditor(table, (String) value);
			break;
		case StringShortMultiValue:
			editor = new ShortTextMultiValueCellEditor(table, (List<String>) value, valueRequired, label, isBoldLabel);
			break;

		case StringLong:
			editor = new LongTextCellEditor(table, (String) value, valueRequired, label, isBoldLabel);
			break;

		case Date:
			editor = new DateCellEditor(table, (Date) value, label, isBoldLabel);
			break;

		case Datetime:
			editor = new DateTimeCellEditor(table, (Date) value, label, isBoldLabel);
			break;

		case Integer:
			editor = new IntegerCellEditor(table, (Integer) value, valueRequired, label, isBoldLabel);
			break;

		case Decimal:
			editor = new DecimalCellEditor(table, (BigDecimal) value, valueRequired, label, isBoldLabel);
			break;
			
		case Boolean:
			editor = new BooleanCellEditor(table, (Boolean) value, valueRequired, label, isBoldLabel);
			break;

		case Url:
			editor = new SimpleHttpURLCellEditor(table, (String) value, valueRequired, label, isBoldLabel);
			break;
			
		case Product:
			editor = new ProductCellEditor(table, (String) value, label, isBoldLabel);
			break;
			
		case Category:
			editor = new CategoryCellEditor(table, (String) value, label, isBoldLabel);
			break;
			
		case HTML:
			editor = new LongTextCellEditor(table, (String) value, valueRequired, label, isBoldLabel);
			break;
			
		default:
			editor = getCellEditorForUnsupported(table, type, value, labelProvider);
			break;
		}
		return editor;
	}
	
	/**
	 * Hook method that allows to react if the main case is not satisfied.
	 * @param table the table container
	 * @param type the type of value
	 * @param value the value of cell
	 * @param labelProvider the label provider from abstract editing support
	 * @return the NullCellEditor editor
	 */
	protected CellEditor getCellEditorForUnsupported(final Table table, final ValueTypeEnum type, final Object value, 
			final DialogValueLabelProvider labelProvider) {
		return new NullCellEditor(table, value.toString());
	}

}

/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.ui.framework;

import org.eclipse.jface.viewers.ColumnLabelProvider;

/**
 * Abstract class for providing check style behavior for columns. Should be implemented if the type of the column has been set to RADIO or CHECKBOX.
 * For more info on the available type constants see IEpTableColum. For adding a type column see:
 * 
 * @see IEpTableViewer#addTableColumn(String, int, int)
 * @see IEpTableColumn#TYPE_CHECKBOX
 * @see IEpTableColumn#TYPE_RADIO
 * @see IEpTableColumn#TYPE_NONE
 */
public abstract class AbstractEpCheckLabelProvider extends ColumnLabelProvider {

	/**
	 * Returns true if the element has to be with checked state.<br>
	 * Used only if the column type is RADIO or CHECKBOX
	 * 
	 * @param element the model element
	 * @return true if checked state
	 */
	public abstract boolean isChecked(Object element);
}

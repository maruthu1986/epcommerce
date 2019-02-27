/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.viewers;

import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;

/**
 * The viewer provides a  AssetTableViewer#editElementValue method allow program to trigger <br>
 * the cell editor rather than trigger from mouse/key event.
 *
 */
public class AssetTableViewer extends TableViewer {

	/**
	 * 
	 * Constructor.
	 * @param parent the parent composite
	 */
	public AssetTableViewer(final Composite parent) {
		super(parent);	
	}
	
	/**
	 * 
	 * Constructor.
	 * @param parent the parent composite
	 * @param style the SWT style
	 */
	public AssetTableViewer(final Composite parent, final int style) {
		super(parent, style);
	}
	
	/**
	 * Trigger the cell editor to allow user edit cell value.
	 * @param element the element in the row
	 * @param column the column of the cell to edit
	 */
	public void editElementValue(final Object element, final int column) {
		if (getColumnViewerEditor() != null) {
			Widget item = findItem(element);
			if (item != null) {
				ViewerRow row = getViewerRowFromItem(item);
				if (row != null) { //NOPMD
					ViewerCell cell = row.getCell(0);
					if (cell != null) {
						getControl().setRedraw(false);
						setSelection(new StructuredSelection(cell.getElement()));
						triggerEditorActivationEvent(new ColumnViewerEditorActivationEvent(
								cell));
						getControl().setRedraw(true);
					}
				}
			}
		}
	}

}

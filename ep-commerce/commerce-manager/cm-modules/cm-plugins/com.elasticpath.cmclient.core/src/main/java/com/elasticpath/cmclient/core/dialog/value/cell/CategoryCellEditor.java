/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.dialog.value.cell;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.elasticpath.cmclient.core.dialog.value.dialog.AbstractValueDialog;
import com.elasticpath.cmclient.core.ui.dialog.CategoryFinderDialog;
import com.elasticpath.domain.catalog.Category;

/**
 * Category cell editor.
 * Allows to pick a category and returns its code as value.
 */
public class CategoryCellEditor extends AbstractCellEditor<String> {

	/**
	 * Construct the Category editor.
	 * @param parent the composite
	 * @param value the value
	 */
	public CategoryCellEditor(final Composite parent, final String value) {
		super(parent, value);
	}
	
	/**
	 * Construct the Category editor.
	 * @param parent the composite
	 * @param value the value
	 * @param label the label
	 * @param isLabelBold the flag for style type
	 */
	public CategoryCellEditor(final Composite parent, final String value, final String label, final boolean isLabelBold) {
		super(parent, value, label, isLabelBold);
	}
	
	@Override
	protected String openDialogBox(final Control cellEditorWindow) {
		final CategoryFinderDialog dialog = new CategoryFinderDialog(cellEditorWindow.getShell(), false);
		String returnValue = this.getInitalValue();  
		int result = dialog.open();
		
		if (result == org.eclipse.jface.window.Window.OK) {
			Category category = (Category) dialog.getSelectedObject();
			if (null != category.getCode()) {
				returnValue = category.getCode();
			}
		}
		return returnValue;
		
	}
	
	@Override
	protected AbstractValueDialog<String> getValueDialog(
			final Control cellEditorWindow) {
		throw new UnsupportedOperationException("Category does not follow normal flow of execution"); //$NON-NLS-1$
	}

}

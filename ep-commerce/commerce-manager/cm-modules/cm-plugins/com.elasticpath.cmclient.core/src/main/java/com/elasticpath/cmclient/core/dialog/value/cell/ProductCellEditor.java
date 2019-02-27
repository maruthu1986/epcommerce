/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.dialog.value.cell;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.elasticpath.cmclient.core.dialog.value.dialog.AbstractValueDialog;
import com.elasticpath.cmclient.core.ui.dialog.ProductFinderDialog;
import com.elasticpath.domain.catalog.Product;

/**
 * Product cell editor.
 * Allows to pick a product and returns its code as value.
 */
public class ProductCellEditor extends AbstractCellEditor<String> {

	/**
	 * Construct the Product editor.
	 * @param parent the composite
	 * @param value the value
	 */
	public ProductCellEditor(final Composite parent, final String value) {
		super(parent, value);
	}
	
	/**
	 * Construct the Product editor.
	 * @param parent the composite
	 * @param value the value
	 * @param label the label
	 * @param isLabelBold the flag for style type
	 */
	public ProductCellEditor(final Composite parent, final String value, final String label, final boolean isLabelBold) {
		super(parent, value, label, isLabelBold);
	}
	
	@Override
	protected String openDialogBox(final Control cellEditorWindow) {
		final ProductFinderDialog dialog = new ProductFinderDialog(cellEditorWindow.getShell(), false, false);
		String returnValue = this.getInitalValue();  
		int result = dialog.open();
		if (result == org.eclipse.jface.window.Window.OK) {
			Product product = (Product) dialog.getSelectedObject();
			if (product != null) {
				returnValue = product.getCode();
			}
		}
		return returnValue;
	
	}
	
	@Override
	protected AbstractValueDialog<String> getValueDialog(
			final Control cellEditorWindow) {
		throw new UnsupportedOperationException("Product does not follow normal flow of execution"); //$NON-NLS-1$
	}

}

/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.editors.attribute;

/**
 * Creates the dialog to modify the data in the table cells.
 * <p>
 * This interface is used to create identical Editor Dialogs when clicked on either
 * editable cell or by selecting the row and pressing the Edit Button.
 */
public interface ICellEditorDialogService {

	/**
	 * Creates Dialog which will update values in the table cells.
	 */
	void createEditorDialog();
}

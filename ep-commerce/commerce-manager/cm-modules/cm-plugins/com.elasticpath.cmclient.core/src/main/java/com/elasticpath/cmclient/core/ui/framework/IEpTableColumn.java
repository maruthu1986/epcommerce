/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.ui.framework;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableColumn;

/**
 * Represents a column in a table.
 */
public interface IEpTableColumn {

	// /**
	// * Left header alignment.
	// */
	// int LEFT = SWT.LEFT;

	// /**
	// * Right header alignment.
	// */
	// int RIGHT = SWT.RIGHT;

	// /**
	// * Center header alignment.
	// */
	// int CENTER = SWT.CENTER;

	/**
	 * Column type for radio style column.
	 */
	int TYPE_RADIO = SWT.RADIO;

	/**
	 * Column type for checkbox style column.
	 */
	int TYPE_CHECKBOX = SWT.CHECK;

	/**
	 * Column type for normal text column.
	 */
	int TYPE_NONE = 0;

	/**
	 * Returns the original TableColumn object.
	 * 
	 * @return <code>TableColumn</code>
	 * @see TableColumn
	 */
	TableColumn getSwtTableColumn();

	/**
	 * Sets the cell editor for this column.
	 * 
	 * @param editingSupport the Eclipse cell editor
	 * @see EditingSupport
	 */
	void setEditingSupport(EditingSupport editingSupport);

	/**
	 * Gets the editing support of this column.
	 *
	 * @return EditingSupport or null if not set
	 */
	EditingSupport getEditingSupport();

	/**
	 * Sets a column label provider.
	 * 
	 * @param labelProvider the label provider
	 */
	void setLabelProvider(ColumnLabelProvider labelProvider);
}

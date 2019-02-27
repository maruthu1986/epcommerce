/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.ui.framework;

import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Table;

/**
 * Abstraction interface for representing list viewer like behavior.<br>
 * The implementation utilizes an Eclipse JFace TableViewer.
 */
public interface IEpListViewer {

	/**
	 * Gets the table viewer.
	 * 
	 * @return Eclipse <code>TableViewer</code>
	 * @see TableViewer
	 */
	TableViewer getSwtTableViewer();

	/**
	 * Gets the table.
	 * 
	 * @return Eclipse Table
	 * @see Table
	 */
	Table getSwtTable();

	/**
	 * Sets a label provider for the table viewer. <br>
	 * <i>Note: Adds a striped style effect to the table viewer.</i>
	 * 
	 * @param labelProvider table label provider. Usually implementors of this interface extend {@link org.eclipse.jface.viewers.LabelProvider}.
	 */
	void setLabelProvider(ILabelProvider labelProvider);

	/**
	 * Sets the content provider.
	 * 
	 * @param contentProvider the content provider
	 */
	void setContentProvider(IStructuredContentProvider contentProvider);

	/**
	 * Sets the input to the table viewer.
	 * 
	 * @param input input object
	 */
	void setInput(Object input);

	/**
	 * Sets the editing support for this list.<br>
	 * Used only if the list has to be editable.
	 * 
	 * @param support EditingSupport
	 * @see EditingSupport
	 */
	void setEditingSupport(EditingSupport support);

	/**
	 * Sets the layout data of the list viewer.
	 * 
	 * @param layoutData the layout data object
	 */
	void setLayoutData(Object layoutData);
}

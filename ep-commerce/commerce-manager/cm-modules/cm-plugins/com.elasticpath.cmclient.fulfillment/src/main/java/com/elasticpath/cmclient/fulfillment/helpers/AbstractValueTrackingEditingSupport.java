/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.helpers;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorListener;

/**
 * <code>AbstractValueTrackingEditingSupport</code> abstract implementation of EditingSupport.
 * Used for refreshing data on value change.  
 */
public abstract class AbstractValueTrackingEditingSupport extends EditingSupport {

	private Object element;
	
	private final CellEditor cellEditor;

	private Object getCellValue() {
		return cellEditor.getValue();
	}

	/**
	 * Creates the AbstractValueTrackingEditingSupport.
	 *
	 * @param viewer - the ColumnViver
	 * @param cellEditor - the cellEditor
	 */
	public AbstractValueTrackingEditingSupport(final ColumnViewer viewer, final CellEditor cellEditor) {
		super(viewer);
		this.cellEditor = cellEditor;

		this.cellEditor.addListener(new ICellEditorListener() {

			@Override
			public void applyEditorValue() {
				if (element != null) {
					valueChange(element, getCellValue());
				}
				finishEditor();
			}

			@Override
			public void cancelEditor() {
				finishEditor();
			}

			@Override
			public void editorValueChanged(final boolean oldValidState, final boolean newValidState) {
				if (newValidState && element != null) {
					valueChange(element, getCellValue());
				}
			}
		});

	}
	
	/**
	 * Called when editor is finishing.
	 */
	protected void finishEditor() {
		// empty
	}

	@Override
	protected boolean canEdit(final Object element) {
		return true;
	}

	@Override
	protected CellEditor getCellEditor(final Object element) {
		return cellEditor;
	}

	@Override
	protected Object getValue(final Object element) {
		this.element = element;
		return doGetValue(element);
	}

	@Override
	protected void setValue(final Object element, final Object value) {		
		getViewer().update(element, null);
	}
	
	/**
	 * Restore the value from the CellEditor on value change at the Editor. 
	 * 
	 * @param element the model element
	 * @param value the new value
	 */
	protected void valueChange(final Object element, final Object value) {		
		doChangeValue(element, value);
	}
	
	/**
	 * Abstract work on Getting value.
	 *
	 * @param element the model element
	 * @return the value shown
	 */
	protected abstract Object doGetValue(Object element);

	/**
	 * Abstract work on Changing value.
	 *
	 * @param element the model element
	 * @param value the new value
	 */
	protected abstract void doChangeValue(Object element, Object value);	
}

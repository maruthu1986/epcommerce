/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.dialog.value.cell;

/**
 * Wrapper for objects that are used in cell editors. 
 * Allows to overcome ugly output while editing something.
 */
public abstract class AbstractCellEditorValueWrapper {
	
	private final Object wrappedValue;
	
	/**
	 * default constructor.
	 * @param value the value to wrap.
	 */
	public AbstractCellEditorValueWrapper(final Object value) {
		this.wrappedValue = value;
	}
	
	/**
	 * @param <T> type of wrapped value
	 * @return the wrapped value
	 */
	public final <T> T getWrappedValue() {
		return (T) wrappedValue;
	}
	
	/**
	 * @return string value that will be displayed in the cell while editing it.
	 */
	public abstract String getDisplayValue();
	
	@Override
	public final String toString() {
		return getDisplayValue();
	}

}

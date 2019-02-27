/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.dialog.value.support;


/**
 * Used for unifying the retrieval of values from the edit dialogs.
 * @param <E> type of value
 */
public interface IValueChangedListener<E> {

	/**
	 * Perform any action upon value change.
	 * 
	 * @param value the value been changed
	 */
	void valueChanged(E value);
}

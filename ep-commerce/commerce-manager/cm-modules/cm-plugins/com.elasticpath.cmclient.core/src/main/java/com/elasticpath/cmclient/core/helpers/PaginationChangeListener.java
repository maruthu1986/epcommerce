/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.helpers;

/**
 * Listener for a pagination setting change.
 */
public interface PaginationChangeListener {
	
	/**
	 * Handles a fired pagination change with the new value.
	 *
	 * @param newValue the new pagination setting
	 */
	void paginationChange(int newValue);
}

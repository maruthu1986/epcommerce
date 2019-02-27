/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.conditionbuilder.wizard.duallistbox;

/**
 * Handles element removal event.
 * @param <T> type of the object that's being checked for removal
 */
public interface RemoveObjectHandler<T> {

	/**
	 * Checks if it's allowed to remove the object from the assigned list.
	 * 
	 * @param object Object to remove
	 * @return true if it is allowed to remove the object from the assigned list.
	 */
	boolean isRemovalAllowed(T object);
	
}

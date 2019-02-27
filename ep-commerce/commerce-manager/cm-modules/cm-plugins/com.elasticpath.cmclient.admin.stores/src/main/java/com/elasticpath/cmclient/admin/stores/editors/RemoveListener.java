/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.stores.editors;

import java.util.List;

/**
 * Implementers can control removing of elements from the assigned list.
 * 
 * @param <T> the type of object to remove
 */
public interface RemoveListener<T> {

	/**
	 * This method calls when system tries to delete the list of specific objects.
	 *
	 * @param list list of objects to be removed.
	 * @return true if the specified object can be removed and false otherwise
	 */		
	boolean tryToRemove(List<T> list);

}
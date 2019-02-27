/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.catalog.editors;

/**
 * Synchronizes the current view with the persisted entities. 
 */
public interface ViewSynchronizer {
	/**
	 * Saving or reload the view. 
	 * @return true if save or reload was successful, false otherwise.
	 */
	boolean saveOrReload();
}

/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.catalog.perspectives;

/**
 * The catalog perspective view extension interface.
 */
public interface ICatalogPerspectiveViewExtension {
	
	/**
	 * Get view id.
	 * 
	 * @return the view id
	 */
	String getViewId();
	
	/**
	 * determine if the user is authorized.
	 * 
	 * @return true if the user is authorized
	 */
	boolean isAuthorized();
	
	/**
	 * is the view movable.
	 * 
	 * @return true if the view is movable
	 */
	boolean isMovable();
	
	/**
	 * is the view closeable. 
	 * 
	 * @return true if the view is closeable
	 */
	boolean isCloseable();
	
	/**
	 * Get the place holder. 
	 * 
	 * @return the place holder
	 */
	String getPlaceholder();
}

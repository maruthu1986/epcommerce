/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.core.registry;

/**
 * Provides a means of callback mechanism on dealing with the registry objects. 
 */
public interface ObjectRegistryListener {

	/**
	 * A callback method invoked when new object is added to the registry.
	 * 
	 * @param key the object's key in the registry
	 * @param object the added object 
	 */
	void objectAdded(String key, Object object);
	
	/**
	 * A callback method invoked when an object is updated in the registry.
	 * 
	 * @param key the object's key in the registry
	 * @param oldValue the old object 
	 * @param newValue the new object 
	 */
	void objectUpdated(String key, Object oldValue, Object newValue);
	
	/**
	 * A callback method invoked when an object is removed from the registry.
	 * 
	 * @param key the object's key in the registry
	 * @param object the removed object 
	 */
	void objectRemoved(String key, Object object);
}

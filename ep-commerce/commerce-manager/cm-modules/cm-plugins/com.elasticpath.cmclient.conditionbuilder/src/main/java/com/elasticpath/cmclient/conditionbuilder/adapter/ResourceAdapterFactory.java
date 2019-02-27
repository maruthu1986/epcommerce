/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.cmclient.conditionbuilder.adapter;


/**
 * ResourceAdapterFactoryImpl factory for access to ResourceAdapters.
 *
 */
public interface ResourceAdapterFactory {

	/**
	 * Get ResourceAdapter for the class T type.
	 * @param <T> type
	 * @param type class type
	 * @return ResourceAdapter
	 */
	<T> ResourceAdapter<T> getResourceAdapter(Class<T> type);
	
}

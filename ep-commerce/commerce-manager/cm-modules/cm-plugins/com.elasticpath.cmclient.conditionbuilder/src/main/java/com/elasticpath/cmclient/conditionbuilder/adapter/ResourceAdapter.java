/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.cmclient.conditionbuilder.adapter;

/**
 * ResourceAdapter.
 * @param <T> object for adapter
 */
public interface ResourceAdapter<T> {
	
	/**
	 * Get localized string.
	 * @param object source object.
	 * @return string
	 */
	String getLocalizedResource(T object);
}

/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.cmclient.conditionbuilder.editor;

/**
 * A T type model wrapper.
 * @param <T> a model type
 */
public interface ModelWrapper<T> {

	/**
	 * @return a model
	 */
	T getModel();
}

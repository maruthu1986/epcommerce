/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.helpers.extenders;

/**
 * Implemented by extension plugins to provide creation capabilities of extended domain classes.
 * 
 * @param <T> the type of domain entity to create
 */
public interface EpModelCreator<T> {
	/**
	 * Creates a new instance of the model.
	 *
	 * @return the new instance
	 */
	T createModel();
	
	/**
	 * Creates a new instance of the model from another instance.
	 *
	 * @param other the other model object to copy data from
	 * @return the new instance
	 */
	T createModel(T other);
}

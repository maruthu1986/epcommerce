/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.store.targetedselling.handlers;


/**
 * Basic service to provide functionality for creating an entity.
 * 
 * @param <T> the type of domain object
 * 
 * @author dpavlov
 *
 */
public interface CreateHandlerService<T> {

	/**
	 * checks for duplicates that may exist inside the database.
	 * @param domainObject the current domain object being created
	 * @return true if newly created object is a duplicate of an existing one
	 */
	boolean exists(T domainObject);
	
	/**
	 * persist the object.
	 * @param domainObject the current object being created
	 */
	void persist(T domainObject);
	
}

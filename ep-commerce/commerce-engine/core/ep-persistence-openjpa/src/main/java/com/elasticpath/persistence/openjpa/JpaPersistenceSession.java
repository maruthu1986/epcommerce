/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
/**
 * 
 */
package com.elasticpath.persistence.openjpa;

import javax.persistence.EntityManager;

import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.persistence.api.PersistenceSession;

/**
 * Persistence session with an entity manager.
 */
public interface JpaPersistenceSession extends PersistenceSession {

	/**
	 * Get the Entity Manager.
	 *
	 * @return the EntityManager
	 */
	EntityManager getEntityManager();

	/**
	 * Creates and returns a query based on the given named query.
	 *
	 * @param <T> the expected type of elements returned by the query
	 * @param queryName the named query
	 * @return a query
	 * @throws com.elasticpath.persistence.api.EpPersistenceException in case of any error
	 */
	@Override
	<T> JpaQuery<T> createNamedQuery(String queryName) throws EpPersistenceException;
}

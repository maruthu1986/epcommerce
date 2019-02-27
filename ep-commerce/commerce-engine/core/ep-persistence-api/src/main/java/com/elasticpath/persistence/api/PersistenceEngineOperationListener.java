/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.persistence.api;

/**
 * Interface which receives events from the {@code JpaPersistenceEngineImpl}. <br/>
 * The contract for this listener has two scenarios: one for changes to a single object and one for
 * changes provided in a bulk update query string.
 *
 * <h1>Single Changes</h1>
 * <ol>
 * <li>{@code beginSingleOperation} is called.</li>
 *
 * <li>During the execution of the change then normal OpenJPA Lifecycle Events will be sent to LifecycleListeners.
 *
 * <li>Once the change is complete then: {@code endSingleOperation} is called</li>
 * </ol>
 */
public interface PersistenceEngineOperationListener {

	/**
	 * Will be called when the persistence engine is starting an operation (merge, save, delete) on a single entity.
	 *
	 * @param object The object that will be persisted.
	 * @param type The type of change.
	 */
	void beginSingleOperation(Persistable object, ChangeType type);

	/**
	 * Will be called when the persistence engine is starting a bulk operation (merge, save, delete).
	 *
	 * @param queryName the query name or null if not a named query.
	 * @param queryString the string that is being executed.
	 * @param parameters The parameters provided or null if none.
	 * @param type The type of change.
	 */
	void beginBulkOperation(String queryName, String queryString, String parameters, ChangeType type);

	/**
	 * Will be called when the persistence engine is ending an operation (merge, save, delete) on a single entity.
	 *
	 * @param object The object that will be persisted.
	 * @param type The type of change.
	 */
	void endSingleOperation(Persistable object, ChangeType type);

	/**
	 * Will be called when the persistence engine is ending a bulk operation (merge, save, delete).
	 */
	void endBulkOperation();
}

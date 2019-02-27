/*
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.tools.sync.target;

import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.tools.sync.job.JobEntry;

/**
 * Callback interface for hooking into Data Sync Tool JobEntry processing.
 */
public interface JobTransactionCallback {

	/**
	 * Method called before each update (add or change) job entry in a transaction. Calls
	 * are synchronous and in the same transaction as the main sync process.
	 *
	 * @param jobEntry the jobEntry being updated
	 * @param targetPersistence the persistable object on the target, for retrieving info not included in jobEntry
	 */
	default void preUpdateJobEntryHook(JobEntry jobEntry, Persistable targetPersistence) {
		//No-op implementation
	}

	/**
	 * Method called after each update (add or change) job entry in a transaction. Calls
	 * are synchronous and in the same transaction as the main sync process.
	 *
	 * @param jobEntry the jobEntry being updated
	 * @param targetPersistence the persistable object on the target, for retrieving info not included in jobEntry
	 */
	default void postUpdateJobEntryHook(JobEntry jobEntry, Persistable targetPersistence) {
		//No-op implementation
	}

	/**
	 * Method called before each remove of a job entry in a transaction. Calls
	 * are synchronous and in the same transaction as the main sync process.
	 *
	 * @param jobEntry the entry being removed
	 * @param targetPersistence the persistable object on the target, for retrieving info not included in jobEntry
	 */
	default void preRemoveJobEntryHook(JobEntry jobEntry, Persistable targetPersistence) {
		//No-op implementation
	}

	/**
	 * Method called after each remove of a job entry in a transaction. Calls
	 * are synchronous and in the same transaction as the main sync process.
	 *
	 * @param jobEntry the entry being removed
	 * @param targetPersistence the persistable object on the target, for retrieving info not included in jobEntry
	 */
	default void postRemoveJobEntryHook(JobEntry jobEntry, Persistable targetPersistence) {
		//No-op implementation
	}

	/**
	 * Returns a callback id string which identifies what particular implementation is being used.
	 *
	 * @return callback id string which identifies implementation
	 */
	String getCallbackID();
}

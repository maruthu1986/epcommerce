/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.persistence.api;

/**
 * This service exposes methods designed to help construct OpenSessionInView style filters.
 */
public interface PersistenceSessionFilterSupport {
	/**
	 * Opens a session for the current thread.  Callers of this method <b>MUST</b> call
	 * dispose of the session after processing is complete using closedSharedSession().
	 * It's strongly suggested that callers use a try/finally block to accomplish this.
	 */
	void openSharedSession();

	/**
	 * Closes a session opened by openSharedSession.
	 */
	void closeSharedSession();
}

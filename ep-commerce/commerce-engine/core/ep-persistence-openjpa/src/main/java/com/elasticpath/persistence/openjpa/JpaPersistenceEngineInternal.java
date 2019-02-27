/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.persistence.openjpa;

import com.elasticpath.persistence.api.ChangeType;
import com.elasticpath.persistence.api.Persistable;

/**
 * Package internal interface to {@code JpaPersistenceEngineImpl} to allow testing.
 */
public interface JpaPersistenceEngineInternal extends JpaPersistenceEngine {

	/**
	 * Begins a single operation.
	 *
	 * @param object the object being change.
	 * @param changeType The change type.
	 */
	void fireBeginSingleOperationEvent(Persistable object, ChangeType changeType);

	/**
	 * Ends a single operation.
	 * @param object the object being chagned.
	 * @param changeType The change type.
	 */
	void fireEndSingleOperationEvent(Persistable object, ChangeType changeType);

}

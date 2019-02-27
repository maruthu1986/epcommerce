/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.processing;


/**
 * A listener for new objects to be processed.
 */
public interface SerializableObjectListener {

	/**
	 * A hook to indicate a new job is being started, should not be called if a job is currently inflight and {@link #finished()}
	 * hasn't yet been called.
	 */
	void startJob();

	/**
	 * Processes an object.
	 *
	 * @param obj the object to process
	 * @throws ObjectProcessingException if an error occurs while processing an object
	 */
	void processObject(SerializableObject obj) throws ObjectProcessingException;

	/**
	 * A hook method to be invoked when there are no more objects to process.
	 */
	void finished();

}

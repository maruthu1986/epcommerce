/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.domain.order.jobs;

/**
 * Order lock cleanup result will contain information pertinent
 * to the running of the cleanup job.
 */
public interface OrderLockCleanupResult {


	/**
	 * Returns the total number of batch runs that were required
	 * for the order lock cleanup job to complete.
	 *
	 * @return the total number of batch runs
	 */
	int getNumberOfBatchRuns();

	/**
	 * Sets the total number of batch runs that were required for
	 * the order lock cleanup job to complete.
	 *
	 * @param numberBatchRuns - the total number of batch runs required until completion
	 */
	void setNumberOfBatchRuns(int numberBatchRuns);

	/**
	 * Returns the total number of order locks that were removed during the job.
	 *
	 * @return the total number of order locks removed
	 */
	int getNumberOfLocksRemoved();

	/**
	 * Sets the total number of order locks that were removed during the job.
	 *
	 * @param numberLocksRemoved - the total number of order locks removed
	 */
	void setNumberOfLocksRemoved(int numberLocksRemoved);

	/**
	 * Sets the time taken to execute the order lock clean up job.
	 *
	 * @param timeToExecute - the time taken to execute the order lock clean up job
	 */
	void setTimeToExecute(long timeToExecute);

	/**
	 * Returns the time taken to execute the order lock clean up job.
	 *
	 * @return the time taken to execute the order lock clean up job.
	 */
	long getTimeToExecute();
}

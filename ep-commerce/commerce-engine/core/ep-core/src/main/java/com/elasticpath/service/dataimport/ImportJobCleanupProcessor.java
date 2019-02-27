/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.service.dataimport;

/**
 * Defines the import job data cleanup methods.
 */
public interface ImportJobCleanupProcessor {

	/**
	 * The method called on the cleanup processor.
	 * 
	 * @return the number of import jobs cleaned up (data deleted)
	 */
	int cleanupImportJobData();
	
	/**
	 * Processes all the stale import jobs that could be found.
	 */
	void processStaleImportJobs();

	/**
	 * Removes old imported CSV files.
	 * The method is called from CM.
	 *
	 * @return number of deleted files.
	 * The method will try to delete as many as possible files, logging those that currently couldn't be deleted.
	 */
	int cleanupStaleImportCSVFiles();
}

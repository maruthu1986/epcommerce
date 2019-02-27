/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.client;

/**
 * An interface holding information for the sync tool configuration parameters.
 */
public interface SyncJobConfiguration {

	/**
	 * Gets the adapter parameter.
	 * 
	 * @return the adapter parameter
	 */
	String getAdapterParameter();

	/**
	 * Gets the root path.
	 * 
	 * @return the root path
	 */
	String getRootPath();
	
	/**
	 * Gets the sub directory relevant to root path.
	 * 
	 * @return the sub-directory
	 */
	String getSubDir();

	/**
	 * Gets an identifier for the current job execution.
	 *
	 * @return an identifier for the current job execution
	 */
	String getExecutionId();

}

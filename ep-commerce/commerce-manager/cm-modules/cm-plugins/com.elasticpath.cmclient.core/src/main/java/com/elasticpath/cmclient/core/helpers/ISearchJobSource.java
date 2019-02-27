/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.helpers;

/**
 * Interface defining accessors for a search job's source.
 */
public interface ISearchJobSource {

	/**
	 * Get the source which call this job.
	 * 
	 * @return the source
	 */
	Object getSource();

	/**
	 * Get the source which call this job.
	 * 
	 * @param source the source object
	 */
	void setSource(Object source);

}
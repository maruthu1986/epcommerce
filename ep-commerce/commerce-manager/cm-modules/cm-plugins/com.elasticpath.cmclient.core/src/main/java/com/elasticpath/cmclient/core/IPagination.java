/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core;

/**
 * IPagination interface to get current page size settings.
 *
 */
public interface IPagination {

	/**
	 * Returns the pagination settings for the application.
	 * 
	 * @return the pagination settings for the application
	 */
	int getPagination();

}
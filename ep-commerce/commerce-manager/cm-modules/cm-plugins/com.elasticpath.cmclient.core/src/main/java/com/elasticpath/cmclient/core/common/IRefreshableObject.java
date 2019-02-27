/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.core.common;

/**
 * An interface representing an object that has
 * a refresh strategy.
 */
public interface IRefreshableObject {

	/**
	 * Refreshes the object.
	 */
	void refresh();
}

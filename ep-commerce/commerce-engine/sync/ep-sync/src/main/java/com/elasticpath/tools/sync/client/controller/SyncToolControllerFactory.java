/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.tools.sync.client.controller;

import com.elasticpath.tools.sync.client.SyncToolControllerType;

/**
 * A factory responsible for creating a controller.
 */
public interface SyncToolControllerFactory {

	/**
	 * Creates the controller to drive the synchronization.
	 *
	 * @param controllerType the type of controller to create.
	 * @return the synchronization controller
	 */
	SyncToolController createController(SyncToolControllerType controllerType);
}

/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.schema.uri;

/**
 * Builder for navigations uris.
 */
public interface NavigationsUriBuilder extends ScopedUriBuilder<NavigationsUriBuilder> {

	/**
	 * Sets the navigation ID.
	 *
	 * @param navigationId the navigation ID
	 * @return the builder
	 */
	NavigationsUriBuilder setNavigationId(String navigationId);
}

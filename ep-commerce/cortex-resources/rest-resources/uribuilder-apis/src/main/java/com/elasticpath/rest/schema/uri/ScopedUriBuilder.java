/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.schema.uri;


/**
 * Base Interface to define the scope for a uri being built.
 *
 * @param <T> The type of the builder. Use the sub-interface when defining one.
 */
public interface ScopedUriBuilder<T> extends UriBuilder {

	/**
	 * Sets the scope.
	 *
	 * @param scope the scope
	 * @return the builder
	 */
	T setScope(String scope);
}

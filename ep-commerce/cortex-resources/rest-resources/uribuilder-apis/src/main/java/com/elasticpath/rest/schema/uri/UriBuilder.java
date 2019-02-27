/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.schema.uri;

/**
 * Base interface of Uri Builders.
 */
public interface UriBuilder {

	/**
	 * Builds the URI.
	 *
	 * @return the URI
	 */
	String build();
}

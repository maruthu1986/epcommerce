/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.schema.uri;

/**
 * URI builder that knows how to build read from other links.
 * @param <T> The type of the builder. Use the sub-interface when defining one.
 */
public interface ReadFromOtherUriBuilder<T> extends UriBuilder {

	/**
	 * Set the source URI.
	 * @param sourceUri the uri of the "other"
	 * @return this builder
	 */
	T setSourceUri(String sourceUri);
}

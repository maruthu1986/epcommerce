/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.schema.uri;

/**
 * Builder of asset URIs.
 */
public interface AssetsUriBuilder extends ScopedUriBuilder<AssetsUriBuilder> {

	/**
	 * Sets the asset ID.
	 *
	 * @param assetId the asset id
	 * @return the builder
	 */
	AssetsUriBuilder setAssetId(String assetId);
}

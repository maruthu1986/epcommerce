/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.schema.uri;

/**
 * Builds URIs to Purchase resources.
 *
 * @deprecated remove once dependent resources are converted to Helix.
 */
@Deprecated
public interface PurchaseUriBuilder extends ScopedUriBuilder<PurchaseUriBuilder> {

	/**
	 * Set the purchase ID.
	 * @param encodedPurchaseId purchase ID
	 * @return this builder
	 */
	PurchaseUriBuilder setPurchaseId(String encodedPurchaseId);

	/**
	 * Set the purchase ID.
	 * @param decodedPurchaseId purchase ID
	 * @return this builder
	 */
	PurchaseUriBuilder setDecodedPurchaseId(String decodedPurchaseId);

}

/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.schema.uri;

/**
 * Builds a URI to a specific address.
 *
 * @deprecated remove once dependent resources are converted to Helix.
 */
@Deprecated
public interface AddressUriBuilder extends ScopedUriBuilder<AddressUriBuilder> {

	/**
	 * Set the address ID.
	 *
	 * @param addressId the address ID
	 * @return this builder
	 */
	AddressUriBuilder setAddressId(String addressId);
}

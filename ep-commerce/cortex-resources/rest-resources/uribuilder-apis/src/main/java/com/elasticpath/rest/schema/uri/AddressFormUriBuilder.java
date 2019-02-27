/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.schema.uri;

/**
 * Builds a URI to the address form.
 *
 * @deprecated remove once dependent resources are converted to Helix.
 */
@Deprecated
public interface AddressFormUriBuilder extends ScopedUriBuilder<AddressFormUriBuilder> {

	/**
	 * Set the scope for the command.
	 *
	 * @param scope The scope.
	 * @return This builder instance.
	 */
	AddressFormUriBuilder setScope(String scope);
}

/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.commons.addresses.transform;

import com.elasticpath.domain.customer.Address;
import com.elasticpath.rest.definition.addresses.AddressEntity;

/**
 * Transformer that takes an {@link Address} instance and converts it to an {@link Address}.
 */
public interface AddressTransformer {
	/**
	 * Convert an {@link Address} into an {@link AddressEntity}.
	 * @param address the address to convert
	 * @return the resulting {@link AddressEntity}
	 */
	AddressEntity transformAddressToEntity(Address address);
}
/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.addresses.validator;

import io.reactivex.Completable;

import com.elasticpath.rest.definition.addresses.AddressEntity;

/**
 * Validator for an {@link AddressEntity}.
 */
public interface AddressValidator {

	/**
	 * Validate an address entity.
	 *
	 * @param addressEntity the address entity
	 * @return an error if validation fails, success otherwise
	 */
	Completable validate(AddressEntity addressEntity);
}

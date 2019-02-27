/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.addresses.converters;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.springframework.core.convert.converter.Converter;

import com.elasticpath.domain.customer.Address;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.resource.integration.commons.addresses.transform.AddressTransformer;

/**
 * Converter for AddressEntity.
 */
@Singleton
@Named
public class AddressEntityConverter implements Converter<Address, AddressEntity> {

	private final AddressTransformer addressTransformer;

	/**
	 * Constructor.
	 *
	 * @param addressTransformer addressTransformer
	 */
	@Inject
	public AddressEntityConverter(
			@Named("addressTransformer")
			final AddressTransformer addressTransformer) {
		this.addressTransformer = addressTransformer;
	}

	@Override
	public AddressEntity convert(final Address address) {
		return AddressEntity.builderFrom(addressTransformer.transformAddressToEntity(address))
				.withAddressId(address.getGuid())
				.build();
	}
}

/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers.impl;

import static java.util.Objects.requireNonNull;

import java.util.function.Supplier;

import com.elasticpath.domain.customer.Address;
import com.elasticpath.service.shipping.transformers.ShippingAddressTransformer;
import com.elasticpath.shipping.connectivity.dto.ShippingAddress;
import com.elasticpath.shipping.connectivity.dto.builder.ShippingAddressBuilder;

/**
 * Implementation of {@link ShippingAddressTransformer}.
 */
public class ShippingAddressTransformerImpl implements ShippingAddressTransformer {

	private Supplier<ShippingAddressBuilder> shippingAddressBuilderSupplier;

	@Override
	public ShippingAddress apply(final Address address) {
		requireNonNull(address, "Address is required");

		return shippingAddressBuilderSupplier.get()
				.withGuid(address.getGuid())
				.withStreet1(address.getStreet1())
				.withStreet2(address.getStreet2())
				.withCity(address.getCity())
				.withSubCountry(address.getSubCountry())
				.withZipOrPostalCode(address.getZipOrPostalCode())
				.withCountry(address.getCountry())
				.build();

	}

	protected Supplier<ShippingAddressBuilder> getShippingAddressBuilderSupplier() {
		return this.shippingAddressBuilderSupplier;
	}

	public void setShippingAddressBuilderSupplier(final Supplier<ShippingAddressBuilder> shippingAddressBuilderSupplier) {
		this.shippingAddressBuilderSupplier = shippingAddressBuilderSupplier;
	}

}

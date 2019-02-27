/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.dto.builder.impl;

import java.util.Optional;
import java.util.function.Supplier;

import com.elasticpath.shipping.connectivity.dto.ShippingAddress;
import com.elasticpath.shipping.connectivity.dto.builder.ShippingAddressBuilder;
import com.elasticpath.shipping.connectivity.dto.builder.populators.impl.AbstractRespawnBuilderPopulatorImpl;
import com.elasticpath.shipping.connectivity.dto.impl.ShippingAddressImpl;

/**
 * Implementation of {@link ShippingAddressBuilder} to build {@link ShippingAddress}.
 */
public class ShippingAddressBuilderImpl extends AbstractRespawnBuilderPopulatorImpl<ShippingAddress, ShippingAddressImpl, ShippingAddressBuilder>
		implements ShippingAddressBuilder {

	@Override
	protected void copy(final ShippingAddress type) {
		withGuid(type.getGuid());
		withStreet1(type.getStreet1());
		withStreet2(type.getStreet2());
		withCity(type.getCity());
		withSubCountry(type.getSubCountry());
		withCountry(type.getCountry());
		withZipOrPostalCode(type.getZipOrPostalCode());
	}

	@Override
	public ShippingAddressBuilder withGuid(final String guid) {
		getInstanceUnderBuild().setGuid(guid);
		return this;
	}

	@Override
	public ShippingAddressBuilder withStreet1(final String street1) {
		getInstanceUnderBuild().setStreet1(street1);
		return this;
	}

	@Override
	public ShippingAddressBuilder withStreet2(final String street2) {
		getInstanceUnderBuild().setStreet2(street2);
		return this;
	}

	@Override
	public ShippingAddressBuilder withCity(final String city) {
		getInstanceUnderBuild().setCity(city);
		return this;
	}

	@Override
	public ShippingAddressBuilder withSubCountry(final String subCountry) {
		getInstanceUnderBuild().setSubCountry(subCountry);
		return this;
	}

	@Override
	public ShippingAddressBuilder withZipOrPostalCode(final String zipOrPostalCode) {
		getInstanceUnderBuild().setZipOrPostalCode(zipOrPostalCode);
		return this;
	}

	@Override
	public ShippingAddressBuilder withCountry(final String country) {
		getInstanceUnderBuild().setCountry(country);
		return this;
	}

	@Override
	public ShippingAddressBuilder getPopulator() {
		return this;
	}

	@Override
	protected Optional<Supplier<ShippingAddressImpl>> createDefaultInstanceSupplier() {
		return Optional.of(ShippingAddressImpl::new);
	}
}

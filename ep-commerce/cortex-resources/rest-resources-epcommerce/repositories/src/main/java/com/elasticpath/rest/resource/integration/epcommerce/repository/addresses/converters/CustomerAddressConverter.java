/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.addresses.converters;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.springframework.core.convert.converter.Converter;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.rest.definition.addresses.AddressDetailEntity;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.base.NameEntity;

/**
 * Converter for CustomerAddress.
 */
@Singleton
@Named
public class CustomerAddressConverter implements Converter<AddressEntity, CustomerAddress> {

	private final BeanFactory coreBeanFactory;

	/**
	 * Constructor.
	 *
	 * @param coreBeanFactory    coreBeanFactory
	 */
	@Inject
	public CustomerAddressConverter(
			@Named("coreBeanFactory")
			final BeanFactory coreBeanFactory) {
		this.coreBeanFactory = coreBeanFactory;
	}

	@Override
	public CustomerAddress convert(final AddressEntity addressEntity) {
		CustomerAddress domainAddress = coreBeanFactory.getBean(ContextIdNames.CUSTOMER_ADDRESS);

		AddressDetailEntity address = addressEntity.getAddress();
		if (address != null) {
			domainAddress.setStreet1(address.getStreetAddress());
			domainAddress.setStreet2(address.getExtendedAddress());
			domainAddress.setCity(address.getLocality());
			domainAddress.setSubCountry(address.getRegion());
			domainAddress.setCountry(address.getCountryName());
			domainAddress.setZipOrPostalCode(address.getPostalCode());
			domainAddress.setPhoneNumber(address.getPhoneNumber());
			domainAddress.setOrganization(address.getOrganization());
		}

		NameEntity nameEntity = addressEntity.getName();

		if (nameEntity != null) {
			domainAddress.setFirstName(nameEntity.getGivenName());
			domainAddress.setLastName(nameEntity.getFamilyName());
		}

		return domainAddress;
	}
}

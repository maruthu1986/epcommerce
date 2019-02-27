/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.commons.addresses.transform.impl;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;

import com.elasticpath.domain.customer.Address;
import com.elasticpath.rest.definition.addresses.AddressDetailEntity;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.base.NameEntity;
import com.elasticpath.rest.resource.integration.commons.addresses.transform.AddressTransformer;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * Implementation of {@link AddressTransformer}.
 */
@Component(property = AbstractDomainTransformer.DS_SERVICE_RANKING)
public class AddressTransformerImpl implements AddressTransformer {

	@Override
	public AddressEntity transformAddressToEntity(final Address address) {
		AddressDetailEntity addressDetailEntity = AddressDetailEntity.builder()
				.withCountryName(StringUtils.trimToNull(address.getCountry()))
				.withRegion(StringUtils.trimToNull(address.getSubCountry()))
				.withLocality(StringUtils.trimToNull(address.getCity()))
				.withStreetAddress(StringUtils.trimToNull(address.getStreet1()))
				.withExtendedAddress(StringUtils.trimToNull(address.getStreet2()))
				.withPostalCode(StringUtils.trimToNull(address.getZipOrPostalCode()))
				.withPhoneNumber(StringUtils.trimToNull(address.getPhoneNumber()))
				.withOrganization(StringUtils.trimToNull(address.getOrganization()))
				.build();
		NameEntity nameEntity = NameEntity.builder()
				.withGivenName(StringUtils.trimToNull(address.getFirstName()))
				.withFamilyName(StringUtils.trimToNull(address.getLastName()))
				.build();

		return AddressEntity.builder()
				.withAddressId(address.getGuid())
				.withAddress(addressDetailEntity)
				.withName(nameEntity)
				.build();
	}
}
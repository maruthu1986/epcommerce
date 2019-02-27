/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.prototypes;

import io.reactivex.Single;
import org.apache.commons.lang3.StringUtils;

import com.elasticpath.rest.definition.addresses.AddressDetailEntity;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.addresses.AddressFormResource;
import com.elasticpath.rest.definition.base.NameEntity;

/**
 * Address Form prototype for Read operation.
 */
public class ReadAddressFormPrototype implements AddressFormResource.Read {

	@Override
	public Single<AddressEntity> onRead() {
		AddressDetailEntity addressDetailEntity = AddressDetailEntity.builder()
				.withCountryName(StringUtils.EMPTY)
				.withExtendedAddress(StringUtils.EMPTY)
				.withLocality(StringUtils.EMPTY)
				.withPostalCode(StringUtils.EMPTY)
				.withRegion(StringUtils.EMPTY)
				.withStreetAddress(StringUtils.EMPTY)
				.withPhoneNumber(StringUtils.EMPTY)
				.withOrganization(StringUtils.EMPTY)
				.build();

		NameEntity nameEntity = NameEntity.builder()
				.withFamilyName(StringUtils.EMPTY)
				.withGivenName(StringUtils.EMPTY)
				.build();

		return Single.just(AddressEntity.builder()
				.withAddress(addressDetailEntity)
				.withName(nameEntity)
				.build());
	}
}

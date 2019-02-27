/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.addresses.validator.impl;

import javax.inject.Named;
import javax.inject.Singleton;

import io.reactivex.Completable;

import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.addresses.AddressDetailEntity;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.base.NameEntity;
import com.elasticpath.rest.definitions.validator.constants.ValidationMessages;
import com.elasticpath.rest.resource.integration.epcommerce.repository.addresses.validator.AddressValidator;

/**
 * Implementation for AddressValidator.
 */
@Singleton
@Named("addressValidator")
public class AddressValidatorImpl implements AddressValidator {

	private static final String NO_VALID_ADDRESS_FIELDS = "No valid address fields specified.";

	@Override
	public Completable validate(final AddressEntity addressEntity) {
		if (addressEntity == null) {
			return Completable.error(ResourceOperationFailure.badRequestBody(ValidationMessages.MISSING_REQUIRED_REQUEST_BODY));
		}
		if (!representationHasFields(addressEntity)) {
			return Completable.error(ResourceOperationFailure.badRequestBody(NO_VALID_ADDRESS_FIELDS));
		}
		return Completable.complete();
	}

	private boolean representationHasFields(final AddressEntity address) {
		AddressDetailEntity addressDetailsEntity = address.getAddress();
		NameEntity nameEntity = address.getName();

		return nameEntityHasData(nameEntity)
				|| addressEntityHasData(addressDetailsEntity);
	}

	private boolean nameEntityHasData(final NameEntity nameEntity) {
		return nameEntity != null
				&& (
				nameEntity.getFamilyName() != null
						|| nameEntity.getGivenName() != null
		);
	}

	private boolean addressEntityHasData(final AddressDetailEntity addressDetailsEntity) {
		return addressDetailsEntity != null
				&& (
				addressDetailsEntity.getStreetAddress() != null
						|| addressDetailsEntity.getExtendedAddress() != null
						|| addressDetailsEntity.getLocality() != null
						|| addressDetailsEntity.getRegion() != null
						|| addressDetailsEntity.getCountryName() != null
						|| addressDetailsEntity.getPostalCode() != null
						|| addressDetailsEntity.getPhoneNumber() != null
						|| addressDetailsEntity.getOrganization() != null
		);
	}
}

/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.addresses.validator.impl;

import org.junit.Test;

import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.addresses.AddressDetailEntity;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.base.NameEntity;
import com.elasticpath.rest.definitions.validator.constants.ValidationMessages;

/**
 * Contains tests for {@link AddressValidatorImpl}.
 */
public final class AddressValidatorTest {
	private static final String NO_VALID_ADDRESS_FIELDS = "No valid address fields specified.";

	private static final String STREET_ADDRESS = "STREET_ADDRESS";
	private static final String EXTENDED_ADDRESS = "EXTENDED_ADDRESS";
	private static final String LOCALITY = "LOCALITY";
	private static final String REGION = "REGION";
	private static final String COUNTRY_NAME = "COUNTRY_NAME";
	private static final String POSTAL_CODE = "POSTAL_CODE";
	private static final String FIRST_NAME = "FIRST_NAME";
	private static final String LAST_NAME = "LAST_NAME";

	private final AddressValidatorImpl validator = new AddressValidatorImpl();

	/**
	 * Test validate given null for the representation argument.
	 */
	@Test
	public void testValidateGivenNull() {
		validator.validate(null)
				.test()
				.assertError(ResourceOperationFailure.badRequestBody(ValidationMessages.MISSING_REQUIRED_REQUEST_BODY));
	}

	/**
	 * Test validation on address entity with no fields populated within the address representation.
	 */
	@Test
	public void testValidationOnAddressEntityWithNoFieldsPopulatedWithinAddressRepresentation() {
		AddressDetailEntity addressDetailsEntity = createAddressDetailsEntity(null, null, null, null, null, null);
		testEntityFieldPopulationWithFailure(null, addressDetailsEntity, NO_VALID_ADDRESS_FIELDS);
	}

	/**
	 * Test validation of street address on address entity within the address representation.
	 */
	@Test
	public void testValidationOfStreetAddressOnAddressEntityWithinAddressRepresentation() {
		AddressDetailEntity addressDetailsEntity = createAddressDetailsEntity(STREET_ADDRESS, null, null, null, null, null);
		testEntityFieldPopulationWithSuccess(null, addressDetailsEntity);
	}

	/**
	 * Test validation of extended address on address entity within the address representation.
	 */
	@Test
	public void testValidationOfExtendedAddressOnAddressEntityWithinAddressRepresentation() {
		AddressDetailEntity addressDetailsEntity = createAddressDetailsEntity(null, EXTENDED_ADDRESS, null, null, null, null);
		testEntityFieldPopulationWithSuccess(null, addressDetailsEntity);
	}

	/**
	 * Test validation of locality on address entity within the address representation.
	 */
	@Test
	public void testValidationOfLocalityOnAddressEntityWithinAddressRepresentation() {
		AddressDetailEntity addressDetailsEntity = createAddressDetailsEntity(null, null, LOCALITY, null, null, null);
		testEntityFieldPopulationWithSuccess(null, addressDetailsEntity);
	}

	/**
	 * Test validation of region on address entity within the address representation.
	 */
	@Test
	public void testValidationOfRegionOnAddressEntityWithinAddressRepresentation() {
		AddressDetailEntity addressDetailsEntity = createAddressDetailsEntity(null, null, null, REGION, null, null);
		testEntityFieldPopulationWithSuccess(null, addressDetailsEntity);
	}

	/**
	 * Test validation of country name on address entity within the address representation.
	 */
	@Test
	public void testValidationOfCountryNameOnAddressEntityWithinAddressRepresentation() {
		AddressDetailEntity addressDetailsEntity = createAddressDetailsEntity(null, null, null, null, COUNTRY_NAME, null);
		testEntityFieldPopulationWithSuccess(null, addressDetailsEntity);
	}

	/**
	 * Test validation of postal code on address entity within the address representation.
	 */
	@Test
	public void testValidationOfPostalCodeOnAddressEntityWithinAddressRepresentation() {
		AddressDetailEntity addressDetailsEntity = createAddressDetailsEntity(null, null, null, null, null, POSTAL_CODE);
		testEntityFieldPopulationWithSuccess(null, addressDetailsEntity);
	}

	/**
	 * Test validation on name entity with no fields populated within the address representation.
	 */
	@Test
	public void testValidationOnNameEntityWithNoFieldsPopulatedWithinAddressRepresentation() {
		NameEntity nameEntity = createNameEntity(null, null);
		testEntityFieldPopulationWithFailure(nameEntity, null, NO_VALID_ADDRESS_FIELDS);
	}

	/**
	 * Test validation of given name on name entity within the address representation.
	 */
	@Test
	public void testValidationOfGivenNameOnNameEntityWithinAddressRepresentation() {
		NameEntity nameEntity = createNameEntity(FIRST_NAME, null);
		testEntityFieldPopulationWithSuccess(nameEntity, null);
	}

	/**
	 * Test validation of family name on name entity within the address representation.
	 */
	@Test
	public void testValidationOfFamilyNameOnNameEntityWithinAddressRepresentation() {
		NameEntity nameEntity = createNameEntity(null, LAST_NAME);
		testEntityFieldPopulationWithSuccess(nameEntity, null);
	}

	private void testEntityFieldPopulationWithSuccess(final NameEntity nameEntity,
													  final AddressDetailEntity addressDetailsEntity) {
		AddressEntity addressEntity = createAddressEntity(nameEntity, addressDetailsEntity);
		validator.validate(addressEntity)
				.test()
				.assertNoErrors();
	}

	private void testEntityFieldPopulationWithFailure(final NameEntity nameEntity,
													  final AddressDetailEntity addressDetailsEntity,
													  final String failureMessage) {
		AddressEntity addressEntity = createAddressEntity(nameEntity, addressDetailsEntity);
		validator.validate(addressEntity)
				.test()
				.assertError(ResourceOperationFailure.badRequestBody(failureMessage));
	}

	private AddressEntity createAddressEntity(final NameEntity name, final AddressDetailEntity addressDetailsEntity) {
		return AddressEntity.builder().withName(name).withAddress(addressDetailsEntity).build();
	}

	private AddressDetailEntity createAddressDetailsEntity(final String streetAddress,
															final String extendedAddress,
															final String locality,
															final String region,
															final String countryName,
															final String postalCode) {

		return AddressDetailEntity.builder()
				.withStreetAddress(streetAddress)
				.withExtendedAddress(extendedAddress)
				.withLocality(locality)
				.withRegion(region)
				.withCountryName(countryName)
				.withPostalCode(postalCode)
				.build();
	}

	private NameEntity createNameEntity(final String givenName, final String familyName) {
		return NameEntity.builder()
				.withGivenName(givenName)
				.withFamilyName(familyName)
				.build();
	}
}

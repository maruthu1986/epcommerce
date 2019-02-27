/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.profiles.prototypes;

import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.definition.profiles.ProfileIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.impl.ProfileEntityRepositoryImpl;

/**
 * Test class for {@link com.elasticpath.rest.resource.profiles.prototypes.ReadProfilePrototype}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ReadProfilesPrototypeTest {

	@InjectMocks
	private ReadProfilePrototype readProfilePrototype;

	@Mock
	@SuppressWarnings("rawtypes")
	private ProfileEntityRepositoryImpl profileEntityRepository;

	@Mock
	private ProfileIdentifier profileIdentifier;

	@Test
	public void shouldReturnCustomerProfile() {

		readProfilePrototype.onRead();

		verify(profileEntityRepository).findOne(profileIdentifier);
	}

}

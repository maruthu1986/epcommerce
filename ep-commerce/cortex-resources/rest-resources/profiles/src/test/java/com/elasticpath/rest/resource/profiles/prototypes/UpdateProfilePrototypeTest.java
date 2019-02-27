/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.profiles.prototypes;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import io.reactivex.Completable;
import io.reactivex.Single;

import com.elasticpath.rest.definition.profiles.ProfileEntity;
import com.elasticpath.rest.definition.profiles.ProfileIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.impl.ProfileEntityRepositoryImpl;

/**
 * Test class for {@link com.elasticpath.rest.resource.profiles.prototypes.UpdateProfilePrototype}.
 */
@RunWith(MockitoJUnitRunner.class)
public class UpdateProfilePrototypeTest {

	@InjectMocks
	private UpdateProfilePrototype updateProfilePrototype;

	@Mock
	@SuppressWarnings("rawtypes")
	private ProfileEntityRepositoryImpl profileEntityRepository;

	@Mock
	private ProfileIdentifier profileIdentifier;

	@Mock
	private ProfileEntity profileEntity;

	@Test
	public void shouldUpdateCustomerFromProfileEntity() {

		when(profileEntityRepository.update(profileEntity, profileIdentifier)).thenReturn(Single.just("this").toCompletable());

		assertThat(updateProfilePrototype.onUpdate(), instanceOf(Completable.class));
		verify(profileEntityRepository).update(profileEntity, profileIdentifier);
	}

}

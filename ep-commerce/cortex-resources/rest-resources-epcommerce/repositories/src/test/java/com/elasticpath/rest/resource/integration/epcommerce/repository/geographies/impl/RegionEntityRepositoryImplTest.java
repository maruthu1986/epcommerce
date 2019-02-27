/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.geographies.impl;

import static org.mockito.Mockito.when;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.misc.Geography;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.geographies.CountriesIdentifier;
import com.elasticpath.rest.definition.geographies.CountryIdentifier;
import com.elasticpath.rest.definition.geographies.RegionEntity;
import com.elasticpath.rest.definition.geographies.RegionIdentifier;
import com.elasticpath.rest.definition.geographies.RegionsIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl.ReactiveAdapterImpl;

/**
 * Test for {@link RegionEntityRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class RegionEntityRepositoryImplTest {

	private static final String TEST_COUNTRY_ID = "testCountryID";
	private static final String TEST_REGION_ID = "testRegionID";
	private static final Locale TEST_LOCALE = Locale.CANADA;
	private static final String STORE_CODE = "STORE_CODE";
	private static final String USER_ID = "userid";

	@Mock
	private ResourceOperationContext resourceOperationContext;

	@Mock
	private Geography geography;

	@InjectMocks
	private ReactiveAdapterImpl reactiveAdapterImpl;

	@InjectMocks
	private RegionEntityRepositoryImpl<RegionEntity, RegionIdentifier> regionEntityRepository;

	@Before
	public void initialize() {
		regionEntityRepository.setReactiveAdapter(reactiveAdapterImpl);
	}

	@Test
	public void shouldGetRegion() {
		shouldFindSubject();
		when(geography.getSubCountryDisplayName(TEST_COUNTRY_ID, TEST_REGION_ID, TEST_LOCALE)).thenReturn("Canada");
		regionEntityRepository.findOne(getRegionIdentifier())
				.test()
				.assertNoErrors();
	}

	@Test
	public void shouldFailWhenRegionNotFound() {
		shouldFindSubject();
		regionEntityRepository.findOne(getRegionIdentifier())
				.test()
				.assertError(ResourceOperationFailure.class);
	}

	private void shouldFindSubject() {
		Subject subject = TestSubjectFactory.createWithScopeAndUserIdAndLocale(STORE_CODE, USER_ID, TEST_LOCALE);
		when(resourceOperationContext.getSubject()).thenReturn(subject);
	}

	private RegionIdentifier getRegionIdentifier() {
		CountriesIdentifier countriesIdentifier = CountriesIdentifier.builder()
				.withScope(StringIdentifier.of("scope"))
				.build();

		CountryIdentifier countryIdentifier = CountryIdentifier.builder()
				.withCountries(countriesIdentifier)
				.withCountryId(StringIdentifier.of(TEST_COUNTRY_ID))
				.build();

		RegionsIdentifier regionsIdentifier = RegionsIdentifier.builder()
				.withCountry(countryIdentifier)
				.build();

		return RegionIdentifier.builder()
				.withRegionId(StringIdentifier.of(TEST_REGION_ID))
				.withRegions(regionsIdentifier)
				.build();
	}
}
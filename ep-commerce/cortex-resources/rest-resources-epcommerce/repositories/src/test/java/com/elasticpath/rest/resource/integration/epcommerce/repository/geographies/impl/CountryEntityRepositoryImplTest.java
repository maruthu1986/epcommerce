/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.geographies.impl;

import static org.mockito.Mockito.when;

import java.util.Locale;
import java.util.Set;

import com.google.common.collect.Iterators;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.misc.Geography;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.geographies.CountriesIdentifier;
import com.elasticpath.rest.definition.geographies.CountryEntity;
import com.elasticpath.rest.definition.geographies.CountryIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl.ReactiveAdapterImpl;

/**
 * Test for {@link CountryEntityRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CountryEntityRepositoryImplTest {

	private static final String TEST_COUNTRY_ID = "testCountryID";
	private static final Locale TEST_LOCALE = Locale.CANADA;
	private static final String STORE_CODE = "STORE_CODE";
	private static final String USER_ID = "userid";

	@Mock
	private Set<String> countryCodes;

	@Mock
	private ResourceOperationContext resourceOperationContext;

	@Mock
	private Geography geography;

	@InjectMocks
	private ReactiveAdapterImpl reactiveAdapterImpl;

	@InjectMocks
	private CountryEntityRepositoryImpl<CountryEntity, CountryIdentifier> countryEntityRepository;

	@Before
	public void initialize() {
		countryEntityRepository.setReactiveAdapter(reactiveAdapterImpl);
	}

	@Test
	public void shouldGetAllCountries() {
		final int numOfCountryCodes = 3;
		when(countryCodes.iterator()).thenReturn(Iterators.forArray("CA", "US", "GB"));
		when(geography.getCountryCodes()).thenReturn(countryCodes);
		countryEntityRepository.findAll(StringIdentifier.of("scope"))
				.test()
				.assertValueCount(numOfCountryCodes)
				.assertNoErrors();
	}

	@Test
	public void shouldGetNoCountries() {
		when(countryCodes.iterator()).thenReturn(Iterators.forArray());
		when(geography.getCountryCodes()).thenReturn(countryCodes);
		countryEntityRepository.findAll(StringIdentifier.of("scope"))
				.test()
				.assertValueCount(0)
				.assertNoErrors();
	}

	@Test
	public void shouldGetCountry() {
		shouldFindSubject();
		when(geography.getCountryDisplayName(TEST_COUNTRY_ID, TEST_LOCALE)).thenReturn("Canada");
		countryEntityRepository.findOne(getCountriesIdentifier())
				.test()
				.assertNoErrors();
	}

	@Test
	public void shouldFailWhenCountryNotFound() {
		shouldFindSubject();
		countryEntityRepository.findOne(getCountriesIdentifier())
				.test()
				.assertError(ResourceOperationFailure.class);

	}

	private void shouldFindSubject() {
		Subject subject = TestSubjectFactory.createWithScopeAndUserIdAndLocale(STORE_CODE, USER_ID, TEST_LOCALE);
		when(resourceOperationContext.getSubject()).thenReturn(subject);
	}

	private CountryIdentifier getCountriesIdentifier() {
		CountriesIdentifier countriesIdentifier = CountriesIdentifier.builder()
				.withScope(StringIdentifier.of("scope"))
				.build();

		return CountryIdentifier.builder()
				.withCountries(countriesIdentifier)
				.withCountryId(StringIdentifier.of(TEST_COUNTRY_ID))
				.build();
	}
}
/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.geographies.impl;

import static org.mockito.Mockito.when;

import java.util.Set;

import com.google.common.collect.Iterators;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.misc.Geography;
import com.elasticpath.rest.definition.geographies.CountriesIdentifier;
import com.elasticpath.rest.definition.geographies.CountryIdentifier;
import com.elasticpath.rest.definition.geographies.RegionIdentifier;
import com.elasticpath.rest.definition.geographies.RegionsIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl.ReactiveAdapterImpl;

/**
 * Test for {@link RegionIdentifierRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class RegionIdentifierRepositoryImplTest {

	private static final String TEST_COUNTRY_ID = "testCountryID";

	@Mock
	private Set<String> regionsCodes;

	@Mock
	private Geography geography;

	@InjectMocks
	private ReactiveAdapterImpl reactiveAdapterImpl;

	@InjectMocks
	private RegionIdentifierRepositoryImpl<RegionsIdentifier, RegionIdentifier> regionIdentifierRepository;


	@Before
	public void initialize() {
		regionIdentifierRepository.setReactiveAdapter(reactiveAdapterImpl);
	}

	@Test
	public void shouldGetAllRegions() {
		final int numOfRegionsCodes = 4;
		when(regionsCodes.iterator()).thenReturn(Iterators.forArray("BC", "AB", "ON", "QC"));
		when(geography.getSubCountryCodes(TEST_COUNTRY_ID)).thenReturn(regionsCodes);
		regionIdentifierRepository.getElements(getRegionsIdentifier())
				.test()
				.assertValueCount(numOfRegionsCodes)
				.assertNoErrors();
	}

	@Test
	public void shouldGetNoRegions() {
		regionIdentifierRepository.getElements(getRegionsIdentifier())
				.test()
				.assertValueCount(0)
				.assertNoErrors();
	}

	private RegionsIdentifier getRegionsIdentifier() {
		CountriesIdentifier countriesIdentifier = CountriesIdentifier.builder()
				.withScope(StringIdentifier.of("scope"))
				.build();

		CountryIdentifier countryIdentifier = CountryIdentifier.builder()
				.withCountries(countriesIdentifier)
				.withCountryId(StringIdentifier.of(TEST_COUNTRY_ID))
				.build();

		return RegionsIdentifier.builder()
				.withCountry(countryIdentifier)
				.build();
	}

}
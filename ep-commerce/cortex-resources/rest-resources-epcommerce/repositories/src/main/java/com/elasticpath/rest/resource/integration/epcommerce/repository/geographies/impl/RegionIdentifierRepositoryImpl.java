/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.geographies.impl;

import io.reactivex.Observable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.misc.Geography;
import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.geographies.RegionIdentifier;
import com.elasticpath.rest.definition.geographies.RegionsIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;

/**
 * Repository for regions in a country.
 *
 * @param <I>  the identifier type
 * @param <LI> the linked identifier type
 */
@Component
public class RegionIdentifierRepositoryImpl<I extends RegionsIdentifier, LI extends RegionIdentifier>
		implements LinksRepository<RegionsIdentifier, RegionIdentifier> {

	private static final String CANNOT_FIND_REGIONS = "Cannot find regions.";
	private Geography geography;
	private ReactiveAdapter reactiveAdapter;

	@Override
	public Observable<RegionIdentifier> getElements(final RegionsIdentifier regionsIdentifier) {
		String countryId = regionsIdentifier.getCountry().getCountryId().getValue();

		return getSubCountryCodes(countryId)
				.map(regionCode -> RegionIdentifier.builder()
						.withRegions(regionsIdentifier)
						.withRegionId(StringIdentifier.of(regionCode))
						.build());
	}

	/**
	 * Returns all the region codes for the given country ID.
	 *
	 * @param countryId country ID
	 * @return region codes
	 */
	protected Observable<String> getSubCountryCodes(final String countryId) {
		return reactiveAdapter.fromNullable(() -> geography.getSubCountryCodes(countryId), CANNOT_FIND_REGIONS)
				.flatMapIterable(regionCodes -> regionCodes);
	}

	@Reference
	public void setGeography(final Geography geography) {
		this.geography = geography;
	}

	@Reference
	public void setReactiveAdapter(final ReactiveAdapter reactiveAdapter) {
		this.reactiveAdapter = reactiveAdapter;
	}
}

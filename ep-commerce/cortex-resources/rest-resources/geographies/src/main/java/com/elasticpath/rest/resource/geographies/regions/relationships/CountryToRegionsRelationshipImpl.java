/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.geographies.regions.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.geographies.CountryIdentifier;
import com.elasticpath.rest.definition.geographies.RegionsForCountryRelationship;
import com.elasticpath.rest.definition.geographies.RegionsIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Country to regions link.
 */
public class CountryToRegionsRelationshipImpl implements RegionsForCountryRelationship.LinkTo {

	private final CountryIdentifier countryIdentifier;

	/**
	 * Constructor.
	 *
	 * @param countryIdentifier countryIdentifier
	 */
	@Inject
	public CountryToRegionsRelationshipImpl(@RequestIdentifier final CountryIdentifier countryIdentifier) {
		this.countryIdentifier = countryIdentifier;
	}

	@Override
	public Observable<RegionsIdentifier> onLinkTo() {
		return Observable.just(RegionsIdentifier.builder()
				.withCountry(countryIdentifier)
				.build());
	}
}

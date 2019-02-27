/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.navigations.relationship;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.lookups.LookupsIdentifier;
import com.elasticpath.rest.definition.navigations.NavigationLookupFormFromLookupsRelationship;
import com.elasticpath.rest.definition.navigations.NavigationLookupFormIdentifier;
import com.elasticpath.rest.definition.navigations.NavigationsIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Implementing NavigationLookupFormFromLookupsRelationship.
 */
public class NavigationLookupFormFromLookupsRelationshipImpl implements NavigationLookupFormFromLookupsRelationship.LinkTo {
	private final LookupsIdentifier lookupsIdentifier;

	/**
	 * Constructor.
	 *
	 * @param lookupsIdentifier lookupsIdentifier
	 */
	@Inject
	public NavigationLookupFormFromLookupsRelationshipImpl(@RequestIdentifier final LookupsIdentifier lookupsIdentifier) {
		this.lookupsIdentifier = lookupsIdentifier;
	}

	@Override
	public Observable<NavigationLookupFormIdentifier> onLinkTo() {
		return Observable.just(NavigationLookupFormIdentifier.builder()
				.withNavigations(NavigationsIdentifier.builder()
						.withScope(lookupsIdentifier.getScope())
						.build())
				.build());
	}
}

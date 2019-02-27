/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.lookups.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.lookups.LookupItemFormFromLookupsRelationship;
import com.elasticpath.rest.definition.lookups.LookupItemFormIdentifier;
import com.elasticpath.rest.definition.lookups.LookupsIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Lookup item form from lookups resource relationship implementation.
 */
public class LookupsToItemLookupFormRelationshipImpl implements LookupItemFormFromLookupsRelationship.LinkTo {


	private final LookupsIdentifier lookupsIdentifier;

	/**
	 * Constructor.
	 *
	 * @param lookupsIdentifier lookupsIdentifier
	 */
	@Inject
	public LookupsToItemLookupFormRelationshipImpl(@RequestIdentifier final LookupsIdentifier lookupsIdentifier) {
		this.lookupsIdentifier = lookupsIdentifier;
	}

	@Override
	public Observable<LookupItemFormIdentifier> onLinkTo() {
		return Observable.just(LookupItemFormIdentifier.builder()
				.withLookups(lookupsIdentifier)
				.build());
	}
}

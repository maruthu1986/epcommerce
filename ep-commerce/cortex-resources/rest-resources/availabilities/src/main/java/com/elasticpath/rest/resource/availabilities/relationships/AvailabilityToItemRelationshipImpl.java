/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.availabilities.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.availabilities.AvailabilityForItemIdentifier;
import com.elasticpath.rest.definition.availabilities.ItemToAvailabilityRelationship;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Availability to item link.
 */
public class AvailabilityToItemRelationshipImpl implements ItemToAvailabilityRelationship.LinkFrom {

	private final AvailabilityForItemIdentifier availabilityForItemIdentifier;

	/**
	 * Constructor.
	 *
	 * @param availabilityForItemIdentifier availabilityForItemIdentifier
	 */
	@Inject
	public AvailabilityToItemRelationshipImpl(@RequestIdentifier final AvailabilityForItemIdentifier availabilityForItemIdentifier) {
		this.availabilityForItemIdentifier = availabilityForItemIdentifier;
	}

	@Override
	public Observable<ItemIdentifier> onLinkFrom() {
		return Observable.just(availabilityForItemIdentifier.getItem());
	}
}

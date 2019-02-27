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
 * Item to availability link.
 */
public class ItemToAvailabilityRelationshipImpl implements ItemToAvailabilityRelationship.LinkTo {

	private final ItemIdentifier itemIdentifier;

	/**
	 * Constructor.
	 *
	 * @param itemIdentifier itemIdentifier
	 */
	@Inject
	public ItemToAvailabilityRelationshipImpl(@RequestIdentifier final ItemIdentifier itemIdentifier) {
		this.itemIdentifier = itemIdentifier;
	}

	@Override
	public Observable<AvailabilityForItemIdentifier> onLinkTo() {
		return Observable.just(AvailabilityForItemIdentifier.builder()
				.withItem(itemIdentifier)
				.build());
	}
}

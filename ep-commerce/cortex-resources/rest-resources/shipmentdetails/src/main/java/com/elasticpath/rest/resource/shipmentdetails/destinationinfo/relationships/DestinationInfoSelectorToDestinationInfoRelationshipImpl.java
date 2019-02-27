/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.destinationinfo.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.shipmentdetails.DestinationInfoIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.DestinationInfoSelectorIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.DestinationInfoSelectorToDestinationInfoRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Adds a destination info link in a selector.
 */
public class DestinationInfoSelectorToDestinationInfoRelationshipImpl implements DestinationInfoSelectorToDestinationInfoRelationship.LinkTo {

	private final DestinationInfoIdentifier destinationInfoIdentifier;

	/**
	 * Constructor.
	 *
	 * @param destinationInfoSelectorIdentifier	identifier
	 */
	@Inject
	public DestinationInfoSelectorToDestinationInfoRelationshipImpl(
			@RequestIdentifier final DestinationInfoSelectorIdentifier destinationInfoSelectorIdentifier) {
		this.destinationInfoIdentifier = destinationInfoSelectorIdentifier.getDestinationInfo();
	}

	@Override
	public Observable<DestinationInfoIdentifier> onLinkTo() {
		return Observable.just(destinationInfoIdentifier);
	}
}

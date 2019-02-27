/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.destinationinfo.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.shipmentdetails.DestinationInfoDestinationInfoSelectorRelationship;
import com.elasticpath.rest.definition.shipmentdetails.DestinationInfoIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.DestinationInfoSelectorIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Adds a selector link in destination info.
 */
public class DestinationInfoDestinationInfoSelectorRelationshipImpl implements DestinationInfoDestinationInfoSelectorRelationship.LinkTo {

	private final DestinationInfoIdentifier destinationInfoIdentifier;

	/**
	 * Constructor.
	 *
	 * @param destinationInfoIdentifier	identifier.
	 */
	@Inject
	public DestinationInfoDestinationInfoSelectorRelationshipImpl(@RequestIdentifier final DestinationInfoIdentifier destinationInfoIdentifier) {
		this.destinationInfoIdentifier = destinationInfoIdentifier;
	}

	@Override
	public Observable<DestinationInfoSelectorIdentifier> onLinkTo() {
		return Observable.just(DestinationInfoSelectorIdentifier.builder()
				.withDestinationInfo(destinationInfoIdentifier)
				.build());
	}
}

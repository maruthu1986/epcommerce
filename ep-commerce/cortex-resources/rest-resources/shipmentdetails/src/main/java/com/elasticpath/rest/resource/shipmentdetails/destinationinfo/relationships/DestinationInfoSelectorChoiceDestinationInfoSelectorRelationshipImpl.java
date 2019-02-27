/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.destinationinfo.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.shipmentdetails.DestinationInfoSelectorChoiceDestinationInfoSelectorRelationship;
import com.elasticpath.rest.definition.shipmentdetails.DestinationInfoSelectorChoiceIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.DestinationInfoSelectorIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Adds a destination info selector link in a choice.
 */
public class DestinationInfoSelectorChoiceDestinationInfoSelectorRelationshipImpl implements
		DestinationInfoSelectorChoiceDestinationInfoSelectorRelationship.LinkTo {

	private final DestinationInfoSelectorChoiceIdentifier destinationInfoSelectorChoiceIdentifier;

	/**
	 * Constructor.
	 *
	 * @param destinationInfoSelectorChoiceIdentifier	identifier
	 */
	@Inject
	public DestinationInfoSelectorChoiceDestinationInfoSelectorRelationshipImpl(
			@RequestIdentifier final DestinationInfoSelectorChoiceIdentifier destinationInfoSelectorChoiceIdentifier) {
		this.destinationInfoSelectorChoiceIdentifier = destinationInfoSelectorChoiceIdentifier;
	}

	@Override
	public Observable<DestinationInfoSelectorIdentifier> onLinkTo() {
		return Observable.just(destinationInfoSelectorChoiceIdentifier.getDestinationInfoSelector());
	}
}

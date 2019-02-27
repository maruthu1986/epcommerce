/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.destinationinfo.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.addresses.AddressIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.DestinationInfoIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.ShippingAddressForDestinationInfoRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Adds an address link in destination info.
 */
public class DestinationInfoToAddressRelationshipImpl implements ShippingAddressForDestinationInfoRelationship.LinkTo {

	private final DestinationInfoIdentifier destinationInfoIdentifier;
	private final LinksRepository<DestinationInfoIdentifier, AddressIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param destinationInfoIdentifier				identifier.
	 * @param repository							destination info service.
	 */
	@Inject
	public DestinationInfoToAddressRelationshipImpl(
			@RequestIdentifier final DestinationInfoIdentifier destinationInfoIdentifier,
			@ResourceRepository final LinksRepository<DestinationInfoIdentifier, AddressIdentifier> repository) {
		this.destinationInfoIdentifier = destinationInfoIdentifier;
		this.repository = repository;
	}

	@Override
	public Observable<AddressIdentifier> onLinkTo() {
		return repository.getElements(destinationInfoIdentifier);
	}
}

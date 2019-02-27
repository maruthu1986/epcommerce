/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.shippingoption.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionInfoForShippingOptionRelationship;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionInfoIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceService;

/**
 * Adds a shipping option link in shipping option info.
 */
public class ShippingOptionInfoToShippingOptionRelationshipImpl implements ShippingOptionInfoForShippingOptionRelationship.LinkTo {

	private final ShippingOptionInfoIdentifier shippingOptionInfoIdentifier;
	private final LinksRepository<ShippingOptionInfoIdentifier, ShippingOptionIdentifier> repository;
	/**
	 * Constructor.
	 *
	 * @param shippingOptionInfoIdentifier	identifier
	 * @param repository					repository
	 */
	@Inject
	public ShippingOptionInfoToShippingOptionRelationshipImpl(
			@RequestIdentifier final ShippingOptionInfoIdentifier shippingOptionInfoIdentifier,
			@ResourceService final LinksRepository<ShippingOptionInfoIdentifier, ShippingOptionIdentifier> repository) {
		this.shippingOptionInfoIdentifier = shippingOptionInfoIdentifier;
		this.repository = repository;
	}

	@Override
	public Observable<ShippingOptionIdentifier> onLinkTo() {
		return repository.getElements(shippingOptionInfoIdentifier);
	}
}

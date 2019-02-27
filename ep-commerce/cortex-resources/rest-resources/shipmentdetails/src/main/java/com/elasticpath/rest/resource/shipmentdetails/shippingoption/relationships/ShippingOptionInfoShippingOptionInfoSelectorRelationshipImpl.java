/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.shippingoption.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionInfoIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionInfoSelectorIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionInfoShippingOptionInfoSelectorRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Adds a shipping option info to selector.
 */
public class ShippingOptionInfoShippingOptionInfoSelectorRelationshipImpl implements
		ShippingOptionInfoShippingOptionInfoSelectorRelationship.LinkTo {

	private final ShippingOptionInfoIdentifier shippingOptionInfoIdentifier;

	/**
	 * Constructor.
	 *
	 * @param shippingOptionInfoIdentifier	identifier
	 */
	@Inject
	public ShippingOptionInfoShippingOptionInfoSelectorRelationshipImpl(
			@RequestIdentifier final ShippingOptionInfoIdentifier shippingOptionInfoIdentifier) {
		this.shippingOptionInfoIdentifier = shippingOptionInfoIdentifier;
	}

	@Override
	public Observable<ShippingOptionInfoSelectorIdentifier> onLinkTo() {
		return Observable.just(ShippingOptionInfoSelectorIdentifier.builder()
				.withShippingOptionInfo(shippingOptionInfoIdentifier)
				.build());
	}
}

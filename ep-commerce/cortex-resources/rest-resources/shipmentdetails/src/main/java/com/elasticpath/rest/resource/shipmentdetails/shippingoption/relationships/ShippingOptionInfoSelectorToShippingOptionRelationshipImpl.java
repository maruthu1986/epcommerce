/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.shippingoption.relationships;

import java.util.Map;
import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionInfoIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionInfoSelectorIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionInfoSelectorToShippingOptionInfoRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.id.IdentifierPart;

/**
 * Adds a shipping option link in shipping option info selector.
 */
public class ShippingOptionInfoSelectorToShippingOptionRelationshipImpl implements ShippingOptionInfoSelectorToShippingOptionInfoRelationship.LinkTo {

	private final ShippingOptionInfoSelectorIdentifier shippingOptionInfoSelectorIdentifier;

	/**
	 * Constructor.
	 *
	 * @param shippingOptionInfoSelectorIdentifier	identifier
	 */
	@Inject
	public ShippingOptionInfoSelectorToShippingOptionRelationshipImpl(
			@RequestIdentifier final ShippingOptionInfoSelectorIdentifier shippingOptionInfoSelectorIdentifier) {
		this.shippingOptionInfoSelectorIdentifier = shippingOptionInfoSelectorIdentifier;
	}

	@Override
	public Observable<ShippingOptionInfoIdentifier> onLinkTo() {
		ShippingOptionInfoIdentifier shippingOptionInfoIdentifier = shippingOptionInfoSelectorIdentifier.getShippingOptionInfo();
		IdentifierPart<Map<String, String>> shipmentDetailsId = shippingOptionInfoIdentifier.getShipmentDetailsId();
		IdentifierPart<String> scope = shippingOptionInfoIdentifier.getScope();
		return Observable.just(ShippingOptionInfoIdentifier.builder()
				.withShipmentDetailsId(shipmentDetailsId)
				.withScope(scope)
				.build());
	}
}

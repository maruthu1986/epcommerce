/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.shippingoption.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionInfoSelectorChoiceIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionInfoSelectorChoiceShippingOptionInfoSelectorRelationship;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionInfoSelectorIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Adds a selector link in choice.
 */
public class ShippingOptionInfoSelectorChoiceShippingOptionInfoSelectorRelationshipImpl implements
		ShippingOptionInfoSelectorChoiceShippingOptionInfoSelectorRelationship.LinkTo {

	private final ShippingOptionInfoSelectorChoiceIdentifier shippingOptionInfoSelectorChoiceIdentifier;

	/**
	 * Constructor.
	 *
	 * @param shippingOptionInfoSelectorChoiceIdentifier	identifier
	 */
	@Inject
	public ShippingOptionInfoSelectorChoiceShippingOptionInfoSelectorRelationshipImpl(
			@RequestIdentifier final ShippingOptionInfoSelectorChoiceIdentifier shippingOptionInfoSelectorChoiceIdentifier) {
		this.shippingOptionInfoSelectorChoiceIdentifier = shippingOptionInfoSelectorChoiceIdentifier;
	}

	@Override
	public Observable<ShippingOptionInfoSelectorIdentifier> onLinkTo() {
		return Observable.just(shippingOptionInfoSelectorChoiceIdentifier.getShippingOptionInfoSelector());
	}
}

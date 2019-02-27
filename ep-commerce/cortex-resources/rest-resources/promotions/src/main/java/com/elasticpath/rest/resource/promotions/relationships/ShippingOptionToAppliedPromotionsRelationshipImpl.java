/*
 *  Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.promotions.AppliedPromotionsForShippingOptionIdentifier;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForShippingOptionRelationship;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Adds a appliedpromotions link in shippingtoption.
 */
public class ShippingOptionToAppliedPromotionsRelationshipImpl implements AppliedPromotionsForShippingOptionRelationship.LinkTo {

	private final ShippingOptionIdentifier shippingOptionIdentifier;

	/**
	 * Constructor.
	 *
	 * @param shippingOptionIdentifier	identifier
	 */
	@Inject
	public ShippingOptionToAppliedPromotionsRelationshipImpl(@RequestIdentifier final ShippingOptionIdentifier
																		 shippingOptionIdentifier) {
		this.shippingOptionIdentifier = shippingOptionIdentifier;
	}

	@Override
	public Observable<AppliedPromotionsForShippingOptionIdentifier> onLinkTo() {
		return Observable.just(AppliedPromotionsForShippingOptionIdentifier.builder()
				.withShippingOption(shippingOptionIdentifier)
				.build());
	}
}

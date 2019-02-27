/*
 *  Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.relationships;

import java.util.Map;
import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.promotions.AppliedPromotionsForShippingOptionIdentifier;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForShippingOptionRelationship;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.id.IdentifierPart;

/**
 * Adds a shippingoption link in promotions.
 */
public class AppliedPromotionsToShippingOptionRelationshipImpl implements AppliedPromotionsForShippingOptionRelationship.LinkFrom {

	private final AppliedPromotionsForShippingOptionIdentifier appliedPromotionsForShippingOptionIdentifier;

	/**
	 * Constructor.
	 *
	 * @param appliedPromotionsForShippingOptionIdentifier	identifier
	 */
	@Inject
	public AppliedPromotionsToShippingOptionRelationshipImpl(@RequestIdentifier final AppliedPromotionsForShippingOptionIdentifier
																	 appliedPromotionsForShippingOptionIdentifier) {
		this.appliedPromotionsForShippingOptionIdentifier = appliedPromotionsForShippingOptionIdentifier;
	}

	@Override
	public Observable<ShippingOptionIdentifier> onLinkFrom() {
		ShippingOptionIdentifier shippingOptionIdentifier = appliedPromotionsForShippingOptionIdentifier.getShippingOption();
		IdentifierPart<String> scope = shippingOptionIdentifier.getScope();
		IdentifierPart<Map<String, String>> shipmentDetailsId = shippingOptionIdentifier.getShipmentDetailsId();
		IdentifierPart<String> shippingOptionId = shippingOptionIdentifier.getShippingOptionId();
		return Observable.just(ShippingOptionIdentifier.builder()
				.withScope(scope)
				.withShipmentDetailsId(shipmentDetailsId)
				.withShippingOptionId(shippingOptionId)
				.build());
	}
}

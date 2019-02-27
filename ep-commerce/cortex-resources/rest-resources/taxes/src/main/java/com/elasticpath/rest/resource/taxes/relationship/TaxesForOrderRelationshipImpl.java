/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.relationship;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.orders.OrderIdentifier;
import com.elasticpath.rest.definition.taxes.OrderTaxIdentifier;
import com.elasticpath.rest.definition.taxes.TaxesForOrderRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Taxes for Order Relationship.
 */
public class TaxesForOrderRelationshipImpl implements TaxesForOrderRelationship.LinkTo {

	private final OrderIdentifier orderIdentifier;

	/**
	 * Constructor.
	 *
	 * @param orderIdentifier order identifier
	 */
	@Inject
	public TaxesForOrderRelationshipImpl(@RequestIdentifier final OrderIdentifier orderIdentifier) {
		this.orderIdentifier = orderIdentifier;
	}

	@Override
	public Observable<OrderTaxIdentifier> onLinkTo() {
		return Observable.just(OrderTaxIdentifier.builder()
				.withOrder(orderIdentifier)
				.build());
	}
}

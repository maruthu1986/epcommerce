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
 * Order to Taxes Relationship.
 */
public class OrderToTaxesRelationshipImpl implements TaxesForOrderRelationship.LinkFrom {

	private final OrderTaxIdentifier orderTaxIdentifier;

	/**
	 * Constructor.
	 *
	 * @param orderTaxIdentifier orderTaxIdentifier
	 */
	@Inject
	public OrderToTaxesRelationshipImpl(@RequestIdentifier final OrderTaxIdentifier orderTaxIdentifier) {
		this.orderTaxIdentifier = orderTaxIdentifier;
	}

	@Override
	public Observable<OrderIdentifier> onLinkFrom() {
		return Observable.just(orderTaxIdentifier.getOrder());
	}
}

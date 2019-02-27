/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.orders.OrderIdentifier;
import com.elasticpath.rest.definition.totals.OrderTotalIdentifier;
import com.elasticpath.rest.definition.totals.TotalForOrderRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Order to order total link.
 */
public class OrderToTotalRelationshipImpl implements TotalForOrderRelationship.LinkTo {

	private final OrderIdentifier orderIdentifier;

	/**
	 * Constructor.
	 *
	 * @param orderIdentifier orderIdentifier
	 */
	@Inject
	public OrderToTotalRelationshipImpl(@RequestIdentifier final OrderIdentifier orderIdentifier) {
		this.orderIdentifier = orderIdentifier;
	}

	@Override
	public Observable<OrderTotalIdentifier> onLinkTo() {
		return Observable.just(OrderTotalIdentifier.builder()
				.withOrder(orderIdentifier)
				.build());
	}
}

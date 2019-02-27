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
 * Order total to order link.
 */
public class TotalToOrderRelationshipImpl implements TotalForOrderRelationship.LinkFrom {

	private final OrderTotalIdentifier orderTotalIdentifier;

	/**
	 * Constructor.
	 *
	 * @param orderTotalIdentifier orderTotalIdentifier
	 */
	@Inject
	public TotalToOrderRelationshipImpl(@RequestIdentifier final OrderTotalIdentifier orderTotalIdentifier) {
		this.orderTotalIdentifier = orderTotalIdentifier;
	}

	@Override
	public Observable<OrderIdentifier> onLinkFrom() {
		return Observable.just(orderTotalIdentifier.getOrder());
	}
}

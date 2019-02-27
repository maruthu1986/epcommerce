/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.order.relationship;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.orders.CartToOrderRelationship;
import com.elasticpath.rest.definition.orders.OrderIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Cart to order link.
 */
public class CartToOrderRelationshipImpl implements CartToOrderRelationship.LinkTo {

	private final CartIdentifier cartIdentifier;

	private final LinksRepository<CartIdentifier, OrderIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param cartIdentifier cartIdentifier
	 * @param repository     repository
	 */
	@Inject
	public CartToOrderRelationshipImpl(@RequestIdentifier final CartIdentifier cartIdentifier,
									   @ResourceRepository final LinksRepository<CartIdentifier, OrderIdentifier> repository) {
		this.cartIdentifier = cartIdentifier;
		this.repository = repository;
	}

	@Override
	public Observable<OrderIdentifier> onLinkTo() {
		return repository.getElements(cartIdentifier);
	}
}

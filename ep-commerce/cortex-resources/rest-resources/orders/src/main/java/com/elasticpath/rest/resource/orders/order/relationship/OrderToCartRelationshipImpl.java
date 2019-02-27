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
 * Order to cart link.
 */
public class OrderToCartRelationshipImpl implements CartToOrderRelationship.LinkFrom {

	private final OrderIdentifier orderIdentifier;

	private final LinksRepository<OrderIdentifier, CartIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param orderIdentifier orderIdentifier
	 * @param repository      repository
	 */
	@Inject
	public OrderToCartRelationshipImpl(@RequestIdentifier final OrderIdentifier orderIdentifier,
									   @ResourceRepository final LinksRepository<OrderIdentifier, CartIdentifier> repository) {
		this.orderIdentifier = orderIdentifier;
		this.repository = repository;
	}

	@Override
	public Observable<CartIdentifier> onLinkFrom() {
		return repository.getElements(orderIdentifier);
	}
}

/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.discounts.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.discounts.DiscountForCartIdentifier;
import com.elasticpath.rest.definition.discounts.DiscountToCartRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Discount from Cart link.
 */
public class DiscountFromCartRelationshipImpl implements DiscountToCartRelationship.LinkFrom {

	private final CartIdentifier cartIdentifier;

	/**
	 * Constructor.
	 *
	 * @param cartIdentifier	cartIdentifier
	 */
	@Inject
	public DiscountFromCartRelationshipImpl(@RequestIdentifier final CartIdentifier cartIdentifier) {
		this.cartIdentifier = cartIdentifier;
	}


	@Override
	public Observable<DiscountForCartIdentifier> onLinkFrom() {
		return Observable.just(DiscountForCartIdentifier.builder()
				.withCart(cartIdentifier)
				.build());
	}
}

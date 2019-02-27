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
 * Discount to Cart link.
 */
public class DiscountToCartRelationshipImpl implements DiscountToCartRelationship.LinkTo {

	private final DiscountForCartIdentifier discountForCartIdentifier;

	/**
	 * Constructor.
	 *
	 * @param discountForCartIdentifier	discountForCartIdentifier
	 */
	@Inject
	public DiscountToCartRelationshipImpl(@RequestIdentifier final DiscountForCartIdentifier discountForCartIdentifier) {
		this.discountForCartIdentifier = discountForCartIdentifier;
	}


	@Override
	public Observable<CartIdentifier> onLinkTo() {
		return Observable.just(discountForCartIdentifier.getCart());
	}
}

/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.wishlists.lineitems.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.definition.wishlists.MoveToCartFormIdentifier;
import com.elasticpath.rest.definition.wishlists.MoveToCartFormResource;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemIdentifier;
import com.elasticpath.rest.form.SubmitResult;
import com.elasticpath.rest.helix.data.annotation.RequestForm;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceService;
import com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.MoveToCartService;

/**
 * Submit move to cart form.
 */
public class SubmitMoveToCartFormPrototype implements MoveToCartFormResource.SubmitWithResult {

	private final WishlistLineItemIdentifier wishlistLineItemIdentifier;

	private final LineItemEntity lineItemEntity;
	private final MoveToCartService moveToCartService;

	/**
	 * Constructor.
	 *
	 * @param moveToCartFormIdentifier wishlistLineItemIdentifier
	 * @param lineItemEntity     lineItemEntity
	 * @param moveToCartService        moveToCartService
	 */
	@Inject
	public SubmitMoveToCartFormPrototype(@RequestIdentifier final MoveToCartFormIdentifier moveToCartFormIdentifier,
										 @RequestForm final LineItemEntity lineItemEntity,
										 @ResourceService final MoveToCartService moveToCartService) {
		this.wishlistLineItemIdentifier = moveToCartFormIdentifier.getWishlistLineItem();
		this.lineItemEntity = lineItemEntity;
		this.moveToCartService = moveToCartService;
	}

	@Override
	public Single<SubmitResult<LineItemIdentifier>> onSubmitWithResult() {
		return moveToCartService.move(wishlistLineItemIdentifier, lineItemEntity);
	}
}

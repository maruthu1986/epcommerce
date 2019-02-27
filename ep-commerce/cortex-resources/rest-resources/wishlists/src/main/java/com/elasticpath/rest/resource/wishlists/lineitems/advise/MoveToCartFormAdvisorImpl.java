/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.wishlists.lineitems.advise;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.advise.Message;
import com.elasticpath.rest.definition.wishlists.MoveToCartFormAdvisor;
import com.elasticpath.rest.definition.wishlists.MoveToCartFormIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceService;
import com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.ItemValidationService;

/**
 * Advisor for move to cart.
 */
public class MoveToCartFormAdvisorImpl implements MoveToCartFormAdvisor.FormAdvisor {

	private final WishlistLineItemIdentifier wishlistLineItemIdentifier;

	private final ItemValidationService itemValidationService;

	/**
	 * Constructor.
	 *
	 * @param moveToCartFormIdentifier  moveToCartFormIdentifier
	 * @param itemValidationService itemValidationService
	 */
	@Inject
	public MoveToCartFormAdvisorImpl(@RequestIdentifier final MoveToCartFormIdentifier moveToCartFormIdentifier,
									 @ResourceService final ItemValidationService itemValidationService) {
		this.wishlistLineItemIdentifier = moveToCartFormIdentifier.getWishlistLineItem();
		this.itemValidationService = itemValidationService;
	}

	@Override
	public Observable<Message> onAdvise() {
		return itemValidationService.isItemPurchasable(wishlistLineItemIdentifier);
	}
}

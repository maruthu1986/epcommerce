/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.services;

import io.reactivex.Single;
import io.reactivex.functions.Function;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemIdentifier;
import com.elasticpath.rest.form.SubmitResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.MoveToWishlistService;
import com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.WishlistRepository;

/**
 * Move to favorites service.
 */
@Component
public class MoveToWishlistServiceImpl implements MoveToWishlistService {

	private ShoppingCartRepository shoppingCartRepository;
	private WishlistRepository wishlistRepository;

	private static final Logger LOG = LoggerFactory.getLogger(MoveToWishlistServiceImpl.class);

	@Override
	public Single<SubmitResult<WishlistLineItemIdentifier>> move(final LineItemIdentifier lineItemIdentifier) {

		String cartLineItemId = lineItemIdentifier.getLineItemId().getValue();

		CartIdentifier cartIdentifier = lineItemIdentifier.getLineItems().getCart();
		String cartId = cartIdentifier.getCartId().getValue();
		String scope = cartIdentifier.getScope().getValue();

		LOG.trace("Moving line id {} from cart {} to favorites", cartLineItemId, cartId);

		return wishlistRepository.getDefaultWishlistId(scope)
				.flatMap(toSubmitResult(cartLineItemId, scope));
	}

	/**
	 * Move item to favorites.
	 *
	 * @param cartLineItemId line item id
	 * @param scope          scope
	 * @return the function
	 */
	protected Function<String, Single<SubmitResult<WishlistLineItemIdentifier>>> toSubmitResult(final String cartLineItemId, final String scope) {
		return wishlistId -> shoppingCartRepository.getDefaultShoppingCart()
				.flatMap(cart -> shoppingCartRepository.moveItemToWishlist(cart, cartLineItemId))
				.flatMap(addToWishlistResult -> wishlistRepository.buildSubmitResult(scope, wishlistId, addToWishlistResult));
	}

	@Reference
	public void setShoppingCartRepository(final ShoppingCartRepository shoppingCartRepository) {
		this.shoppingCartRepository = shoppingCartRepository;
	}

	@Reference
	public void setWishlistRepository(final WishlistRepository wishlistRepository) {
		this.wishlistRepository = wishlistRepository;
	}

}

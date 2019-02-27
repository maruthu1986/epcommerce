/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.services;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.rest.advise.Message;
import com.elasticpath.rest.definition.wishlists.WishlistIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.AddToCartAdvisorService;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.ItemValidationService;
import com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.WishlistRepository;

/**
 * Item validation service.
 */
@Component
public class ItemValidationServiceImpl implements ItemValidationService {

	private WishlistRepository wishlistRepository;
	private AddToCartAdvisorService addToCartAdvisorService;
	private ShoppingCartRepository shoppingCartRepository;

	@Override
	public Observable<Message> isItemPurchasable(final WishlistLineItemIdentifier wishlistLineItemIdentifier) {
		WishlistIdentifier wishlistIdentifier = wishlistLineItemIdentifier.getWishlistLineItems().getWishlist();
		String lineItemGuid = wishlistLineItemIdentifier.getLineItemId().getValue();
		String wishlistId = wishlistIdentifier.getWishlistId().getValue();

		return shoppingCartRepository.getDefaultShoppingCartGuid()
				.flatMapObservable(cartGuid -> wishlistRepository.getWishlist(wishlistId)
						.flatMapObservable(toMessages(cartGuid, lineItemGuid)));


	}

	/**
	 * Get structured advise messages, if any.
	 *
	 * @param  cartId the cart id.
	 * @param lineItemGuid the line item guid.
	 * @return the function
	 */
	protected Function<WishList, Observable<Message>> toMessages(final String cartId, final String lineItemGuid) {
		return wishList -> wishlistRepository.getProductSku(wishList, lineItemGuid)
				.flatMapObservable(productSku -> addToCartAdvisorService.validateItemPurchasable(wishList.getStoreCode(), cartId, productSku, null));
	}

	@Reference
	public void setWishlistRepository(final WishlistRepository wishlistRepository) {
		this.wishlistRepository = wishlistRepository;
	}

	@Reference
	public void setShoppingCartRepository(final ShoppingCartRepository shoppingCartRepository) {
		this.shoppingCartRepository = shoppingCartRepository;
	}

	@Reference
	public void setAddToCartAdvisorService(final AddToCartAdvisorService addToCartAdvisorService) {
		this.addToCartAdvisorService = addToCartAdvisorService;
	}
}

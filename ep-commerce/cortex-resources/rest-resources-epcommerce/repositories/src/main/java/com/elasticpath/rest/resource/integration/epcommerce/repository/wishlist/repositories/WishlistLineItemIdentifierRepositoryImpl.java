/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.repositories;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.WishlistIdentifierUtil.buildWishlistLineItemIdentifier;

import io.reactivex.Completable;
import io.reactivex.Observable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.wishlists.WishlistIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.WishlistRepository;

/**
 * Repository for line items in a favorites.
 *
 * @param <I>  the identifier type
 * @param <LI> the linked identifier type
 */
@Component
public class WishlistLineItemIdentifierRepositoryImpl<I extends WishlistIdentifier, LI extends WishlistLineItemIdentifier>
		implements LinksRepository<WishlistIdentifier, WishlistLineItemIdentifier> {

	private WishlistRepository wishlistRepository;

	@Override
	public Observable<WishlistLineItemIdentifier> getElements(final WishlistIdentifier wishlistIdentifier) {
		String wishlistId = wishlistIdentifier.getWishlistId().getValue();
		String scope = wishlistIdentifier.getWishlists().getScope().getValue();

		return wishlistRepository.getWishlist(wishlistId)
				.flatMapObservable(wishList -> Observable.fromIterable(wishList.getAllItems()))
				.map(shoppingItem -> buildWishlistLineItemIdentifier(scope, wishlistId, shoppingItem.getGuid()));
	}

	@Override
	public Completable deleteAll(final WishlistIdentifier wishlistIdentifier) {
		String wishlistId = wishlistIdentifier.getWishlistId().getValue();
		return wishlistRepository.removeAllItemsFromWishlist(wishlistId);
	}

	@Reference
	public void setWishlistRepository(final WishlistRepository wishlistRepository) {
		this.wishlistRepository = wishlistRepository;
	}

}

/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.repositories;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.WishlistIdentifierUtil.buildWishlistIdentifier;

import io.reactivex.Observable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistIdentifier;
import com.elasticpath.rest.id.transform.IdentifierTransformerProvider;
import com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.WishlistRepository;

/**
 * Wishlists for item membership repository.
 *
 * @param <I>  the identifier type
 * @param <LI> the linked identifier type
 */
@Component
public class WishlistsForItemRepositoryImpl<I extends ItemIdentifier, LI extends WishlistIdentifier>
		implements LinksRepository<ItemIdentifier, WishlistIdentifier> {

	private WishlistRepository wishlistRepository;

	private IdentifierTransformerProvider identifierTransformerProvider;

	@Override
	public Observable<WishlistIdentifier> getElements(final ItemIdentifier itemIdentifier) {
		//Deep down item repository expects an encoded id
		String encodedItemId = identifierTransformerProvider.forUriPart(ItemIdentifier.ITEM_ID).identifierToUri(itemIdentifier.getItemId());
		return wishlistRepository.findWishlistsContainingItem(encodedItemId)
				.map(wishList -> buildWishlistIdentifier(wishList.getStoreCode(), wishList.getGuid()))
				.toObservable();
	}

	@Reference
	public void setWishlistRepository(final WishlistRepository wishlistRepository) {
		this.wishlistRepository = wishlistRepository;
	}

	@Reference
	public void setIdentifierTransformerProvider(final IdentifierTransformerProvider identifierTransformerProvider) {
		this.identifierTransformerProvider = identifierTransformerProvider;
	}
}

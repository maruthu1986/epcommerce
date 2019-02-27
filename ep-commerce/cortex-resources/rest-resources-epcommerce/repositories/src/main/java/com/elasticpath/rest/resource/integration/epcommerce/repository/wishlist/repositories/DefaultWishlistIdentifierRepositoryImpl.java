/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.repositories;

import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.repository.AliasRepository;
import com.elasticpath.rest.definition.wishlists.DefaultWishlistIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistsIdentifier;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.WishlistRepository;

/**
 * Repository for Default Wishlist Identifiers.
 *
 * @param <AI>  the Alias identifier type
 * @param <I> the identifier type
 */
@Component
public class DefaultWishlistIdentifierRepositoryImpl<AI extends DefaultWishlistIdentifier, I extends WishlistIdentifier>
		implements AliasRepository<DefaultWishlistIdentifier, WishlistIdentifier> {

	private WishlistRepository wishlistRepository;

	@Override
	public Single<WishlistIdentifier> resolve(final DefaultWishlistIdentifier defaultWishlistIdentifier) {

		IdentifierPart<String> scope = defaultWishlistIdentifier.getWishlists().getScope();

		return wishlistRepository.getDefaultWishlistId(scope.getValue())
				.map(defaultId -> WishlistIdentifier.builder()
						.withWishlistId(StringIdentifier.of(defaultId))
						.withWishlists(WishlistsIdentifier.builder()
								.withScope(scope)
								.build())
						.build());
	}

	@Reference
	public void setWishlistRepository(final WishlistRepository wishlistRepository) {
		this.wishlistRepository = wishlistRepository;
	}
}



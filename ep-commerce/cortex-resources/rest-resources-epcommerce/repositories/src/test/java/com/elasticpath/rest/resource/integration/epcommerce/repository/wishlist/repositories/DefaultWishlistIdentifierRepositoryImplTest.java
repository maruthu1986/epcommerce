/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.repositories;

import static org.mockito.Mockito.when;

import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.definition.wishlists.DefaultWishlistIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistsIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.impl.WishlistRepositoryImpl;

/**
 * Test for {@link DefaultWishlistIdentifierRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class DefaultWishlistIdentifierRepositoryImplTest {


    private static final String SCOPE = "SCOPE";
    private static final String GUID = "GUID";

    @Mock
    private WishlistRepositoryImpl wishlistRepository;

    @InjectMocks
    private DefaultWishlistIdentifierRepositoryImpl<DefaultWishlistIdentifier, WishlistIdentifier>
            defaultWishlistIdentifierRepository;

    @Before
    public void initialize() {
        when(wishlistRepository.getDefaultWishlistId(SCOPE)).thenReturn(Single.just(GUID));
        defaultWishlistIdentifierRepository.setWishlistRepository(wishlistRepository);
    }

    @Test
    public void resolveProducesWishlistIdentifier() {

        DefaultWishlistIdentifier defaultWishlistIdentifier = DefaultWishlistIdentifier.builder()
                .withWishlists(getWishlistsIdentifier())
                .build();

        defaultWishlistIdentifierRepository.resolve(defaultWishlistIdentifier)
                .test()
                .assertNoErrors()
                .assertValue(wishlistIdentifier ->
						wishlistIdentifier.getWishlistId().getValue().equals(GUID))
                .assertValue(wishlistIdentifier ->
                        wishlistIdentifier.getWishlists().equals(getWishlistsIdentifier()));
    }

    private WishlistsIdentifier getWishlistsIdentifier() {
        return WishlistsIdentifier.builder()
                .withScope(StringIdentifier.of(SCOPE))
                .build();
    }

}

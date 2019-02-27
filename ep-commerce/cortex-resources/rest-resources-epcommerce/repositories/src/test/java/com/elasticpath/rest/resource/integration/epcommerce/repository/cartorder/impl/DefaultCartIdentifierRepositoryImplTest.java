/**
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.reactivex.Single;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.carts.DefaultCartIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.repositories.DefaultCartIdentifierRepositoryImpl;

/**
 * Test for {@link DefaultCartIdentifierRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class DefaultCartIdentifierRepositoryImplTest {

    private static final String SCOPE = "SCOPE";
    private static final String GUID = "GUID";

    @Mock
    private ShoppingCartRepositoryImpl shoppingCartRepository;

    @InjectMocks
    private DefaultCartIdentifierRepositoryImpl<DefaultCartIdentifier, CartIdentifier> defaultCartRepository;

    @Before
    public void initialize() {
        defaultCartRepository.setShoppingCartRepository(shoppingCartRepository);
    }

    @Test
    public void resolveProducesCartIdentifier() {
        when(shoppingCartRepository.getDefaultShoppingCartGuid()).thenReturn(Single.just(GUID));

        DefaultCartIdentifier defaultCartIdentifier = DefaultCartIdentifier.builder()
                .withScope(StringIdentifier.of(SCOPE))
                .build();

        defaultCartRepository.resolve(defaultCartIdentifier)
                .test()
                .assertNoErrors()
                .assertValue(cartIdentifier -> cartIdentifier.getCartId().getValue().equals(GUID))
                .assertValue(cartIdentifier -> cartIdentifier.getScope().getValue().equals(SCOPE));
    }

    @Test
    public void resolveProducesCartIdentifierWhenCartGuidNotFound() {
        when(shoppingCartRepository.getDefaultShoppingCartGuid()).thenReturn(Single.error(new ResourceNotFoundException("Cart not found")));

        ShoppingCart mockCart = mock(ShoppingCart.class);

        when(shoppingCartRepository.getDefaultShoppingCart()).thenReturn(Single.just(mockCart));
        when(mockCart.getGuid()).thenReturn(GUID);

        DefaultCartIdentifier defaultCartIdentifier = DefaultCartIdentifier.builder()
                .withScope(StringIdentifier.of(SCOPE))
                .build();

        defaultCartRepository.resolve(defaultCartIdentifier)
                .test()
                .assertNoErrors()
                .assertValue(cartIdentifier -> cartIdentifier.getCartId().getValue().equals(GUID))
                .assertValue(cartIdentifier -> cartIdentifier.getScope().getValue().equals(SCOPE));
    }


}

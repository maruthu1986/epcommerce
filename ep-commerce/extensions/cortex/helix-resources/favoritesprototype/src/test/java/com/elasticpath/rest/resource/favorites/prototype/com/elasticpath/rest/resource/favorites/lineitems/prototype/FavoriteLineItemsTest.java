package com.elasticpath.rest.resource.favorites.prototype.com.elasticpath.rest.resource.favorites.lineitems.prototype;

import com.elasticpath.extensions.rest.resource.integration.epcommerce.repository.favorites.MoveToCartService;
import com.elasticpath.extensions.rest.resource.integration.epcommerce.repository.favorites.repositories.FavoriteLineItemIdentifierRepositoryImpl;
import com.elasticpath.extensions.rest.resource.integration.epcommerce.repository.impl.FavLineItemEntityRepositoryImpl;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.favorites.FavoriteLineItemIdentifier;
import com.elasticpath.rest.definition.favorites.FavoriteLineItemsIdentifier;
import com.elasticpath.rest.definition.favorites.MoveToCartFormIdentifier;
import com.elasticpath.rest.resource.favorites.lineitems.prototypes.DeleteLineItemPrototype;
import com.elasticpath.rest.resource.favorites.lineitems.prototypes.DeleteLineItemsPrototype;
import com.elasticpath.rest.resource.favorites.lineitems.prototypes.ReadLineItemPrototype;
import com.elasticpath.rest.resource.favorites.lineitems.prototypes.ReadLineItemsPrototype;
import com.elasticpath.rest.resource.favorites.lineitems.prototypes.SubmitMoveToCartFormPrototype;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FavoriteLineItemsTest {

    @InjectMocks
    private DeleteLineItemPrototype deleteLineItemPrototype;

    @InjectMocks
    private DeleteLineItemsPrototype deleteLineItemsPrototype;

    @InjectMocks
    private ReadLineItemPrototype readLineItemPrototype;

    @InjectMocks
    private ReadLineItemsPrototype readLineItemsPrototype;

    private SubmitMoveToCartFormPrototype submitMoveToCartFormPrototype;

    @Mock
    @SuppressWarnings("rawType")
    private FavLineItemEntityRepositoryImpl favLineItemEntityRepository;

    @Mock
    private FavoriteLineItemIdentifierRepositoryImpl favoriteLineItemIdentifierRepository;

    @Mock
    private FavoriteLineItemIdentifier favoriteLineItemIdentifier;

    @Mock
    private FavoriteLineItemsIdentifier favoriteLineItemsIdentifier;

    @Mock
    private MoveToCartService moveToCartService;

    @Mock
    private LineItemEntity lineItemEntity;

    @Test
    public void shouldDeleteLineItem() {
        deleteLineItemPrototype.onDelete();
        verify(favLineItemEntityRepository).delete(favoriteLineItemIdentifier);
    }

    @Test
    public void shouldDeleteLineItems() {
        deleteLineItemsPrototype.onDelete();
        verify(favoriteLineItemIdentifierRepository).deleteAll(favoriteLineItemsIdentifier.getFavorite());
    }

    @Test
    public void shouldReturnLineItem() {
        readLineItemPrototype.onRead();
        verify(favLineItemEntityRepository).findOne(favoriteLineItemIdentifier);
    }

    @Test
    public void shouldReturnLineItems() {
        readLineItemsPrototype.onRead();
        verify(favoriteLineItemIdentifierRepository).getElements(favoriteLineItemsIdentifier.getFavorite());
    }

    @Mock
    private MoveToCartFormIdentifier moveToCartFormIdentifier;

    @Test
    public void shouldSubmitToCart() {
        submitMoveToCartFormPrototype = new SubmitMoveToCartFormPrototype(moveToCartFormIdentifier, lineItemEntity, moveToCartService);
        submitMoveToCartFormPrototype.onSubmitWithResult();
        verify(moveToCartService).move(moveToCartFormIdentifier.getFavoriteLineItem(), lineItemEntity);
    }

}

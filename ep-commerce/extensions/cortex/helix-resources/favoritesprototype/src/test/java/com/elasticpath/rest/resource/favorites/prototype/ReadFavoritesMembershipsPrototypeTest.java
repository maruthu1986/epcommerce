package com.elasticpath.rest.resource.favorites.prototype;

import com.elasticpath.extensions.rest.resource.integration.epcommerce.repository.favorites.repositories.FavoritesForItemRepositoryImpl;
import com.elasticpath.rest.definition.favorites.ReadFavoriteMembershipsIdentifier;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ReadFavoritesMembershipsPrototypeTest {

    @InjectMocks
    private ReadFavoritesMembershipsPrototype readFavoritesMembershipsPrototype;

    @Mock
    private ReadFavoriteMembershipsIdentifier itemIdentifier;

    @Mock
    @SuppressWarnings("rawTypes")
    private FavoritesForItemRepositoryImpl favoritesForItemRepository;

    @Test
    public void shouldReturnFavorite() {
        readFavoritesMembershipsPrototype = new ReadFavoritesMembershipsPrototype(itemIdentifier, favoritesForItemRepository);
        readFavoritesMembershipsPrototype.onRead();

        verify(favoritesForItemRepository).getElements(itemIdentifier.getItem());
    }
}

package com.elasticpath.rest.resource.favorites.prototype;

import com.elasticpath.extensions.rest.resource.integration.epcommerce.repository.favorites.repositories.DefaultFavoriteIdentifierRepositoryImpl;
import com.elasticpath.extensions.rest.resource.integration.epcommerce.repository.impl.FavEntityRepositoryImpl;
import com.elasticpath.rest.definition.favorites.DefaultFavoriteIdentifier;
import com.elasticpath.rest.definition.favorites.FavoriteIdentifier;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ReadFavoritesPrototypeTest {

    @InjectMocks
    private ReadDefaultFavoritePrototype readDefaultFavoritePrototype;

    @Mock
    @SuppressWarnings("rawtypes")
    private FavEntityRepositoryImpl favEntityRepository;

    @Mock
    @SuppressWarnings("rawTypes")
    private DefaultFavoriteIdentifierRepositoryImpl defaultFavoriteIdentifierRepository;

    @Mock
    private FavoriteIdentifier favoriteIdentifier;

    @Mock
    private DefaultFavoriteIdentifier defaultFavoriteIdentifier;

    @InjectMocks
    private ReadFavoritePrototype readFavoritePrototype;

    @Test
    public void shouldReturnFavorite() {

        readFavoritePrototype.onRead();

        verify(favEntityRepository).findOne(favoriteIdentifier);
    }

    @Test
    public void shouldReturnDefaultFavorite() {

        readDefaultFavoritePrototype.onRead();

        verify(defaultFavoriteIdentifierRepository).resolve(defaultFavoriteIdentifier);
    }
}

package com.elasticpath.rest.resource.favorites.prototype;

import com.elasticpath.extensions.rest.resource.integration.epcommerce.repository.favorites.MoveToFavoriteService;
import com.elasticpath.rest.definition.favorites.MoveToFavoriteFormIdentifier;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SubmitMoveToFavoriteFormPrototypeTest {

    private SubmitMoveToFavoriteFormPrototype submitMoveToFavoriteFormPrototype;
    @Mock
    private MoveToFavoriteFormIdentifier lineItemIdentifier;

    @Mock
    private MoveToFavoriteService moveToFavoriteService;

    @Test
    public void shouldMoveToFavorites() {
        submitMoveToFavoriteFormPrototype = new SubmitMoveToFavoriteFormPrototype(lineItemIdentifier,moveToFavoriteService);
        submitMoveToFavoriteFormPrototype.onSubmitWithResult();
        verify(moveToFavoriteService).move(lineItemIdentifier.getLineItem());
    }
}

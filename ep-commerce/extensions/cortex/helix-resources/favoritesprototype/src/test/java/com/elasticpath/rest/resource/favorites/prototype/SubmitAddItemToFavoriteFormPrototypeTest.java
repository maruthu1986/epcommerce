package com.elasticpath.rest.resource.favorites.prototype;

import com.elasticpath.extensions.rest.resource.integration.epcommerce.repository.impl.FavLineItemEntityRepositoryImpl;
import com.elasticpath.rest.definition.favorites.FavoriteLineItemEntity;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.CompositeIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Default;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SubmitAddItemToFavoriteFormPrototypeTest {

    private SubmitAddItemToFavoriteFormPrototype submitAddItemToFavoriteFormPrototype;
    private final IdentifierPart<Map<String, String>> identifierTransformer = CompositeIdentifier.of("S", "pillow_sku");
    @Mock
    @SuppressWarnings("rawTypes")
    private FavLineItemEntityRepositoryImpl favLineItemEntityRepository;

    @Test
    public void shouldAddItemsToFavorites() {

        submitAddItemToFavoriteFormPrototype = new SubmitAddItemToFavoriteFormPrototype(identifierTransformer, StringIdentifier.of("scope"), favLineItemEntityRepository);
        submitAddItemToFavoriteFormPrototype.onSubmitWithResult();
        FavoriteLineItemEntity lineItemEntity = FavoriteLineItemEntity.builder()
                .withItemId("pillow_sku")
                .withFavoriteId(Default.URI_PART)
                .build();

        verify(favLineItemEntityRepository).submit(lineItemEntity, StringIdentifier.of("scope"));
    }
}


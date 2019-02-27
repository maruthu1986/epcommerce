/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.favorites.prototype;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.favorites.AddItemToFavoriteFormResource;
import com.elasticpath.rest.definition.favorites.FavoriteLineItemEntity;
import com.elasticpath.rest.definition.favorites.FavoriteLineItemIdentifier;
import com.elasticpath.rest.definition.favorites.FavoritesIdentifier;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.form.SubmitResult;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;
import com.elasticpath.rest.helix.data.annotation.UriPart;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Default;
import io.reactivex.Single;

import javax.inject.Inject;
import java.util.Map;

/**
 * Submit add item to favorites form.
 */
public class SubmitAddItemToFavoriteFormPrototype implements AddItemToFavoriteFormResource.SubmitWithResult {
    private final String itemId;

    private final IdentifierPart<String> scope;

    private final Repository<FavoriteLineItemEntity, FavoriteLineItemIdentifier> repository;

    /**
     * Constructor.
     *
     * @param itemId itemId
     * @param scope  scope
     */
    @Inject
    public SubmitAddItemToFavoriteFormPrototype(@UriPart(ItemIdentifier.ITEM_ID) final IdentifierPart<Map<String, String>> itemId,
                                                @UriPart(FavoritesIdentifier.SCOPE) final IdentifierPart<String> scope,
                                                @ResourceRepository final Repository<FavoriteLineItemEntity, FavoriteLineItemIdentifier> repository
    ) {
        this.itemId = itemId.getValue().get("S");
        this.scope = scope;
        this.repository = repository;
    }

    @Override
    public Single<SubmitResult<FavoriteLineItemIdentifier>> onSubmitWithResult() {
        final FavoriteLineItemEntity lineItemEntity = FavoriteLineItemEntity.builder()
                .withItemId(itemId)
                .withFavoriteId(Default.URI_PART)
                .build();
        return repository.submit(lineItemEntity, scope);
    }
}

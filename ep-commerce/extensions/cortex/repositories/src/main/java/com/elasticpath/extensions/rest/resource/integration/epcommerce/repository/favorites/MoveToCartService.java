/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.extensions.rest.resource.integration.epcommerce.repository.favorites;

import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.definition.favorites.FavoriteLineItemIdentifier;
import com.elasticpath.rest.form.SubmitResult;
import io.reactivex.Single;

/**
 * Service interface for moving an item from favorites to cart.
 */
public interface MoveToCartService {

    /**
     * Move an item from favorites to cart.
     *
     * @param favoriteLineItemIdentifier the favorites line item identifier
     * @param lineItemEntity             the line item entity
     * @return the line item identifier
     */
    Single<SubmitResult<LineItemIdentifier>> move(FavoriteLineItemIdentifier favoriteLineItemIdentifier, LineItemEntity lineItemEntity);
}

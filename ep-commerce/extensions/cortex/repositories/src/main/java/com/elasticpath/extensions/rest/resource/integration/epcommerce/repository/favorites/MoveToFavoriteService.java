/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.extensions.rest.resource.integration.epcommerce.repository.favorites;

import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.definition.favorites.FavoriteLineItemIdentifier;
import com.elasticpath.rest.form.SubmitResult;
import io.reactivex.Single;

/**
 * Service interface for moving an item from cart to favorites.
 */
public interface MoveToFavoriteService {

    /**
     * Move an item from cart to favorites.
     *
     * @param lineItemIdentifier the line item identifier
     * @return the favorites line item identifier
     */
    Single<SubmitResult<FavoriteLineItemIdentifier>> move(LineItemIdentifier lineItemIdentifier);
}

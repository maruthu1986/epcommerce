/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.extensions.rest.resource.integration.epcommerce.repository.favorites;

import com.elasticpath.rest.advise.Message;
import com.elasticpath.rest.definition.favorites.FavoriteLineItemIdentifier;
import io.reactivex.Observable;

/**
 * Item Validation service for favorites.
 */
public interface ItemValidationService {

    /**
     * Check if the item is purchasable.
     *
     * @param favoriteLineItemIdentifier favoriteLineItemIdentifier
     * @return the execution result with the boolean result
     */
    Observable<Message> isItemPurchasable(FavoriteLineItemIdentifier favoriteLineItemIdentifier);

}

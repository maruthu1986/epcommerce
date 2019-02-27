/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.favorites.prototype;

import com.elasticpath.rest.definition.favorites.AddItemToFavoriteFormEntity;
import com.elasticpath.rest.definition.favorites.AddItemToFavoriteFormResource;
import io.reactivex.Single;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Add item to favorites form.
 */
public class ReadAddItemToFavoriteFormPrototype implements AddItemToFavoriteFormResource.Read {
    private static final Logger LOG = LoggerFactory.getLogger(ReadAddItemToFavoriteFormPrototype.class);

    @Override
    public Single<AddItemToFavoriteFormEntity> onRead() {
        LOG.info("ReadAddItemToFavoriteFormPrototype onRead()");
        return Single.just(AddItemToFavoriteFormEntity.builder()
                .build());
    }
}

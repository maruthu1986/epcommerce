/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.favorites.prototype;

import com.elasticpath.rest.definition.favorites.MoveToFavoriteFormEntity;
import com.elasticpath.rest.definition.favorites.MoveToFavoriteFormResource;
import io.reactivex.Single;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Move to favorites form.
 */
public class ReadMoveToFavoriteFormPrototype implements MoveToFavoriteFormResource.Read {
    private static final Logger LOG = LoggerFactory.getLogger(ReadMoveToFavoriteFormPrototype.class);

    @Override
    public Single<MoveToFavoriteFormEntity> onRead() {
        LOG.info("ReadMoveToFavoriteFormPrototype onRead()");
        return Single.just(MoveToFavoriteFormEntity.builder()
                .build());
    }
}

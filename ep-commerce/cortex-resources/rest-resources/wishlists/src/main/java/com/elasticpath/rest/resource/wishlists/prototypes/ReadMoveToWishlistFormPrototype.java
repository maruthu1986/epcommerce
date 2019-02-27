/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.wishlists.prototypes;

import io.reactivex.Single;

import com.elasticpath.rest.definition.wishlists.MoveToWishlistFormEntity;
import com.elasticpath.rest.definition.wishlists.MoveToWishlistFormResource;

/**
 * Move to favorites form.
 */
public class ReadMoveToWishlistFormPrototype implements MoveToWishlistFormResource.Read {

	@Override
	public Single<MoveToWishlistFormEntity> onRead() {
		return Single.just(MoveToWishlistFormEntity.builder()
				.build());
	}
}

/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.prototypes;

import io.reactivex.Single;

import com.elasticpath.rest.definition.purchases.CreatePurchaseFormResource;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;

/**
 * Create Purchase Form prototype for Read operation.
 */
public class ReadCreatePurchaseFormPrototype implements CreatePurchaseFormResource.Read {

	@Override
	public Single<PurchaseEntity> onRead() {
		return Single.just(PurchaseEntity.builder()
				.build());
	}
}

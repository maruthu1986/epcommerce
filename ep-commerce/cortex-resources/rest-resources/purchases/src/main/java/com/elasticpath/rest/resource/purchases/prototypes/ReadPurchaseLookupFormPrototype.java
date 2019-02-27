/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.prototypes;

import io.reactivex.Single;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.rest.definition.purchases.PurchaseLookupFormResource;
import com.elasticpath.rest.definition.purchases.PurchaseNumberEntity;

/**
 * ReadPurchaseLookupFormPrototype.
 */
public class ReadPurchaseLookupFormPrototype implements PurchaseLookupFormResource.Read {

    @Override
    public Single<PurchaseNumberEntity> onRead() {
        return Single.just(PurchaseNumberEntity.builder()
                .withPurchaseNumber(StringUtils.EMPTY)
                .build());
    }
}

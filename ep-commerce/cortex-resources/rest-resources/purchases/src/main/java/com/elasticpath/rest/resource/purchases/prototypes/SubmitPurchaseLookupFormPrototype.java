/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;

import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLookupFormIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLookupFormResource;
import com.elasticpath.rest.definition.purchases.PurchaseNumberEntity;
import com.elasticpath.rest.form.SubmitResult;
import com.elasticpath.rest.helix.data.annotation.RequestForm;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * SubmitPurchaseLookupFormPrototype.
 */
public class SubmitPurchaseLookupFormPrototype implements PurchaseLookupFormResource.SubmitWithResult {

    private final PurchaseNumberEntity purchaseNumberEntity;

    private final Repository<PurchaseNumberEntity, PurchaseIdentifier> repository;

    private final PurchaseLookupFormIdentifier purchaseLookupFormIdentifier;

    /**
     * Constructor.
     *
     * @param purchaseNumberEntity       purchaseNumberEntity
     * @param repository                Repository
     * @param purchaseLookupFormIdentifier	purchaseLookupFormIdentifier
     */
    @Inject
    public SubmitPurchaseLookupFormPrototype(@RequestForm final PurchaseNumberEntity purchaseNumberEntity,
                                         @ResourceRepository final Repository<PurchaseNumberEntity, PurchaseIdentifier> repository,
                                         @RequestIdentifier final PurchaseLookupFormIdentifier purchaseLookupFormIdentifier) {
        this.purchaseNumberEntity = purchaseNumberEntity;
        this.repository = repository;
        this.purchaseLookupFormIdentifier = purchaseLookupFormIdentifier;
    }

    @Override
    public Single<SubmitResult<PurchaseIdentifier>> onSubmitWithResult() {
        return repository.submit(purchaseNumberEntity, purchaseLookupFormIdentifier.getPurchases().getScope());
    }
}


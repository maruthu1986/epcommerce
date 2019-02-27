/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.lookups.LookupsIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLookupFormFromLookupsRelationship;
import com.elasticpath.rest.definition.purchases.PurchaseLookupFormIdentifier;
import com.elasticpath.rest.definition.purchases.PurchasesIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * PurchaseLookupFormFromLookupsRelationshipImpl.
 */
public class PurchaseLookupFormFromLookupsRelationshipImpl implements PurchaseLookupFormFromLookupsRelationship.LinkTo {


    private final LookupsIdentifier lookupsIdentifier;

    /**
     * Constructor.
     *
     * @param lookupsIdentifier lookupsIdentifier
     */
    @Inject
    public PurchaseLookupFormFromLookupsRelationshipImpl(@RequestIdentifier final LookupsIdentifier lookupsIdentifier) {
        this.lookupsIdentifier = lookupsIdentifier;
    }

    @Override
    public Observable<PurchaseLookupFormIdentifier> onLinkTo() {
        return Observable.just(PurchaseLookupFormIdentifier.builder()
                .withPurchases(PurchasesIdentifier.builder().withScope(lookupsIdentifier.getScope()).build())
                .build());
    }
}

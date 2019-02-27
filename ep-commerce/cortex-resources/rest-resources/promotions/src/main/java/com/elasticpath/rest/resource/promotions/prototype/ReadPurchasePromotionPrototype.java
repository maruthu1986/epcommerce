/*
 *  Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.prototype;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.promotions.PromotionEntity;
import com.elasticpath.rest.definition.promotions.PurchasePromotionIdentifier;
import com.elasticpath.rest.definition.promotions.PurchasePromotionResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Prototype that implements reading purchase promotion.
 */
public class ReadPurchasePromotionPrototype implements PurchasePromotionResource.Read {

	private final Repository<PromotionEntity, PurchasePromotionIdentifier> repository;
	private final PurchasePromotionIdentifier purchasePromotionIdentifier;

	/**
	 * Constructor.
	 *
	 * @param purchasePromotionIdentifier	identifier
	 * @param repository					repository
	 */
	@Inject
	public ReadPurchasePromotionPrototype(@RequestIdentifier final PurchasePromotionIdentifier purchasePromotionIdentifier,
										  @ResourceRepository final Repository<PromotionEntity, PurchasePromotionIdentifier> repository) {
		this.purchasePromotionIdentifier = purchasePromotionIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<PromotionEntity> onRead() {
		return repository.findOne(purchasePromotionIdentifier);
	}
}

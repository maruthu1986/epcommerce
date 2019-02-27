/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.discounts.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.discounts.DiscountEntity;
import com.elasticpath.rest.definition.discounts.DiscountForPurchaseIdentifier;
import com.elasticpath.rest.definition.discounts.DiscountForPurchaseResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Discounts for cart prototype for Read operation.
 */
public class ReadDiscountsForPurchasePrototype implements DiscountForPurchaseResource.Read {

	private final DiscountForPurchaseIdentifier discountForPurchaseIdentifier;
	private final Repository<DiscountEntity, DiscountForPurchaseIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param discountForPurchaseIdentifier	discountForCartIdentifier
	 * @param repository      			repository
	 */
	@Inject
	public ReadDiscountsForPurchasePrototype(@RequestIdentifier final DiscountForPurchaseIdentifier discountForPurchaseIdentifier,
										 @ResourceRepository final Repository<DiscountEntity, DiscountForPurchaseIdentifier> repository) {
		this.discountForPurchaseIdentifier = discountForPurchaseIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<DiscountEntity> onRead() {
		return repository.findOne(discountForPurchaseIdentifier);
	}
}

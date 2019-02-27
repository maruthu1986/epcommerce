/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.discounts.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.discounts.DiscountEntity;
import com.elasticpath.rest.definition.discounts.DiscountForCartIdentifier;
import com.elasticpath.rest.definition.discounts.DiscountForCartResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Discounts for cart prototype for Read operation.
 */
public class ReadDiscountsForCartPrototype implements DiscountForCartResource.Read {

	private final DiscountForCartIdentifier discountForCartIdentifier;
	private final Repository<DiscountEntity, DiscountForCartIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param discountForCartIdentifier	discountForCartIdentifier
	 * @param repository      			repository
	 */
	@Inject
	public ReadDiscountsForCartPrototype(@RequestIdentifier final DiscountForCartIdentifier discountForCartIdentifier,
										 @ResourceRepository final Repository<DiscountEntity, DiscountForCartIdentifier> repository) {
		this.discountForCartIdentifier = discountForCartIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<DiscountEntity> onRead() {
		return repository.findOne(discountForCartIdentifier);
	}
}

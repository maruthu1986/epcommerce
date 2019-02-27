/*
 *  Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.prototype;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.promotions.PromotionEntity;
import com.elasticpath.rest.definition.promotions.PromotionIdentifier;
import com.elasticpath.rest.definition.promotions.PromotionResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Prototype that implements reading a promotion.
 */
public class ReadPromotionPrototype implements PromotionResource.Read {

	private final PromotionIdentifier promotionIdentifier;
	private final Repository<PromotionEntity, PromotionIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param promotionIdentifier identifier
	 * @param repository          repository
	 */
	@Inject
	public ReadPromotionPrototype(@RequestIdentifier final PromotionIdentifier promotionIdentifier,
								  @ResourceRepository final Repository<PromotionEntity, PromotionIdentifier> repository) {
		this.promotionIdentifier = promotionIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<PromotionEntity> onRead() {
		return repository.findOne(promotionIdentifier);
	}
}

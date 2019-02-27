/*
 *  Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.prototype;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForShippingOptionIdentifier;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForShippingOptionResource;
import com.elasticpath.rest.definition.promotions.PromotionIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Prototype that implements reading promotions for shippingoption.
 */
public class ReadAppliedPromotionsForShippingOptionPrototype implements AppliedPromotionsForShippingOptionResource.Read {

	private final AppliedPromotionsForShippingOptionIdentifier appliedPromotionsForShippingOptionIdentifier;
	private final LinksRepository<AppliedPromotionsForShippingOptionIdentifier, PromotionIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param appliedPromotionsForShippingOptionIdentifier	identifier
	 * @param repository									repository
	 */
	@Inject
	public ReadAppliedPromotionsForShippingOptionPrototype(
			@RequestIdentifier final AppliedPromotionsForShippingOptionIdentifier appliedPromotionsForShippingOptionIdentifier,
			@ResourceRepository final LinksRepository<AppliedPromotionsForShippingOptionIdentifier, PromotionIdentifier> repository) {
		this.appliedPromotionsForShippingOptionIdentifier = appliedPromotionsForShippingOptionIdentifier;
		this.repository = repository;
	}

	@Override
	public Observable<PromotionIdentifier> onRead() {
		return repository.getElements(appliedPromotionsForShippingOptionIdentifier);
	}
}

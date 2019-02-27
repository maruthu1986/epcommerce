/*
 *  Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.prototype;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForCartIdentifier;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForCartResource;
import com.elasticpath.rest.definition.promotions.PromotionIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Prototype that implements reading promotions for cart.
 */
public class ReadAppliedPromotionsForCartPrototype implements AppliedPromotionsForCartResource.Read {

	private final AppliedPromotionsForCartIdentifier appliedPromotionsForCartIdentifier;
	private final LinksRepository<AppliedPromotionsForCartIdentifier, PromotionIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param appliedPromotionsForCartIdentifier	identifier
	 * @param repository							repository
	 */
	@Inject
	public ReadAppliedPromotionsForCartPrototype(
			@RequestIdentifier final AppliedPromotionsForCartIdentifier appliedPromotionsForCartIdentifier,
			@ResourceRepository final LinksRepository<AppliedPromotionsForCartIdentifier, PromotionIdentifier> repository) {
		this.appliedPromotionsForCartIdentifier = appliedPromotionsForCartIdentifier;
		this.repository = repository;
	}

	@Override
	public Observable<PromotionIdentifier> onRead() {
		return repository.getElements(appliedPromotionsForCartIdentifier);
	}
}

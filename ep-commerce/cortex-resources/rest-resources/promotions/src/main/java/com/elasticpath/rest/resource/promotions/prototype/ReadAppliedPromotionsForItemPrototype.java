/*
 *  Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.prototype;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForItemIdentifier;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForItemResource;
import com.elasticpath.rest.definition.promotions.PromotionIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Prototype that implements reading promotion for an item.
 */
public class ReadAppliedPromotionsForItemPrototype implements AppliedPromotionsForItemResource.Read {

	private final AppliedPromotionsForItemIdentifier appliedPromotionsForItemIdentifier;
	private final LinksRepository<AppliedPromotionsForItemIdentifier, PromotionIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param appliedPromotionsForItemIdentifier	identifier
	 * @param repository							repository
	 */
	@Inject
	public ReadAppliedPromotionsForItemPrototype(
			@RequestIdentifier final AppliedPromotionsForItemIdentifier appliedPromotionsForItemIdentifier,
			@ResourceRepository final LinksRepository<AppliedPromotionsForItemIdentifier, PromotionIdentifier> repository) {
		this.appliedPromotionsForItemIdentifier = appliedPromotionsForItemIdentifier;
		this.repository = repository;
	}

	@Override
	public Observable<PromotionIdentifier> onRead() {
		return repository.getElements(appliedPromotionsForItemIdentifier);
	}
}

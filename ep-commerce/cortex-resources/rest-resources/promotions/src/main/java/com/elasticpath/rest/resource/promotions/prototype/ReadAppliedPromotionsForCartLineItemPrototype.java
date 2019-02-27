/*
 *  Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.prototype;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForCartLineItemIdentifier;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForCartLineItemResource;
import com.elasticpath.rest.definition.promotions.PromotionIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Prototype that implements reading promotions of a cart lineitem.
 */
public class ReadAppliedPromotionsForCartLineItemPrototype implements AppliedPromotionsForCartLineItemResource.Read {

	private final AppliedPromotionsForCartLineItemIdentifier appliedPromotionsForCartLineItemIdentifier;
	private final LinksRepository<AppliedPromotionsForCartLineItemIdentifier, PromotionIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param appliedPromotionsForCartLineItemIdentifier	identifier
	 * @param repository									repository
	 */
	@Inject
	public ReadAppliedPromotionsForCartLineItemPrototype(
			@RequestIdentifier final AppliedPromotionsForCartLineItemIdentifier appliedPromotionsForCartLineItemIdentifier,
			@ResourceRepository final LinksRepository<AppliedPromotionsForCartLineItemIdentifier, PromotionIdentifier> repository) {
		this.appliedPromotionsForCartLineItemIdentifier = appliedPromotionsForCartLineItemIdentifier;
		this.repository = repository;
	}

	@Override
	public Observable<PromotionIdentifier> onRead() {
		return repository.getElements(appliedPromotionsForCartLineItemIdentifier);
	}
}

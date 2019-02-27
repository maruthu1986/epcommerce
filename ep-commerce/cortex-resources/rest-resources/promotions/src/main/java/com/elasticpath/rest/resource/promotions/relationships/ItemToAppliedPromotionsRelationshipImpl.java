/*
 *  Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForItemIdentifier;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForItemRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Adds a promotion link in item.
 */
public class ItemToAppliedPromotionsRelationshipImpl implements AppliedPromotionsForItemRelationship.LinkTo {

	private final ItemIdentifier itemIdentifier;

	/**
	 * Constructor.
	 *
	 * @param itemIdentifier	identifier
	 */
	@Inject
	public ItemToAppliedPromotionsRelationshipImpl(@RequestIdentifier final ItemIdentifier itemIdentifier) {
		this.itemIdentifier = itemIdentifier;
	}

	@Override
	public Observable<AppliedPromotionsForItemIdentifier> onLinkTo() {
		return Observable.just(AppliedPromotionsForItemIdentifier.builder()
				.withItem(itemIdentifier)
				.build());
	}
}

/*
 *  Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.relationships;

import java.util.Map;
import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.items.ItemsIdentifier;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForItemIdentifier;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForItemRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.id.IdentifierPart;

/**
 * Adds an item link in promotions.
 */
public class AppliedPromotionsToItemRelationshipImpl implements AppliedPromotionsForItemRelationship.LinkFrom {

	private final AppliedPromotionsForItemIdentifier appliedPromotionsForItemIdentifier;

	/**
	 * Constructor.
	 *
	 * @param appliedPromotionsForItemIdentifier	identifier
	 */
	@Inject
	public AppliedPromotionsToItemRelationshipImpl(@RequestIdentifier final AppliedPromotionsForItemIdentifier
															   appliedPromotionsForItemIdentifier) {
		this.appliedPromotionsForItemIdentifier = appliedPromotionsForItemIdentifier;
	}

	@Override
	public Observable<ItemIdentifier> onLinkFrom() {
		ItemIdentifier itemIdentifier = appliedPromotionsForItemIdentifier.getItem();
		IdentifierPart<Map<String, String>> itemId = itemIdentifier.getItemId();
		ItemsIdentifier itemsIdentifier = itemIdentifier.getItems();
		return Observable.just(ItemIdentifier.builder()
				.withItemId(itemId)
				.withItems(itemsIdentifier)
				.build());
	}
}

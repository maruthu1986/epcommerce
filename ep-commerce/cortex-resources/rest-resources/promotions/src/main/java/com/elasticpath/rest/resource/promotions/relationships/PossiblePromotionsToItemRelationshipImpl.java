/*
 *  Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.relationships;

import java.util.Map;
import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.items.ItemsIdentifier;
import com.elasticpath.rest.definition.promotions.PossiblePromotionsForItemIdentifier;
import com.elasticpath.rest.definition.promotions.PossiblePromotionsForItemRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.id.IdentifierPart;

/**
 * Adds a item link in promotions.
 */
public class PossiblePromotionsToItemRelationshipImpl implements PossiblePromotionsForItemRelationship.LinkFrom {

	private final PossiblePromotionsForItemIdentifier possiblePromotionsForItemIdentifier;

	/**
	 * Constructor.
	 *
	 * @param possiblePromotionsForItemIdentifier	identifier
	 */
	@Inject
	public PossiblePromotionsToItemRelationshipImpl(@RequestIdentifier final PossiblePromotionsForItemIdentifier
																possiblePromotionsForItemIdentifier) {
		this.possiblePromotionsForItemIdentifier = possiblePromotionsForItemIdentifier;
	}

	@Override
	public Observable<ItemIdentifier> onLinkFrom() {
		ItemIdentifier itemIdentifier = possiblePromotionsForItemIdentifier.getItem();
		IdentifierPart<Map<String, String>> itemId = itemIdentifier.getItemId();
		ItemsIdentifier itemsIdentifier = itemIdentifier.getItems();
		return Observable.just(ItemIdentifier.builder()
				.withItems(itemsIdentifier)
				.withItemId(itemId)
				.build());
	}
}

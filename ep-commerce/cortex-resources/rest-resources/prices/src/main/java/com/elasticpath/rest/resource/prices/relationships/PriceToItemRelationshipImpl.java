/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.prices.PriceForItemIdentifier;
import com.elasticpath.rest.definition.prices.PriceForItemRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Adds a link from price to item.
 */
public class PriceToItemRelationshipImpl implements PriceForItemRelationship.LinkFrom {

	private final PriceForItemIdentifier priceForItemIdentifier;

	/**
	 * Constructor.
	 *
	 * @param priceForItemIdentifier	priceForItemIdentifier
	 */
	@Inject
	public PriceToItemRelationshipImpl(@RequestIdentifier final PriceForItemIdentifier priceForItemIdentifier) {
		this.priceForItemIdentifier = priceForItemIdentifier;
	}

	@Override
	public Observable<ItemIdentifier> onLinkFrom() {
		ItemIdentifier itemIdentifier = priceForItemIdentifier.getItem();
		return Observable.just(ItemIdentifier.builder()
				.withItemId(itemIdentifier.getItemId())
				.withItems(itemIdentifier.getItems())
				.build());
	}
}
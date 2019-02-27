/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionIdentifier;
import com.elasticpath.rest.definition.prices.LowestPriceForItemDefinitionRelationship;
import com.elasticpath.rest.definition.prices.PriceForItemdefinitionIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Adds a link from price to itemdefinition.
 */
public class LowestPriceToItemDefinitionRelationshipImpl implements LowestPriceForItemDefinitionRelationship.LinkFrom {

	private final PriceForItemdefinitionIdentifier priceForItemdefinitionIdentifier;

	/**
	 * Constructor.
	 *
	 * @param priceForItemdefinitionIdentifier	priceForItemdefinitionIdentifier
	 */
	@Inject
	public LowestPriceToItemDefinitionRelationshipImpl(@RequestIdentifier final PriceForItemdefinitionIdentifier priceForItemdefinitionIdentifier) {
		this.priceForItemdefinitionIdentifier = priceForItemdefinitionIdentifier;
	}

	@Override
	public Observable<ItemDefinitionIdentifier> onLinkFrom() {
		ItemDefinitionIdentifier itemDefinitionIdentifier = priceForItemdefinitionIdentifier.getItemDefinition();
		return Observable.just(ItemDefinitionIdentifier.builder()
				.withItemId(itemDefinitionIdentifier.getItemId())
				.withScope(itemDefinitionIdentifier.getScope())
				.build());
	}
}

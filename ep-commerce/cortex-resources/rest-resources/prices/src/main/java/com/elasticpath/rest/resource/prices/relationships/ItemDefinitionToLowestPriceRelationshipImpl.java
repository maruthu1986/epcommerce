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
 * Adds a link from itemdefinition to price.
 */
public class ItemDefinitionToLowestPriceRelationshipImpl implements LowestPriceForItemDefinitionRelationship.LinkTo {

	private final ItemDefinitionIdentifier itemDefinitionIdentifier;

	/**
	 * Constructor.
	 *
	 * @param itemDefinitionIdentifier	itemDefinitionIdentifier
	 */
	@Inject
	public ItemDefinitionToLowestPriceRelationshipImpl(@RequestIdentifier final ItemDefinitionIdentifier itemDefinitionIdentifier) {
		this.itemDefinitionIdentifier = itemDefinitionIdentifier;
	}

	@Override
	public Observable<PriceForItemdefinitionIdentifier> onLinkTo() {
		return Observable.just(PriceForItemdefinitionIdentifier.builder()
				.withItemDefinition(itemDefinitionIdentifier)
				.build());
	}
}

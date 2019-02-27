/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.carts.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.carts.AddToDefaultCartFormForItemRelationship;
import com.elasticpath.rest.definition.carts.AddToDefaultCartFormIdentifier;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Item to Add to Default Cart form.
 */
public class AddToDefaultCartFormForItemRelationshipImpl implements AddToDefaultCartFormForItemRelationship.LinkTo {

	private final ItemIdentifier itemIdentifier;

	/**
	 * Constructor.
	 *
	 * @param itemIdentifier itemIdentifier
	 */
	@Inject
	public AddToDefaultCartFormForItemRelationshipImpl(@RequestIdentifier final ItemIdentifier itemIdentifier) {
		this.itemIdentifier = itemIdentifier;
	}


	@Override
	public Observable<AddToDefaultCartFormIdentifier> onLinkTo() {
		return Observable.just(AddToDefaultCartFormIdentifier.builder().withItem(itemIdentifier).build());
	}
}

/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.carts.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.carts.CartMembershipsFromItemRelationship;
import com.elasticpath.rest.definition.carts.ReadCartMembershipsIdentifier;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Item to cart memberships link.
 */
public class CartMembershipsFromItemRelationshipImpl implements CartMembershipsFromItemRelationship.LinkTo {

	private final ItemIdentifier itemIdentifier;

	/**
	 * Constructor.
	 *
	 * @param itemIdentifier itemIdentifier
	 */
	@Inject
	public CartMembershipsFromItemRelationshipImpl(@RequestIdentifier final ItemIdentifier itemIdentifier) {
		this.itemIdentifier = itemIdentifier;
	}


	@Override
	public Observable<ReadCartMembershipsIdentifier> onLinkTo() {
		return Observable.just(ReadCartMembershipsIdentifier.builder()
				.withItem(itemIdentifier)
				.build());
	}
}

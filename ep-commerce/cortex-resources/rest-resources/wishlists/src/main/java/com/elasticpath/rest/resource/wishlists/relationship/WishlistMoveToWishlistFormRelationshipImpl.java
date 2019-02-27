/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.wishlists.relationship;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.definition.wishlists.MoveToWishlistFormIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistMoveToWishlistFormRelationship;
import com.elasticpath.rest.definition.wishlists.WishlistsIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.UriPart;
import com.elasticpath.rest.id.IdentifierPart;

/**
 * Move to favorites form link.
 */
public class WishlistMoveToWishlistFormRelationshipImpl implements WishlistMoveToWishlistFormRelationship.LinkTo {

	private final LineItemIdentifier lineItemIdentifier;
	private final IdentifierPart<String> scope;

	/**
	 * Constructor.
	 *
	 * @param lineItemIdentifier lineItemIdentifier
	 * @param scope scope
	 */
	@Inject
	public WishlistMoveToWishlistFormRelationshipImpl(@RequestIdentifier final LineItemIdentifier lineItemIdentifier,
			@UriPart(WishlistsIdentifier.SCOPE) final IdentifierPart<String> scope) {
		this.lineItemIdentifier = lineItemIdentifier;
		this.scope = scope;
	}

	@Override
	public Observable<MoveToWishlistFormIdentifier> onLinkTo() {
		return Observable.just(MoveToWishlistFormIdentifier.builder()
				.withWishlists(WishlistsIdentifier.builder()
						.withScope(scope)
						.build())
				.withLineItem(lineItemIdentifier)
				.build());
	}
}

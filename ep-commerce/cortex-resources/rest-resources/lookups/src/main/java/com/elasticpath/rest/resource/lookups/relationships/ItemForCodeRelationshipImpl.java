/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.lookups.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.lookups.CodeForItemIdentifier;
import com.elasticpath.rest.definition.lookups.CodeForItemRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Item for code relationship implementation.
 */
public class ItemForCodeRelationshipImpl implements CodeForItemRelationship.LinkFrom {


	private final CodeForItemIdentifier codeForItemIdentifier;

	/**
	 * Constructor.
	 *
	 * @param codeForItemIdentifier codeForItemIdentifier
	 */
	@Inject
	public ItemForCodeRelationshipImpl(@RequestIdentifier final CodeForItemIdentifier codeForItemIdentifier) {
		this.codeForItemIdentifier = codeForItemIdentifier;
	}

	@Override
	public Observable<ItemIdentifier> onLinkFrom() {
		return Observable.just(codeForItemIdentifier.getItem());
	}
}

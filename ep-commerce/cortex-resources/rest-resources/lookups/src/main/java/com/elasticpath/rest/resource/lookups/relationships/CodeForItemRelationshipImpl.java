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
 * Code for item relationship implementation.
 */
public class CodeForItemRelationshipImpl implements CodeForItemRelationship.LinkTo {


	private final ItemIdentifier itemIdentifier;

	/**
	 * Constructor.
	 *
	 * @param itemIdentifier itemIdentifier
	 */
	@Inject
	public CodeForItemRelationshipImpl(@RequestIdentifier final ItemIdentifier itemIdentifier) {
		this.itemIdentifier = itemIdentifier;
	}

	@Override
	public Observable<CodeForItemIdentifier> onLinkTo() {
		return Observable.just(CodeForItemIdentifier.builder()
				.withItem(itemIdentifier)
				.build());
	}
}

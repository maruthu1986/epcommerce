/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.lookups.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.lookups.CodeEntity;
import com.elasticpath.rest.definition.lookups.CodeForItemIdentifier;
import com.elasticpath.rest.definition.lookups.CodeForItemResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Read prototype for code for item resource.
 */
public class ReadCodeForItemPrototype implements CodeForItemResource.Read {

	private final CodeForItemIdentifier codeForItemIdentifier;
	private final Repository<CodeEntity, ItemIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param codeForItemIdentifier	codeForItemIdentifier
	 * @param repository			repository
	 */
	@Inject
	public ReadCodeForItemPrototype(@RequestIdentifier final CodeForItemIdentifier codeForItemIdentifier,
									@ResourceRepository final Repository<CodeEntity, ItemIdentifier> repository) {
		this.codeForItemIdentifier = codeForItemIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<CodeEntity> onRead() {
		return repository.findOne(codeForItemIdentifier.getItem());
	}
}
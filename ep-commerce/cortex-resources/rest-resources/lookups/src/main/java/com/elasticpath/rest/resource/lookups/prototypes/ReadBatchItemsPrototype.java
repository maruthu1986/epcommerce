/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.lookups.prototypes;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.lookups.BatchItemsIdentifier;
import com.elasticpath.rest.definition.lookups.BatchItemsResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Read prototype for batch items resource.
 */
public class ReadBatchItemsPrototype implements BatchItemsResource.Read {

	private final BatchItemsIdentifier batchItemsIdentifier;
	private final LinksRepository<BatchItemsIdentifier, ItemIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param batchItemsIdentifier	batchItemsIdentifier
	 * @param repository			codes entity repository
	 */
	@Inject
	public ReadBatchItemsPrototype(@RequestIdentifier final BatchItemsIdentifier batchItemsIdentifier,
								   @ResourceRepository final LinksRepository<BatchItemsIdentifier, ItemIdentifier> repository) {
		this.batchItemsIdentifier = batchItemsIdentifier;
		this.repository = repository;
	}

	@Override
	public Observable<ItemIdentifier> onRead() {
		return repository.getElements(batchItemsIdentifier);
	}
}
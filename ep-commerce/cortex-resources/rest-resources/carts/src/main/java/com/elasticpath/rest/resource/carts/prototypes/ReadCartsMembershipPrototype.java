/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.carts.prototypes;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.carts.ReadCartMembershipsIdentifier;
import com.elasticpath.rest.definition.carts.ReadCartMembershipsResource;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Cart membership for an item.
 */
public class ReadCartsMembershipPrototype implements ReadCartMembershipsResource.Read {

	private final ItemIdentifier itemIdentifier;

	private final LinksRepository<ItemIdentifier, CartIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param readCartMembershipsIdentifier readCartMembershipsIdentifier
	 * @param repository                    repository
	 */
	@Inject
	public ReadCartsMembershipPrototype(@RequestIdentifier final ReadCartMembershipsIdentifier readCartMembershipsIdentifier,
										@ResourceRepository final LinksRepository<ItemIdentifier, CartIdentifier> repository) {
		this.itemIdentifier = readCartMembershipsIdentifier.getItem();
		this.repository = repository;
	}

	@Override
	public Observable<CartIdentifier> onRead() {
		return repository.getElements(itemIdentifier);
	}

}

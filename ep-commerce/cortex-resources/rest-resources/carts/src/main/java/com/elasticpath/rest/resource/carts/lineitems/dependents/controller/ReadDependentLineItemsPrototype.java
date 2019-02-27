/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.lineitems.dependents.controller;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.carts.DependentLineItemsIdentifier;
import com.elasticpath.rest.definition.carts.DependentLineItemsResource;
import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.carts.lineitems.DependentLineItemRepository;

/**
 * Prototype-scoped controller to handle requests to read the list of dependent cart line items.
 */
public class ReadDependentLineItemsPrototype implements DependentLineItemsResource.Read {

	private final DependentLineItemsIdentifier lineItemIdentifier;

	private final DependentLineItemRepository dependentLineItemRepository;

	/**
	 * Constructor.
	 *
	 * @param dependentLineItemIdentifier the line item identifier
	 * @param dependentLineItemRepository the dependent line item repository
	 */
	@Inject
	public ReadDependentLineItemsPrototype(
			@RequestIdentifier
			final DependentLineItemsIdentifier dependentLineItemIdentifier,
			@ResourceRepository
			final DependentLineItemRepository dependentLineItemRepository) {
		this.lineItemIdentifier = dependentLineItemIdentifier;
		this.dependentLineItemRepository = dependentLineItemRepository;
	}

	@Override
	public Observable<LineItemIdentifier> onRead() {
		return dependentLineItemRepository.getElements(lineItemIdentifier.getLineItem());
	}

}

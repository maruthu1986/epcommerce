/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.lineitems.dependents.relationship;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.definition.carts.LineitemToParentLineitemRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.carts.lineitems.DependentLineItemRepository;

/**
 * Link from a cart Line Item resource to its owning parent.
 */
public class LineItemToParentLineItemRelationshipDefinition implements LineitemToParentLineitemRelationship.LinkTo {

	private final LineItemIdentifier lineItemIdentifier;

	private final DependentLineItemRepository dependentLineItemRepository;

	/**
	 * Constructor.
	 *
	 * @param lineItemIdentifier the line item identifier
	 * @param dependentLineItemRepository the dependent line item repository
	 */
	@Inject
	public LineItemToParentLineItemRelationshipDefinition(
			@RequestIdentifier
			final LineItemIdentifier lineItemIdentifier,
			@ResourceRepository
			final DependentLineItemRepository dependentLineItemRepository) {
		this.lineItemIdentifier = lineItemIdentifier;
		this.dependentLineItemRepository = dependentLineItemRepository;
	}

	@Override
	public Observable<LineItemIdentifier> onLinkTo() {
		return dependentLineItemRepository.findParent(lineItemIdentifier)
				.toObservable();
	}

}

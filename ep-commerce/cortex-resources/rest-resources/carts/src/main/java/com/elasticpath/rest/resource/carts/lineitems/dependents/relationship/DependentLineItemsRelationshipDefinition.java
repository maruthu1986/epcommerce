/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.lineitems.dependents.relationship;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.carts.DependentLineItemsIdentifier;
import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.definition.carts.LineitemToDependentLineitemsRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Link from a cart Line Item resource to its collection of dependent line items.
 */
public class DependentLineItemsRelationshipDefinition implements LineitemToDependentLineitemsRelationship.LinkTo {

	private final LineItemIdentifier lineItemIdentifier;

	/**
	 * Constructor.
	 *
	 * @param lineItemIdentifier the line item identifier
	 */
	@Inject
	public DependentLineItemsRelationshipDefinition(
			@RequestIdentifier
			final LineItemIdentifier lineItemIdentifier) {
		this.lineItemIdentifier = lineItemIdentifier;
	}

	@Override
	public Observable<DependentLineItemsIdentifier> onLinkTo() {
		return Observable.fromArray(DependentLineItemsIdentifier.builder()
											.withLineItem(lineItemIdentifier)
											.build());
	}

}

/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.carts.lineitems;

import io.reactivex.Maybe;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.carts.LineItemIdentifier;

/**
 * Repository that provides access to dependent cart Line Item entities.
 */
public interface DependentLineItemRepository extends LinksRepository<LineItemIdentifier, LineItemIdentifier> {

	/**
	 * Returns the identifier of the line item's owning parent, if this line item is a dependent.
	 *
	 * @param lineItemIdentifier the line item for which a parent should be found
	 * @return the identifier corresponding to the owning parent cart line item, if applicable
	 */
	Maybe<LineItemIdentifier> findParent(LineItemIdentifier lineItemIdentifier);

}

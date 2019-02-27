/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.customer;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.tags.TagSet;

/**
 * Create Customer-based Tags and populate a TagSet.
 */
public interface CustomerTagStrategy {

	/**
	 * Populate the provided TagSet with appropriate Tags. Strategies should examine the provided
	 * TagSet to decide if a Tag should be added as it may already exist.
	 *
	 * @param customer the current Customer
	 * @param tagSet the TagSet to populate
	 */
	void populate(Customer customer, TagSet tagSet);
}

/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.authentication.epcommerce.transformer;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.rest.relos.rs.authentication.User;

/**
 * Transforms a {@link Customer} into a {@link User}.
 */
public interface CustomerTransformer {

	/**
	 * Transform a {@link Customer} into a {@link User}.
	 *
	 * @param customer The Customer to transform
	 * @return the User
	 */
	User transform(Customer customer);
}

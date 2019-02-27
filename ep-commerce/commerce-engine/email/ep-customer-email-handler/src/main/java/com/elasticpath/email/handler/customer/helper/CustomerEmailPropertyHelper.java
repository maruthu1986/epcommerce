/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.email.handler.customer.helper;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.email.domain.EmailProperties;

/**
 * Helper for constructing email properties.
 */
public interface CustomerEmailPropertyHelper {

	/**
	 * Returns email properties.
	 *
	 * @param customer the customer object
	 *
	 * @return {@link EmailProperties}
	 */
	EmailProperties getPasswordConfirmationEmailProperties(Customer customer);

	/**
	 * Returns email properties.
	 *
	 * @param customer the customer object
	 * @param newPassword the new password
	 * @return {@link EmailProperties}
	 */
	EmailProperties getNewlyRegisteredCustomerEmailProperties(Customer customer, String newPassword);

	/**
	 * Returns email properties.
	 *
	 * @param customer the customer object
	 * @param newPassword the new password
	 * @return {@link EmailProperties}
	 */
	EmailProperties getForgottenPasswordEmailProperties(Customer customer, String newPassword);

	/**
	 * Gets the email properties on creation of a new customer account.
	 *
	 * @param customer the customer
	 * @return {@link EmailProperties}
	 */
	EmailProperties getNewAccountEmailProperties(Customer customer);

}

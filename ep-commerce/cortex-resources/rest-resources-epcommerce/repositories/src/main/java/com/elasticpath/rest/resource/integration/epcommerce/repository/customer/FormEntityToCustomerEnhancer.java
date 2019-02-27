/**
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.customer;


import com.elasticpath.domain.customer.Customer;
import com.elasticpath.rest.definition.registrations.RegistrationEntity;

/**
 * Enhances the Customer with information from the registration or profile resource.
 */
public interface FormEntityToCustomerEnhancer {

	/**
	 * Updates the customer attributes with the registration entity.
	 *
	 * @param registrationEntity registration entity
	 * @param customer           customer to update
	 * @return customer
	 */
	Customer registrationEntityToCustomer(RegistrationEntity registrationEntity, Customer customer);

}

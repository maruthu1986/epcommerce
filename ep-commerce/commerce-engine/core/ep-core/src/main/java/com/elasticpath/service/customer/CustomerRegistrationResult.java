/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.customer;

import java.io.Serializable;
import java.util.Set;
import javax.validation.ConstraintViolation;

import com.elasticpath.domain.customer.Customer;

/**
 * Holds errors/parameters from register customer operations.
 */
public interface CustomerRegistrationResult extends Serializable {

	/**
	 * Sets the registered customer.
	 *
	 * @param customer the registered customer..
	 */
	void setRegisteredCustomer(Customer customer);

	/**
	 * Gets the registered customer.
	 *
	 * @return the registered customer.
	 */
	Customer getRegisteredCustomer();

	/**
	 * Sets the set of customer field constraint violations.
	 *
	 * @param constraintViolations the set of constraint violations.
	 */
	void setConstraintViolations(Set<ConstraintViolation<Customer>> constraintViolations);

	/**
	 *	Gets the set of customer field constraint violations.
	 *
	 * @return the set of constraint violations.
	 */
	Set<ConstraintViolation<Customer>> getConstraintViolations();
}

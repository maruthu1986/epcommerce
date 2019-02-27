/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.customer.impl;

import java.util.Set;
import javax.validation.ConstraintViolation;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.service.customer.CustomerRegistrationResult;

/**
 * Holds errors/parameters from register customer operations.
 */
public class CustomerRegistrationResultImpl implements CustomerRegistrationResult {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private Customer customer;
	private Set<ConstraintViolation<Customer>> constraintViolations;

	@Override
	public void setRegisteredCustomer(final Customer customer) {
		this.customer = customer;
	}

	@Override
	public Customer getRegisteredCustomer() {
		return this.customer;
	}

	@Override
	public void setConstraintViolations(final Set<ConstraintViolation<Customer>> constraintViolations) {
		this.constraintViolations = constraintViolations;
	}

	@Override
	public Set<ConstraintViolation<Customer>> getConstraintViolations() {
		return this.constraintViolations;
	}

}

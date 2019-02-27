/**
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.customer.impl;

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.rest.definition.registrations.RegistrationEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.FormEntityToCustomerEnhancer;

/**
 * Implementation of FormEntityCustomerEnhancer.
 */
@Singleton
@Named("formEntityToCustomerEnhancer")
public class FormEntityToCustomerEnhancerImpl implements FormEntityToCustomerEnhancer {

	@Override
	public Customer registrationEntityToCustomer(final RegistrationEntity registrationEntity, final Customer customer) {

		if (registrationEntity.getGivenName() != null) {
			customer.setFirstName(StringUtils.trimToNull(registrationEntity.getGivenName()));
		}

		if (registrationEntity.getFamilyName() != null) {
			customer.setLastName(StringUtils.trimToNull(registrationEntity.getFamilyName()));
		}

		if (registrationEntity.getPassword() != null) {
			customer.setClearTextPassword(StringUtils.trimToNull(registrationEntity.getPassword()));
		}

		if (registrationEntity.getUsername() != null) {
			customer.setEmail(StringUtils.trimToNull(registrationEntity.getUsername()));
		}

		return customer;
	}

}

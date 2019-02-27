/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.authentication.epcommerce.transformer.impl;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.rest.identity.util.PrincipalsUtil;
import com.elasticpath.rest.relos.rs.authentication.User;
import com.elasticpath.rest.relos.rs.authentication.epcommerce.transformer.CustomerTransformer;

/**
 * Test the behaviour of {@link CustomerTransformerImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomerTransformerTest {

	private static final String SCOPE = "SCOPE";
	private static final String USER_ID = "jeanluc.picard@starfleetacademy.edu";
	private static final String CUSTOMER_GUID = "499A4471-06CE-CB60-BFBE-511F8D96C21E";
	private static final String ENCODED_PASSWORD = "5baa61e4c9b93f3f0682250b6cf8331b7ee68fd8";
	private static final String SALT = "7e48b1de633bfdbab38f783cbda591834bb63503912d206d3da104f88dbff9c7";

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private Customer customer;

	private final Set<String> roleNames = new HashSet<>();
	private final CustomerTransformer customerTransformer = new CustomerTransformerImpl();

	/**
	 * Test assemble dto.
	 */
	@Test
	public void testTransformToEntity() {
		setupMockCustomer();
		User user = customerTransformer.transform(customer);
		assertEquals("The scope should match", SCOPE, user.getRequestedScope());
		assertEquals("The user name should match", USER_ID, user.getUsername());
		assertEquals("The principals should match", PrincipalsUtil.createRolePrincipals(roleNames), user.getPrincipals());
		assertTrue("The account enabled should match which is expected of a customer in status active", user.isAccountEnabled());
		assertFalse("The account locked should match which is expected of a customer in status active", user.isAccountLocked());
		assertFalse("The account expired should match", user.isAccountExpired());
		assertEquals("The guid should match", CUSTOMER_GUID, user.getUserId());
		assertEquals("The salt should match", SALT, user.getSalt());
		assertTrue("Account should be enabled", user.isAccountEnabled());
		assertFalse("Account should not be expired", user.isAccountExpired());
		assertEquals("The passwords should match", ENCODED_PASSWORD, user.getPassword());
	}

	private void setupMockCustomer() {
		when(customer.getStoreCode())
			.thenReturn(SCOPE);

		when(customer.getUserId())
			.thenReturn(USER_ID);

		when(customer.getPassword())
			.thenReturn(ENCODED_PASSWORD);

		when(customer.getCustomerRoleMapper().getAllRoles())
			.thenReturn(roleNames);

		when(customer.getStatus())
			.thenReturn(Customer.STATUS_ACTIVE);

		when(customer.isAccountNonExpired())
			.thenReturn(true);

		when(customer.getGuid())
			.thenReturn(CUSTOMER_GUID);

		when(customer.getCustomerAuthentication().getSalt())
			.thenReturn(SALT);
	}
}

/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.authentication.epcommerce.transformer.impl;

import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.Test;

import org.hamcrest.Matchers;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.elasticpath.rest.identity.TestRoleConstants;
import com.elasticpath.rest.relos.rs.authentication.epcommerce.transformer.AuthoritiesTransformer;

/**
 * Tests {@link AuthoritiesTransformerImpl}.
 */
public class AuthoritiesTransformerTest {

	private final AuthoritiesTransformer authoritiesTransformer = new AuthoritiesTransformerImpl();

	/**
	 * Test with no authorities.
	 */
	@Test
	public void testWithNoAuthorities() {
		Collection<String> result = authoritiesTransformer.transform(Collections.emptyList());

		assertThat("The result should be empty.", result, Matchers.empty());
	}

	/**
	 * Test with one authority.
	 */
	@Test
	public void testWithOneAuthority() {
		GrantedAuthority roleCustomer = new SimpleGrantedAuthority(TestRoleConstants.REGISTERED);

		Collection<String> result = authoritiesTransformer.transform(Collections.singletonList(roleCustomer));

		assertThat("The result should be as expected.", result, Matchers.contains(TestRoleConstants.REGISTERED));
	}

	/**
	 * Test with multiple authorities.
	 */
	@Test
	public void testWithMultipleAuthorities() {
		GrantedAuthority roleCustomer = new SimpleGrantedAuthority(TestRoleConstants.REGISTERED);
		GrantedAuthority roleAnonymous = new SimpleGrantedAuthority(TestRoleConstants.PUBLIC);

		Collection<String> result = authoritiesTransformer.transform(Arrays.asList(roleCustomer, roleAnonymous));

		assertThat("The result should be as expected", result, Matchers.contains(TestRoleConstants.REGISTERED, TestRoleConstants.PUBLIC));
	}
}

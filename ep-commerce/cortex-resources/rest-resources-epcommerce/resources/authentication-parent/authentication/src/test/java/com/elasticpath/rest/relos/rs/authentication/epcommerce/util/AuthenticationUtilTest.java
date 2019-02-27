/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.authentication.epcommerce.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;

import org.junit.Test;

import org.hamcrest.Matchers;
import org.springframework.security.core.GrantedAuthority;

import com.elasticpath.rest.identity.RolePrincipal;
import com.elasticpath.rest.identity.TestRoleConstants;
import com.elasticpath.rest.util.collection.CollectionUtil;


/**
 * Test that the authentication utilities behave as expected.
 */
public class AuthenticationUtilTest {
	private static final String STORE_CODE = "STORE_CODE";
	private static final String USERNAME = "USERNAME";

	/**
	 * Test create authorities.
	 */
	@Test
	public void testCreateAuthorities() {
		Collection<GrantedAuthority> authorities = AuthenticationUtil.createAuthorities(Collections.singleton(TestRoleConstants.REGISTERED_ROLE));
		assertThat("There should be 1 authority", authorities, Matchers.hasSize(1));
		assertEquals("The authority name should match", TestRoleConstants.REGISTERED, CollectionUtil.first(authorities).getAuthority());
	}

	/**
	 * Test create authorities with an empty collection of principals.
	 */
	@Test
	public void testCreateEmptyAuthorities() {
		Collection<GrantedAuthority> authorities = AuthenticationUtil.createAuthorities(Collections.<RolePrincipal>emptySet());
		assertTrue("The result set should be empty", authorities.isEmpty());
	}

	/**
	 * Test that the scope and username can be combined and split as expected.
	 */
	@Test
	public void testSplitAndCombine() {
		String combined = AuthenticationUtil.combinePrincipals(STORE_CODE, USERNAME);
		String[] splitScopeAndUsername = AuthenticationUtil.splitPrincipals(combined);
		assertEquals("The results should be a 2 element array", 2, splitScopeAndUsername.length);
		assertEquals("The scope should round-trip correctly", STORE_CODE, splitScopeAndUsername[0]);
		assertEquals("The username should round-trip correctly", USERNAME, splitScopeAndUsername[1]);
	}

	/**
	 * Test split with an invalid string.
	 */
	@Test(expected = AssertionError.class)
	public void testSplitInvalidString() {
		AuthenticationUtil.splitPrincipals("invalid");
	}
}

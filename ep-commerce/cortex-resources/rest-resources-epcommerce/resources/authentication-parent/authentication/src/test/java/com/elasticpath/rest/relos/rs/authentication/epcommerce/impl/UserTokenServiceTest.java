/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.authentication.epcommerce.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.springframework.security.core.Authentication;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.relos.rs.authentication.epcommerce.UserTokenService;
import com.elasticpath.rest.relos.rs.authentication.epcommerce.util.AuthenticationUtil;

/**
 * Test that {@link UserTokenServiceImpl} behaves as expected.
 */
public class UserTokenServiceTest {
	private static final String USERNAME = "USERNAME";
	private static final String PASSWORD = "PASSWORD";
	private static final String STORE_CODE = "STORE_CODE";

	private final UserTokenService userTokenService = new UserTokenServiceImpl();

	/**
	 * Test the behaviour of create user authentication token.
	 */
	@Test
	public void testCreateUserAuthenticationToken() {
		ExecutionResult<Authentication> userTokenResult = userTokenService.createUserAuthenticationToken(USERNAME, PASSWORD, STORE_CODE);
		assertTrue("The operation should have been successful", userTokenResult.isSuccessful());

		Authentication userAuthentication = userTokenResult.getData();
		String storeUserName = AuthenticationUtil.combinePrincipals(STORE_CODE, USERNAME);
		assertEquals("The token username should match", storeUserName, userAuthentication.getPrincipal());
		assertEquals("The token password should match", PASSWORD, userAuthentication.getCredentials());
	}
}

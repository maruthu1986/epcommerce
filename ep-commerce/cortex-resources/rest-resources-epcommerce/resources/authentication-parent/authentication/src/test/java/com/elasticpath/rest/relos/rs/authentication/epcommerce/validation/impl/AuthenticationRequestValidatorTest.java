/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.authentication.epcommerce.validation.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.identity.TestRoleConstants;
import com.elasticpath.rest.relos.rs.authentication.dto.AuthenticationRequestDto;
import com.elasticpath.rest.relos.rs.authentication.epcommerce.validation.AuthenticationRequestValidator;

/**
 * Tests {@link AuthenticationRequestValidatorImpl}.
 */
public class AuthenticationRequestValidatorTest {

	private static final String EXPECTED_MESSAGE = "The resulting error message should be as expected";
	private static final String EXPECTED_RESOURCE_STATUS = "The result should have the expected resource status.";
	private static final String FAILURE_RESULT = "This should result in a failure.";
	private static final String USERNAME = "USERNAME";
	private static final String PASSWORD = "PASSWORD";
	private static final String SCOPE = "SCOPE";
	private static final String IS_MISSING = "%s is missing.";
	private static final String ARE_MISSING = "%s, %s are missing.";
	private static final String ARE_EMPTY = "%s, %s, %s are missing.";
	private static final String IS_NOT_EMPTY = "%s should be empty for public users.";

	private final AuthenticationRequestValidator validator = new AuthenticationRequestValidatorImpl();

	/**
	 * Test valid fields for registered user.
	 */
	@Test
	public void testValidFieldsForRegisteredUser() {
		ExecutionResult<Void> result = validator.validateRegisteredUserRequest(SCOPE, USERNAME, PASSWORD, TestRoleConstants.REGISTERED);

		assertTrue("This should be a successful result.", result.isSuccessful());
		assertEquals(EXPECTED_RESOURCE_STATUS, ResourceStatus.UPDATE_OK, result.getResourceStatus());
	}

	/**
	 * Test missing username for registered user.
	 */
	@Test
	public void testMissingUsernameForRegisteredUser() {
		ExecutionResult<Void> result = validator.validateRegisteredUserRequest(SCOPE, null, PASSWORD, TestRoleConstants.REGISTERED);

		assertTrue(FAILURE_RESULT, result.isFailure());
		assertEquals(EXPECTED_RESOURCE_STATUS, ResourceStatus.BAD_REQUEST_BODY, result.getResourceStatus());
		assertEquals(EXPECTED_MESSAGE,
				String.format(IS_MISSING, StringUtils.capitalize(AuthenticationRequestDto.USERNAME_PROPERTY)),
				result.getErrorMessage());
	}

	/**
	 * Test missing password for registered user.
	 */
	@Test
	public void testMissingPasswordForRegisteredUser() {
		ExecutionResult<Void> result = validator.validateRegisteredUserRequest(SCOPE, USERNAME, null, TestRoleConstants.REGISTERED);

		assertTrue(FAILURE_RESULT, result.isFailure());
		assertEquals(EXPECTED_RESOURCE_STATUS, ResourceStatus.BAD_REQUEST_BODY, result.getResourceStatus());
		assertEquals(EXPECTED_MESSAGE,
				String.format(IS_MISSING, StringUtils.capitalize(AuthenticationRequestDto.PASSWORD_PROPERTY)),
				result.getErrorMessage());
	}

	/**
	 * Test missing scope for registered user.
	 */
	@Test
	public void testMissingScopeForRegisteredUser() {
		ExecutionResult<Void> result = validator.validateRegisteredUserRequest(null, USERNAME, PASSWORD, TestRoleConstants.REGISTERED);

		assertTrue(FAILURE_RESULT, result.isFailure());
		assertEquals(EXPECTED_RESOURCE_STATUS, ResourceStatus.BAD_REQUEST_BODY, result.getResourceStatus());
		assertEquals(EXPECTED_MESSAGE,
				String.format(IS_MISSING, StringUtils.capitalize(AuthenticationRequestDto.SCOPE_PROPERTY)),
				result.getErrorMessage());
	}

	/**
	 * Test missing role for registered user.
	 */
	@Test
	public void testMissingRoleForRegisteredUser() {
		ExecutionResult<Void> result = validator.validateRegisteredUserRequest(SCOPE, USERNAME, PASSWORD, null);

		assertTrue(FAILURE_RESULT, result.isFailure());
		assertEquals(EXPECTED_RESOURCE_STATUS, ResourceStatus.BAD_REQUEST_BODY, result.getResourceStatus());
		assertEquals(EXPECTED_MESSAGE,
				String.format(IS_MISSING, StringUtils.capitalize(AuthenticationRequestDto.ROLE_PROPERTY)),
				result.getErrorMessage());
	}

	/**
	 * Test missing multiple fields for registered user.
	 */
	@Test
	public void testMissingMultipleFieldsForRegisteredUser() {
		ExecutionResult<Void> result = validator.validateRegisteredUserRequest(null, null, PASSWORD, TestRoleConstants.REGISTERED);

		assertTrue(FAILURE_RESULT, result.isFailure());
		assertEquals(EXPECTED_RESOURCE_STATUS, ResourceStatus.BAD_REQUEST_BODY, result.getResourceStatus());
		assertEquals(EXPECTED_MESSAGE,
				String.format(ARE_MISSING, StringUtils.capitalize(AuthenticationRequestDto.USERNAME_PROPERTY),
						AuthenticationRequestDto.SCOPE_PROPERTY),
				result.getErrorMessage());
	}

	/**
	 * Test empty fields for registered user.
	 */
	@Test
	public void testEmptyFieldsForRegisteredUser() {
		ExecutionResult<Void> result = validator.validateRegisteredUserRequest(StringUtils.EMPTY, StringUtils.EMPTY, PASSWORD, StringUtils.EMPTY);

		assertTrue(FAILURE_RESULT, result.isFailure());
		assertEquals(EXPECTED_RESOURCE_STATUS, ResourceStatus.BAD_REQUEST_BODY, result.getResourceStatus());
		assertEquals(EXPECTED_MESSAGE,
				String.format(ARE_EMPTY, StringUtils.capitalize(AuthenticationRequestDto.USERNAME_PROPERTY),
						AuthenticationRequestDto.SCOPE_PROPERTY, AuthenticationRequestDto.ROLE_PROPERTY),
				result.getErrorMessage());
	}

	/**
	 * Test valid representation for anonymous user.
	 */
	@Test
	public void testValidRepresentationForAnonymousUser() {
		ExecutionResult<Void> result = validator.validateAnonymousUserRequest(SCOPE, StringUtils.EMPTY, StringUtils.EMPTY, TestRoleConstants.PUBLIC);

		assertTrue("This should be a successful result.", result.isSuccessful());
		assertEquals(EXPECTED_RESOURCE_STATUS, ResourceStatus.UPDATE_OK, result.getResourceStatus());
	}

	/**
	 * Test representation is valid when username and password are null for anonymous users.
	 */
	@Test
	public void testValidRequestWhenUsernameAndPasswordAreNullForAnonymousUser() {
		ExecutionResult<Void> result = validator.validateAnonymousUserRequest(SCOPE, null, null, TestRoleConstants.PUBLIC);

		assertTrue("This should be a successful result.", result.isSuccessful());
		assertEquals(EXPECTED_RESOURCE_STATUS, ResourceStatus.UPDATE_OK, result.getResourceStatus());
	}

	/**
	 * Test missing scope for anonymous user.
	 */
	@Test
	public void testMissingScopeForAnonymousUser() {
		ExecutionResult<Void> result = validator.validateAnonymousUserRequest(null, USERNAME, PASSWORD, TestRoleConstants.PUBLIC);

		assertTrue(FAILURE_RESULT, result.isFailure());
		assertEquals(EXPECTED_RESOURCE_STATUS, ResourceStatus.BAD_REQUEST_BODY, result.getResourceStatus());
		assertEquals(EXPECTED_MESSAGE,
				String.format(IS_MISSING, StringUtils.capitalize(AuthenticationRequestDto.SCOPE_PROPERTY)),
				result.getErrorMessage());
	}

	/**
	 * Test missing role for anonymous user.
	 */
	@Test
	public void testMissingRoleForAnonymousUser() {
		ExecutionResult<Void> result = validator.validateAnonymousUserRequest(SCOPE, USERNAME, PASSWORD, null);

		assertTrue(FAILURE_RESULT, result.isFailure());
		assertEquals(EXPECTED_RESOURCE_STATUS, ResourceStatus.BAD_REQUEST_BODY, result.getResourceStatus());
		assertEquals(EXPECTED_MESSAGE,
				String.format(IS_MISSING, StringUtils.capitalize(AuthenticationRequestDto.ROLE_PROPERTY)),
				result.getErrorMessage());
	}

	/**
	 * Test missing multiple fields for anonymous user.
	 */
	@Test
	public void testMissingMultipleFieldsForAnonymousUser() {
		ExecutionResult<Void> result = validator.validateAnonymousUserRequest(null, StringUtils.EMPTY, StringUtils.EMPTY, null);

		assertTrue(FAILURE_RESULT, result.isFailure());
		assertEquals(EXPECTED_RESOURCE_STATUS, ResourceStatus.BAD_REQUEST_BODY, result.getResourceStatus());
		assertEquals(EXPECTED_MESSAGE,
				String.format(ARE_MISSING, StringUtils.capitalize(AuthenticationRequestDto.SCOPE_PROPERTY),
						AuthenticationRequestDto.ROLE_PROPERTY),
				result.getErrorMessage());
	}

	/**
	 * Test not empty password for anonymous user.
	 */
	@Test
	public void testNotEmptyPasswordForAnonymousUser() {
		ExecutionResult<Void> result = validator.validateAnonymousUserRequest(SCOPE, StringUtils.EMPTY, PASSWORD, TestRoleConstants.PUBLIC);

		assertTrue(FAILURE_RESULT, result.isFailure());
		assertEquals(EXPECTED_RESOURCE_STATUS, ResourceStatus.BAD_REQUEST_BODY, result.getResourceStatus());
		assertEquals(EXPECTED_MESSAGE,
				String.format(IS_NOT_EMPTY, StringUtils.capitalize(AuthenticationRequestDto.PASSWORD_PROPERTY)),
				result.getErrorMessage());
	}

	/**
	 * Test not empty username for anonymous user.
	 */
	@Test
	public void testNotEmptyUsernameForAnonymousUser() {
		ExecutionResult<Void> result = validator.validateAnonymousUserRequest(SCOPE, USERNAME, StringUtils.EMPTY, TestRoleConstants.PUBLIC);

		assertTrue(FAILURE_RESULT, result.isFailure());
		assertEquals(EXPECTED_RESOURCE_STATUS, ResourceStatus.BAD_REQUEST_BODY, result.getResourceStatus());
		assertEquals(EXPECTED_MESSAGE,
				String.format(IS_NOT_EMPTY, StringUtils.capitalize(AuthenticationRequestDto.USERNAME_PROPERTY)),
				result.getErrorMessage());
	}
}

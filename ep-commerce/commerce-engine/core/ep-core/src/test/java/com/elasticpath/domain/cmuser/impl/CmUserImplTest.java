/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.cmuser.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.jmock.Expectations;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.security.authentication.encoding.PasswordEncoder;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.EpPasswordValidationException;
import com.elasticpath.commons.security.CmPasswordPolicy;
import com.elasticpath.commons.security.PasswordPolicy;
import com.elasticpath.commons.security.ValidationError;
import com.elasticpath.commons.security.ValidationResult;
import com.elasticpath.domain.cmuser.UserPasswordHistoryItem;
import com.elasticpath.domain.cmuser.UserPermission;
import com.elasticpath.domain.cmuser.UserRole;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.test.jmock.AbstractEPTestCase;

/**
 * Test of the public API of <code>CmUserImpl</code>.
 */

public class CmUserImplTest extends AbstractEPTestCase {

	private CmUserImpl cmUserImpl;

	private PasswordEncoder mockPasswordEncoder;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	/**
	 * Prepares for the next test.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Override
	@SuppressWarnings("PMD.DontUseElasticPathImplGetInstance")
	public void setUp() throws Exception {
		super.setUp();
		this.cmUserImpl = new CmUserImpl();
		this.cmUserImpl.setUserRoles(new HashSet<>());
		
		stubGetBean("CSR", UserPermissionImpl.class);
		
		this.mockPasswordEncoder = context.mock(PasswordEncoder.class);
		stubGetBean(ContextIdNames.CM_PASSWORDENCODER, mockPasswordEncoder);

		stubGetBean("userPasswordHistoryItem", UserPasswordHistoryItemImpl.class);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CmUserImpl.getUserName()'.
	 */
	@Test
	public void testGetSetUserName() {
		assertEquals(cmUserImpl.getUsername(), null);

		final String userName = "userName";
		cmUserImpl.setUserName(userName);
		assertSame(userName, cmUserImpl.getUsername());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CmUserImpl.setEmail(String)'.
	 */
	@Test
	public void testSetEmail() {
		final String[] testData = new String[] { "aaaa@aaa.aaa", "", null };
		for (final String email : testData) {
			cmUserImpl.setEmail(email);
			assertSame("Check set email", email, cmUserImpl.getEmail());
		}
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CmUserImpl.getFirstName()'.
	 */
	@Test
	public void testGetSetFirstName() {
		assertEquals(cmUserImpl.getFirstName(), null);

		final String firstName = "first";
		cmUserImpl.setFirstName(firstName);
		assertSame(firstName, cmUserImpl.getFirstName());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CmUserImpl.getLastName()'.
	 */
	@Test
	public void testGetSetLastName() {
		assertEquals(cmUserImpl.getLastName(), null);

		final String firstName = "first";
		cmUserImpl.setLastName(firstName);
		assertSame(firstName, cmUserImpl.getLastName());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CmUserImpl.hasUserRole()'.
	 */
	@Test
	public void testHasUserRole() {
		final long userRoleId = 1000L;
		final long invalidUserRoleId = 1001L;
		final String testRoleName = "TestRole";
		final String invalidTestRoleName = "InvalidTestRole";

		final UserRole userRole = new UserRoleImpl();
		userRole.setUidPk(userRoleId);
		userRole.setName(testRoleName);

		cmUserImpl.addUserRole(userRole);
		assertTrue(cmUserImpl.hasUserRole(userRoleId));
		assertTrue(cmUserImpl.hasUserRole(testRoleName));

		assertFalse(cmUserImpl.hasUserRole(invalidTestRoleName));
		assertFalse(cmUserImpl.hasUserRole(invalidUserRoleId));
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CmUserImpl.hasPermission()'.
	 */
	@Test
	public void testHasPermission() {
		final long userRoleId = 1000L;
		final String testRoleName = "TestRole";
		final String authority = "CSR";
		final String invalidAuthority = "Invalid";

		final UserRole userRole = new UserRoleImpl();
		userRole.setUidPk(userRoleId);
		userRole.setName(testRoleName);

		final UserPermission userPermission = new UserPermissionImpl();
		userPermission.setAuthority(authority);
		Set<UserPermission> userPermissions = new HashSet<>();
		userPermissions.add(userPermission);
		userRole.setUserPermissions(userPermissions);
		cmUserImpl.addUserRole(userRole);

		assertTrue(cmUserImpl.hasPermission(authority));
		assertFalse(cmUserImpl.hasPermission(invalidAuthority));
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CmUserImpl.getPassword()'.
	 */
	@Test
	public void testGetPassword() {
		assertEquals(null, cmUserImpl.getPassword());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CmUserImpl.getPassword()'.
	 */
	@Test
	public void testGetEncryptedPassword() {
		assertEquals(cmUserImpl.getPassword(), null);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CmUserImpl.setClearTextPassword(String)'.
	 */
	@Test
	public void testSetClearTextPassword() {
		final String[] passwords = new String[] { "AbCdEfGhI", "AbCdEfGhIjKlMnOpQrS", "aA123_$@#^&", "", null };
		final String[] hashedPasswords = new String[] { "d60c7aaba158d8270ec509390438152ca931ec6a", "32a6ea3419c4d9653cf51c6500f3accef2012ab0",
				"e9d1d12fbb45ca95c496f3a33a40956c1a4da1ef", "adc83b19e793491b1c6ea0fd8b46cd9f32e592fc", null };

		for (int i = 0; i < passwords.length; i++) {
			final String password = passwords[i];
			final String hashedPassword = hashedPasswords[i];
		context.checking(new Expectations() {
			{
			allowing(mockPasswordEncoder).encodePassword(password, null);
			will(returnValue(hashedPassword));
			}
		});
			cmUserImpl.setClearTextPassword(password);
			cmUserImpl.setPasswordUsingPasswordEncoder(password);
			assertEquals(hashedPassword, cmUserImpl.getPassword());
		}
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CmUserImpl.getConfirmClearTextPassword()'.
	 */
	@Test
	public void testGetConfirmClearTextPassword() {
		assertEquals(null, cmUserImpl.getConfirmClearTextPassword());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CmUserImpl.setLastName(String)'.
	 */
	@Test
	public void testSetConfirmClearTextPassword() {
		final String[] passwords = new String[] { "AbCdEfGhI", "AbCdEfGhIjKlMnOpQrS", "aA123_$@#^&", "", null };
		for (final String password : passwords) {
			cmUserImpl.setConfirmClearTextPassword(password);
			assertSame(password, cmUserImpl.getConfirmClearTextPassword());
		}
	}

	/** 
	 * Test that isCmAccess returns true when a user has the CMUSER role. 
	 * @throws Exception on error.
	 */
	@Test
	public void testIsCmAccessTrue() throws Exception {
		UserRole cmUserRole = new UserRoleImpl();
		cmUserRole.setUserPermissions(new HashSet<>());
		cmUserRole.setName(UserRole.CMUSER);
		cmUserImpl.addUserRole(cmUserRole);
		assertTrue(cmUserImpl.isCmAccess());
	}
	
	/** 
	 * Test that isCmAccess returns false when a user does not have the CMUSER role. 
	 * @throws Exception on error.
	 */
	@Test
	public void testIsCmAccessFalse() throws Exception {
		UserRole wsUserRole = new UserRoleImpl();
		wsUserRole.setUserPermissions(new HashSet<>());
		wsUserRole.setName(UserRole.WSUSER);
		cmUserImpl.addUserRole(wsUserRole);
		assertFalse(cmUserImpl.isCmAccess());
	}
	
	/** 
	 * Test that isWsAccess returns true when a user has the WSUSER role. 
	 * @throws Exception on error.
	 */
	@Test
	public void testIsWsAccessTrue() throws Exception {
		UserRole wsUserRole = new UserRoleImpl();
		wsUserRole.setUserPermissions(new HashSet<>());
		wsUserRole.setName(UserRole.WSUSER);
		cmUserImpl.addUserRole(wsUserRole);
		assertTrue(cmUserImpl.isWsAccess());
	}
	
	/** 
	 * Test that isWsAccess returns false when a user does not have the WSUSER role. 
	 * @throws Exception on error.
	 */
	@Test
	public void testIsWsAccessFalse() throws Exception {
		UserRole cmUserRole = new UserRoleImpl();
		cmUserRole.setUserPermissions(new HashSet<>());
		cmUserRole.setName(UserRole.CMUSER);
		cmUserImpl.addUserRole(cmUserRole);
		assertFalse(cmUserImpl.isWsAccess());
	}

	@Test
	public void testIsPasswordExpiredTrueWhenMaxAgePasswordPolicyInvalid() {
		final PasswordPolicy maximumAgePasswordPolicy = context.mock(PasswordPolicy.class, "maxAgePasswordPolicy");

		stubGetBean("maximumAgePasswordPolicy", maximumAgePasswordPolicy);

		context.checking(new Expectations() {
			{
				final ValidationResult validationResult = new ValidationResult();
				validationResult.addError(new ValidationError("foo"));

				oneOf(maximumAgePasswordPolicy).validate(cmUserImpl);
				will(returnValue(validationResult));
			}
		});

		assertTrue("Expected password to be expired when the max age password policy is invalid",
				   cmUserImpl.isPasswordExpired());
	}

	@Test
	public void testIsPasswordExpiredFalseWhenMaxAgePasswordPolicyValid() {
		final PasswordPolicy maximumAgePasswordPolicy = context.mock(PasswordPolicy.class, "maxAgePasswordPolicy");

		stubGetBean("maximumAgePasswordPolicy", maximumAgePasswordPolicy);

		context.checking(new Expectations() {
			{
				oneOf(maximumAgePasswordPolicy).validate(cmUserImpl);
				will(returnValue(new ValidationResult()));
			}
		});

		assertFalse("Expected password not to be expired when the max age password policy is valid",
					cmUserImpl.isPasswordExpired());
	}

	@Test
	public void verifySetCheckedClearTextPasswordFailsWhenValidatorRejects() throws Exception {
		cmUserImpl.setPasswordHistoryItems(Collections.<UserPasswordHistoryItem>emptyList());

		final CmPasswordPolicy cmPasswordPolicy = context.mock(CmPasswordPolicy.class);

		stubGetBean("cmPasswordPolicy", cmPasswordPolicy);

		context.checking(new Expectations() {
			{
				final ValidationResult validationResult = new ValidationResult();
				validationResult.addError(new ValidationError("Invalid"));

				oneOf(cmPasswordPolicy).validate(cmUserImpl);
				will(returnValue(validationResult));
			}
		});

		thrown.expect(EpPasswordValidationException.class);
		thrown.expectMessage("Password didn't pass validation");

		cmUserImpl.setCheckedClearTextPassword("foo");
	}

	@Test
	public void verifySetCheckedClearTextPasswordSucceedsWhenValidatorAccepts() throws Exception {
		final String newPassword = "new password";
		final String newPasswordEncoded = "new encoded password";

		cmUserImpl.setPasswordHistoryItems(Collections.<UserPasswordHistoryItem>emptyList());

		final CmPasswordPolicy cmPasswordPolicy = context.mock(CmPasswordPolicy.class);

		givenTimeServiceReturnsCurrentTime();
		stubGetBean("cmPasswordPolicy", cmPasswordPolicy);

		context.checking(new Expectations() {
			{
				allowing(mockPasswordEncoder).encodePassword(newPassword, null);
				will(returnValue(newPasswordEncoded));

				oneOf(cmPasswordPolicy).validate(cmUserImpl);
				will(returnValue(new ValidationResult()));
			}
		});

		cmUserImpl.setCheckedClearTextPassword(newPassword);

		assertEquals("New password should be set successfully", newPasswordEncoded, cmUserImpl.getPassword());
	}

	/**
	 * Tests that the new password is moved to history right after setting it.
	 */
	@Test
	public void testSetCheckedClearTextPassword3() {
		final String initialPassword = "initialPassword";

		final CmPasswordPolicy cmPasswordPolicy = context.mock(CmPasswordPolicy.class);

		givenTimeServiceReturnsCurrentTime();
		stubGetBean("cmPasswordPolicy", cmPasswordPolicy);

		context.checking(new Expectations() {
			{
				allowing(mockPasswordEncoder).encodePassword(initialPassword, null);
				will(returnValue("encodedInitialPassword"));
			}
		});
		cmUserImpl.setClearTextPassword(initialPassword);
		cmUserImpl.setPasswordUsingPasswordEncoder(initialPassword);

		assertEquals("There should be 0 passwords in history after first login.",
				0, cmUserImpl.getPasswordHistoryItems().size());

		final String oldPassword = cmUserImpl.getPassword();
		final String newPassword = "newPassword1";
		final String encodedNewPassword = "encodedNewPassword1";
		context.checking(new Expectations() {
			{
				allowing(mockPasswordEncoder).encodePassword(newPassword, null);
				will(returnValue(encodedNewPassword));

				allowing(cmPasswordPolicy).validate(cmUserImpl);
				will(returnValue(new ValidationResult()));

				final int passwordHistoryMinimumLength = 3;
				allowing(cmPasswordPolicy).getPasswordHistoryLength();
				will(returnValue(passwordHistoryMinimumLength));
			}
		});
		cmUserImpl.setCheckedClearTextPassword(newPassword);
		assertEquals("After changing password old should be moved to history",
				1, cmUserImpl.getPasswordHistoryItems().size());
		assertEquals("Password in history should be equal to changed password",
					 oldPassword, cmUserImpl.getPasswordHistoryItems().get(0).getOldPassword());
	}

	private void givenTimeServiceReturnsCurrentTime() {
		final TimeService timeService = context.mock(TimeService.class);

		context.checking(new Expectations() {
			{
				allowing(timeService).getCurrentTime();
				will(returnValue(new Date()));
			}
		});

		stubGetBean("timeService", timeService);
	}

}
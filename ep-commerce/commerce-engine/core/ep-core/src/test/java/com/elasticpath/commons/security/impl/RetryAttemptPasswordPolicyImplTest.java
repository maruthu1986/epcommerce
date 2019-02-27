/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.commons.security.impl;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.security.PasswordHolder;
import com.elasticpath.commons.security.PasswordPolicy;
import com.elasticpath.commons.security.ValidationResult;
import com.elasticpath.settings.test.support.SimpleSettingValueProvider;

/**
 * Test <code>RetryAttemptPasswordPolicyImpl</code>.
 */
public class RetryAttemptPasswordPolicyImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private static final int DEFAULT_ACCOUNT_LOCKOUT_THRESHOLD = 4;

	/**
	 * Test method for valid retry attempt.
	 */
	@Test
	public void testValidRetryAttempt() {
		PasswordPolicy passwordPolicy = createRetryAttemptPasswordPolicy(DEFAULT_ACCOUNT_LOCKOUT_THRESHOLD);
		PasswordHolder passwordHolder = createMockPasswordHolder(DEFAULT_ACCOUNT_LOCKOUT_THRESHOLD - 1);

		ValidationResult result = passwordPolicy.validate(passwordHolder);
		assertFalse(result.containsError(RetryAttemptPasswordPolicyImpl.PASSWORD_VALIDATION_ERROR_MAXIMUM_RETRY_ATTEMPTS));
		assertTrue(result.isValid());
	}

	/**
	 * Test method for invalid retry attempt.
	 */
	@Test
	public void testinValidRetryAttempt() {
		PasswordPolicy passwordPolicy = createRetryAttemptPasswordPolicy(DEFAULT_ACCOUNT_LOCKOUT_THRESHOLD);
		PasswordHolder passwordHolder = createMockPasswordHolder(DEFAULT_ACCOUNT_LOCKOUT_THRESHOLD);

		ValidationResult result = passwordPolicy.validate(passwordHolder);
		assertTrue(result.containsError(RetryAttemptPasswordPolicyImpl.PASSWORD_VALIDATION_ERROR_MAXIMUM_RETRY_ATTEMPTS));
		assertFalse(result.isValid());
	}

	private RetryAttemptPasswordPolicyImpl createRetryAttemptPasswordPolicy(final int accountLockoutThreshold) {
		final RetryAttemptPasswordPolicyImpl passwordPolicy = new RetryAttemptPasswordPolicyImpl();
		passwordPolicy.setMaximumFailedLoginAttemptsProvider(new SimpleSettingValueProvider<>(accountLockoutThreshold));
		return passwordPolicy;
	}

	private PasswordHolder createMockPasswordHolder(final int failedLoginAttempts) {
		final PasswordHolder mockPasswordHolder = context.mock(PasswordHolder.class);
		context.checking(new Expectations() {
			{
				allowing(mockPasswordHolder).getFailedLoginAttempts();
				will(returnValue(failedLoginAttempts));
			}
		});
		return mockPasswordHolder;
	}

}

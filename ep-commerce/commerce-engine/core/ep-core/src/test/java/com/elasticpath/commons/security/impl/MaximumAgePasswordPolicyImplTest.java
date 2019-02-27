/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.commons.security.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.security.PasswordHolder;
import com.elasticpath.commons.security.PasswordPolicy;
import com.elasticpath.commons.security.ValidationResult;
import com.elasticpath.domain.cmuser.impl.CmUserImpl;
import com.elasticpath.settings.test.support.SimpleSettingValueProvider;

/**
 * Test <code>MaximumAgePasswordPolicyImpl</code>.
 */
public class MaximumAgePasswordPolicyImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private static final int DEFAULT_MAXIMUM_PASSWORD_AGE = 90;

	private final PasswordPolicy passwordPolicy = createMaximumAgePasswordPolicy(DEFAULT_MAXIMUM_PASSWORD_AGE);

	/**
	 * Test method for valid maximum password age.
	 */
	@Test
	public void testValidRetryAttempt() {
		Calendar calendar = Calendar.getInstance();
		Date lastLoginDate = calendar.getTime();
		calendar.add(Calendar.DATE, -DEFAULT_MAXIMUM_PASSWORD_AGE + 1);
		Date lastChangedPasswordDate = calendar.getTime();
		PasswordHolder passwordHolder = createMockPasswordHolder(lastChangedPasswordDate, lastLoginDate);

		ValidationResult result = passwordPolicy.validate(passwordHolder);
		assertFalse(result.containsError(MaximumAgePasswordPolicyImpl.PASSWORD_VALIDATION_ERROR_MAXIMUM_PASSWORD_AGE));
		assertTrue(result.isValid());
	}

	/**
	 * Test method for invalid maximum password age.
	 */
	@Test
	public void testinValidRetryAttempt() {
		Calendar calendar = Calendar.getInstance();
		Date lastLoginDate = calendar.getTime();
		calendar.add(Calendar.DATE, -DEFAULT_MAXIMUM_PASSWORD_AGE);
		Date lastChangedPasswordDate = calendar.getTime();
		PasswordHolder passwordHolder = createMockPasswordHolder(lastChangedPasswordDate, lastLoginDate);

		ValidationResult result = passwordPolicy.validate(passwordHolder);
		assertTrue(result.containsError(MaximumAgePasswordPolicyImpl.PASSWORD_VALIDATION_ERROR_MAXIMUM_PASSWORD_AGE));
		assertFalse(result.isValid());
	}

	/**
	 * Test the password expiration logic.
	 */
	@Test
	public void tesCmUserPasswordExpired() {
		final Date date = new Date();

		CmUserImpl cmUser = new CmUserImpl();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, -DEFAULT_MAXIMUM_PASSWORD_AGE + 1);
		cmUser.setLastChangedPasswordDate(calendar.getTime());
		cmUser.setLastLoginDate(date);

		assertTrue("the password should be expired next day", passwordPolicy.validate(cmUser).isValid());

		calendar.add(Calendar.DATE, -1);
		cmUser.setLastChangedPasswordDate(calendar.getTime());
		cmUser.setLastLoginDate(date);
		assertFalse("the password expired today", passwordPolicy.validate(cmUser).isValid());

		calendar.add(Calendar.DATE, -1);
		cmUser.setLastChangedPasswordDate(calendar.getTime());
		cmUser.setLastLoginDate(date);
		assertFalse("the password was expired 1 day ago", passwordPolicy.validate(cmUser).isValid());
	}

	private MaximumAgePasswordPolicyImpl createMaximumAgePasswordPolicy(final int maximumPasswordAge) {
		final MaximumAgePasswordPolicyImpl passwordPolicy = new MaximumAgePasswordPolicyImpl();
		passwordPolicy.setMaximumPasswordAgeDaysProvider(new SimpleSettingValueProvider<>(maximumPasswordAge));
		return passwordPolicy;
	}

	private PasswordHolder createMockPasswordHolder(final Date lastChangedPasswordDate, final Date lastLoginDate) {
		final PasswordHolder mockPasswordHolder = context.mock(PasswordHolder.class);
		context.checking(new Expectations() {
			{
				allowing(mockPasswordHolder).getLastChangedPasswordDate();
				will(returnValue(lastChangedPasswordDate));

				allowing(mockPasswordHolder).getLastLoginDate();
				will(returnValue(lastLoginDate));
			}
		});
		return mockPasswordHolder;
	}

}

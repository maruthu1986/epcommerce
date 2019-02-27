/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.commons.security.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.encoding.PasswordEncoder;

import com.elasticpath.commons.security.PasswordHolder;
import com.elasticpath.commons.security.ValidationResult;
import com.elasticpath.domain.cmuser.UserPasswordHistoryItem;
import com.elasticpath.settings.test.support.SimpleSettingValueProvider;

/**
 * Test class for {@link com.elasticpath.commons.security.impl.CmPasswordPolicyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CmPasswordPolicyImplTest {

	@Mock
	private PasswordEncoder mockPasswordEncoder;

	@Mock
	private PasswordHolder passwordHolder;

	@InjectMocks
	private CmPasswordPolicyImpl cmPasswordPolicy;

	@Before
	public void setUp() {
		// Sensible defaults
		cmPasswordPolicy.setMinimumPasswordLengthProvider(new SimpleSettingValueProvider<>(1));
		cmPasswordPolicy.setMinimumPasswordHistoryLengthDaysProvider(new SimpleSettingValueProvider<>(1));
	}

	@Test
	public void verifyGetPasswordHistoryLengthUsesProvider() {
		final int minimumPasswordHistoryDays = 9;

		cmPasswordPolicy.setMinimumPasswordHistoryLengthDaysProvider(new SimpleSettingValueProvider<>(minimumPasswordHistoryDays));

		assertThat(cmPasswordPolicy.getPasswordHistoryLength())
				.as("Unexpected minimum password history length value")
				.isEqualTo(minimumPasswordHistoryDays);
	}

	@Test
	public void verifyPasswordIsInvalidWhenAttemptingToSetSamePasswordAgain() {
		final String currentPassword = "password1";
		final String encodedPassword = "encodedPassword";

		when(mockPasswordEncoder.encodePassword(currentPassword, null)).thenReturn(encodedPassword);
		when(passwordHolder.getUserPassword()).thenReturn(currentPassword);
		when(passwordHolder.getPassword()).thenReturn(encodedPassword);

		final ValidationResult validationResult = cmPasswordPolicy.validate(passwordHolder);

		assertThat(validationResult.isValid())
				.as("Cannot set new password equal to current password")
				.isFalse();

		assertThat(validationResult.containsError("PasswordValidationError_MinimumNoRepeatPassword"))
				.as("Expected error key not found")
				.isTrue();
	}

	@Test
	public void verifyPasswordIsInvalidWhenReusingPasswordTooRecentlyInHistory() {
		final String currentPasswordEncoded = "currentPasswordEncoded";
		final String newPassword = "newPassword";
		final String newPasswordEncoded = "newPasswordEncoded";

		when(mockPasswordEncoder.encodePassword(newPassword, null)).thenReturn(newPasswordEncoded);

		final UserPasswordHistoryItem formerPassword1 = mock(UserPasswordHistoryItem.class);
		final UserPasswordHistoryItem formerPassword2 = mock(UserPasswordHistoryItem.class);

		when(formerPassword1.getOldPassword()).thenReturn("oldPassword1");
		when(formerPassword2.getOldPassword()).thenReturn(newPasswordEncoded);

		when(passwordHolder.getPassword()).thenReturn(currentPasswordEncoded);
		when(passwordHolder.getUserPassword()).thenReturn(newPassword);
		when(passwordHolder.getPasswordHistoryItems()).thenReturn(ImmutableList.of(formerPassword1, formerPassword2));

		// Our minimum password history is set to three, but we used the current password two passwords ago...
		final int minimumPasswordHistoryDays = 3;
		cmPasswordPolicy.setMinimumPasswordHistoryLengthDaysProvider(new SimpleSettingValueProvider<>(minimumPasswordHistoryDays));

		final ValidationResult validationResult = cmPasswordPolicy.validate(passwordHolder);

		assertThat(validationResult.isValid())
				.as("Password should not be valid when reused within minimum history threshold")
				.isFalse();

		assertThat(validationResult.containsError("PasswordValidationError_MinimumNoRepeatPassword"))
				.as("Expected error key not found")
				.isTrue();
	}

}
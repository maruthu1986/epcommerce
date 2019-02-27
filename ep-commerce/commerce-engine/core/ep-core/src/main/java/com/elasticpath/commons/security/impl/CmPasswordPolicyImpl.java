/*
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.commons.security.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.security.authentication.encoding.PasswordEncoder;

import com.elasticpath.commons.security.CmPasswordPolicy;
import com.elasticpath.commons.security.PasswordHolder;
import com.elasticpath.commons.security.ValidationError;
import com.elasticpath.commons.security.ValidationResult;
import com.elasticpath.settings.provider.SettingValueProvider;

/**
 * Implementation of <code>CmPasswordPolicy</code> that validates the password for <code>CmUser</code>.
 */
public class CmPasswordPolicyImpl extends AbstractPasswordPolicyImpl implements CmPasswordPolicy {

	private final List<Function<PasswordHolder, ValidationResult>> passwordPolicies;

	private PasswordEncoder passwordEncoder;

	private SettingValueProvider<Integer> minimumPasswordHistoryLengthDaysProvider;

	/**
	 * Constructs the instance of <code>CmPasswordPolicy</code>.
	 */
	public CmPasswordPolicyImpl() {
		super();

		final Supplier<Integer> minimumPasswordLengthSupplier = this::getMinimumPasswordLength;
		final Supplier<Integer> passwordHistoryLengthSupplier = this::getPasswordHistoryLength;
		final Supplier<PasswordEncoder> passwordEncoderSupplier = this::getPasswordEncoder;

		passwordPolicies = new ArrayList<>();
		passwordPolicies.add(new MinimumPasswordLengthPolicy(minimumPasswordLengthSupplier));
		passwordPolicies.add(new ContainsAlphaNumericPasswordPolicy());
		passwordPolicies.add(new MinimumUniquePasswordPolicy(passwordEncoderSupplier, passwordHistoryLengthSupplier));
	}

	@Override
	public ValidationResult validate(final PasswordHolder passwordHolder) {
		return validate(passwordHolder, passwordPolicies);
	}

	private ValidationResult validate(final PasswordHolder passwordHolder, final Collection<Function<PasswordHolder, ValidationResult>> policies) {
		final List<ValidationResult> validationResults = policies.stream()
				.map(policy -> policy.apply(passwordHolder))
				.collect(Collectors.toList());

		final ValidationResult result = new ValidationResult();
		result.assembleResult(validationResults);

		return result;
	}

	protected PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	public void setPasswordEncoder(final PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public int getPasswordHistoryLength() {
		return getMinimumPasswordHistoryLengthDaysProvider().get();
	}

	public void setMinimumPasswordHistoryLengthDaysProvider(final SettingValueProvider<Integer> minimumPasswordHistoryLengthDaysProvider) {
		this.minimumPasswordHistoryLengthDaysProvider = minimumPasswordHistoryLengthDaysProvider;
	}

	protected SettingValueProvider<Integer> getMinimumPasswordHistoryLengthDaysProvider() {
		return minimumPasswordHistoryLengthDaysProvider;
	}

	/**
	 * Password validation policy that enforces a minimum password length.
	 */
	protected static class MinimumPasswordLengthPolicy implements Function<PasswordHolder, ValidationResult> {

		/**
		 * The key of the message used when a password violates the policy.
		 */
		protected static final String PASSWORD_VALIDATION_ERROR_MINIMUM_LENGTH = "PasswordValidationError_MinimumLength";

		private final Supplier<Integer> minimumPasswordLengthSupplier;

		/**
		 * Constructor.
		 *
		 * @param minimumPasswordLengthSupplier supplies the minimum allowable password length
		 */
		protected MinimumPasswordLengthPolicy(final Supplier<Integer> minimumPasswordLengthSupplier) {
			this.minimumPasswordLengthSupplier = minimumPasswordLengthSupplier;
		}

		@Override
		public ValidationResult apply(final PasswordHolder passwordHolder) {
			final Integer minimumPasswordLength = minimumPasswordLengthSupplier.get();

			if (passwordHolder.getUserPassword().length() < minimumPasswordLength) {
				return new ValidationResult(new ValidationError(PASSWORD_VALIDATION_ERROR_MINIMUM_LENGTH, minimumPasswordLength));
			}

			return ValidationResult.VALID;
		}

	}

	/**
	 * Password validation policy that ensures that passwords contain at least one alphabetic and at least one numeric character.
	 */
	protected static class ContainsAlphaNumericPasswordPolicy implements Function<PasswordHolder, ValidationResult> {

		/**
		 * The key of the message used when a password violates the policy.
		 */
		private static final String PASSWORD_VALIDATION_ERROR_CONTAINS_ALPHA_NUMERIC = "PasswordValidationError_ContainsAlphaNumeric";

		private static final String LETTERS_AND_NUMBERS_REQUIRED_REG_EXPR = "(?=.*\\d)(?=.*([a-zA-Z])).*"; //$NON-NLS-1$

		private final Pattern pattern = Pattern.compile(LETTERS_AND_NUMBERS_REQUIRED_REG_EXPR);

		@Override
		public ValidationResult apply(final PasswordHolder passwordHolder) {
			final Matcher matcher = pattern.matcher(passwordHolder.getUserPassword());

			if (!matcher.matches()) {
				return new ValidationResult(new ValidationError(PASSWORD_VALIDATION_ERROR_CONTAINS_ALPHA_NUMERIC));
			}

			return ValidationResult.VALID;
		}
	}

	/**
	 * Policy that ensures that a password is not identical to previously-used passwords within a given history length.
	 */
	protected static class MinimumUniquePasswordPolicy implements Function<PasswordHolder, ValidationResult> {
		/**
		 * The key of the message used when a password violates the policy.
		 */
		protected static final String PASSWORD_VALIDATION_ERROR_MINIMUM_NO_REPEAT_PASSWORD = "PasswordValidationError_MinimumNoRepeatPassword";

		private final Supplier<PasswordEncoder> passwordEncoderSupplier;
		private final Supplier<Integer> passwordHistoryLengthSupplier;

		/**
		 * Constructor.
		 *
		 * @param passwordEncoderSupplier       produces {@link PasswordEncoder} instances
		 * @param passwordHistoryLengthSupplier supplies the number of passwords in the history to check for duplicates, starting with the most
		 *                                      recent
		 */
		protected MinimumUniquePasswordPolicy(final Supplier<PasswordEncoder> passwordEncoderSupplier,
											  final Supplier<Integer> passwordHistoryLengthSupplier) {
			this.passwordEncoderSupplier = passwordEncoderSupplier;
			this.passwordHistoryLengthSupplier = passwordHistoryLengthSupplier;
		}

		@Override
		public ValidationResult apply(final PasswordHolder passwordHolder) {
			final Integer passwordHistoryLength = passwordHistoryLengthSupplier.get();
			final String encodedPassword = passwordEncoderSupplier.get().encodePassword(passwordHolder.getUserPassword(), null);

			if (encodedPassword.equals(passwordHolder.getPassword())) {
				return new ValidationResult(new ValidationError(PASSWORD_VALIDATION_ERROR_MINIMUM_NO_REPEAT_PASSWORD, passwordHistoryLength));
			}

			final boolean match = passwordHolder.getPasswordHistoryItems().stream()
					.anyMatch(historyItem -> historyItem.getOldPassword().equals(encodedPassword));

			if (match) {
				return new ValidationResult(new ValidationError(PASSWORD_VALIDATION_ERROR_MINIMUM_NO_REPEAT_PASSWORD, passwordHistoryLength));
			} else {
				return ValidationResult.VALID;
			}
		}
	}

}

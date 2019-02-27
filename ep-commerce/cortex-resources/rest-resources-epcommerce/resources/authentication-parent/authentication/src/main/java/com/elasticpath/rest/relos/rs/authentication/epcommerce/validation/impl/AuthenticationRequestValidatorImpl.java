/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.authentication.epcommerce.validation.impl;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;

import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.relos.rs.authentication.dto.AuthenticationRequestDto;
import com.elasticpath.rest.relos.rs.authentication.epcommerce.validation.AuthenticationRequestValidator;
import com.elasticpath.rest.util.validation.FieldValidationBuilder;

/**
 * Validator for an authentication request.
 */
@Component
public class AuthenticationRequestValidatorImpl implements AuthenticationRequestValidator {

	private static final String SHOULD_BE_EMPTY = "should be empty for public users.";

	@Override
	public ExecutionResult<Void> validateRegisteredUserRequest(
			final String storeCode,
			final String username,
			final String password,
			final String role) {

		return new ExecutionResultChain() {
			@Override
			protected ExecutionResult<?> build() {
				Ensure.isTrue(registeredUserRequestHasFields(username, password, role, storeCode),
						OnFailure.returnBadRequestBody(buildMissingFieldsMessageForRegisteredUser(username, password, storeCode, role)));
				return ExecutionResultFactory.createUpdateOK();
			}
		}.execute();
	}

	@Override
	public ExecutionResult<Void> validateAnonymousUserRequest(
			final String storeCode,
			final String username,
			final String password,
			final String role) {

		return new ExecutionResultChain() {
			@Override
			protected ExecutionResult<?> build() {
				Ensure.isTrue(anonymousUserRequestHasFields(storeCode, role),
						OnFailure.returnBadRequestBody(buildMissingFieldsMessageForAnonymousUser(storeCode, role)));
				Ensure.isTrue(anonymousUserRequestHasNoUsernameAndPassword(username, password),
						OnFailure.returnBadRequestBody(buildNotEmptyFieldsMessageForAnonymousUser(username, password)));
				return ExecutionResultFactory.createUpdateOK();
			}
		}.execute();
	}

	private boolean registeredUserRequestHasFields(final String username, final String password, final String storeCode, final String role) {
		return StringUtils.isNotEmpty(username)
				&& StringUtils.isNotEmpty(password)
				&& StringUtils.isNotEmpty(storeCode)
				&& StringUtils.isNotEmpty(role);
	}

	private boolean anonymousUserRequestHasFields(final String storeCode, final String role) {
		return StringUtils.isNotEmpty(storeCode)
				&& StringUtils.isNotEmpty(role);
	}

	private boolean anonymousUserRequestHasNoUsernameAndPassword(final String username, final String password) {
		return StringUtils.isEmpty(username)
				&& StringUtils.isEmpty(password);
	}

	private String buildMissingFieldsMessageForRegisteredUser(final String username,
			final String password,
			final String storeCode,
			final String role) {

		return FieldValidationBuilder.create()
				.validateField(AuthenticationRequestDto.USERNAME_PROPERTY, StringUtils.isNotEmpty(username))
				.validateField(AuthenticationRequestDto.PASSWORD_PROPERTY, StringUtils.isNotEmpty(password))
				.validateField(AuthenticationRequestDto.SCOPE_PROPERTY, StringUtils.isNotEmpty(storeCode))
				.validateField(AuthenticationRequestDto.ROLE_PROPERTY, StringUtils.isNotEmpty(role))
				.build();
	}

	private String buildMissingFieldsMessageForAnonymousUser(final String storeCode, final String role) {
		return FieldValidationBuilder.create()
				.validateField(AuthenticationRequestDto.SCOPE_PROPERTY, StringUtils.isNotEmpty(storeCode))
				.validateField(AuthenticationRequestDto.ROLE_PROPERTY, StringUtils.isNotEmpty(role))
				.build();
	}

	private String buildNotEmptyFieldsMessageForAnonymousUser(final String username, final String password) {
		return FieldValidationBuilder.create()
				.validateField(AuthenticationRequestDto.USERNAME_PROPERTY, StringUtils.isEmpty(username))
				.validateField(AuthenticationRequestDto.PASSWORD_PROPERTY, StringUtils.isEmpty(password))
				.setSingularErrorMessage(SHOULD_BE_EMPTY)
				.setPluralErrorMessage(SHOULD_BE_EMPTY)
				.build();
	}
}

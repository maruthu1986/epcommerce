/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.authentication.springoauth2.tokenstore.impl;

import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.auth.OAuth2AccessTokenMemento;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.relos.rs.authentication.springoauth2.dto.AccessTokenDto;
import com.elasticpath.rest.relos.rs.authentication.springoauth2.transformer.AccessTokenMementoTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.oauth2.OAuth2AccessTokenRepository;

/**
 * Tests for {@link TokenStoreStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class TokenStoreStrategyImplTest {

	private static final String TEST_TOKEN_VALUE = "tokenvalue";

	@Mock
	private OAuth2AccessTokenRepository oAuth2AccessTokenRepository;

	@Mock
	private AccessTokenMementoTransformer accessTokenMementoTransformer;

	@InjectMocks
	private TokenStoreStrategyImpl tokenStoreStrategy;

	@Test
	public void shouldStoreToken() {
		OAuth2AccessTokenMemento oAuth2AccessTokenMemento = mock(OAuth2AccessTokenMemento.class);
		AccessTokenDto accessTokenDto = mock(AccessTokenDto.class);
		when(accessTokenMementoTransformer.transformToOAuth2AccessTokenMemento(accessTokenDto))
			.thenReturn(oAuth2AccessTokenMemento);
		when(oAuth2AccessTokenRepository.save(oAuth2AccessTokenMemento))
			.thenReturn(ExecutionResultFactory.createCreateOKWithData(null, false));

		ExecutionResult<Void> executionResult = tokenStoreStrategy.storeToken(accessTokenDto);

		assertExecutionResult(executionResult).isSuccessful();
	}

	@Test
	public void shouldReadAccessToken() {
		OAuth2AccessTokenMemento accessTokenMemento = mock(OAuth2AccessTokenMemento.class);
		when(oAuth2AccessTokenRepository.load(TEST_TOKEN_VALUE))
			.thenReturn(ExecutionResultFactory.createReadOK(accessTokenMemento));
		AccessTokenDto accessTokenDto = mock(AccessTokenDto.class);
		when(accessTokenMementoTransformer.transformToAccessTokenDto(accessTokenMemento))
			.thenReturn(accessTokenDto);

		ExecutionResult<AccessTokenDto> executionResult = tokenStoreStrategy.readAccessToken(TEST_TOKEN_VALUE);

		assertExecutionResult(executionResult)
			.isSuccessful()
			.data(accessTokenDto);
	}

	@Test
	public void shouldNotReadAccessTokenOnInvalidToken() {
		when(oAuth2AccessTokenRepository.load(TEST_TOKEN_VALUE))
			.thenReturn(ExecutionResultFactory.createServerError("error"));

		ExecutionResult<AccessTokenDto> executionResult = tokenStoreStrategy.readAccessToken(TEST_TOKEN_VALUE);

		assertExecutionResult(executionResult)
			.isFailure()
			.resourceStatus(ResourceStatus.NOT_FOUND);
	}

	@Test
	public void shouldRemoveAccessToken() {
		when(oAuth2AccessTokenRepository.remove(TEST_TOKEN_VALUE))
			.thenReturn(ExecutionResultFactory.createCreateOKWithData(null, false));

		ExecutionResult<Void> executionResult = tokenStoreStrategy.removeAccessToken(TEST_TOKEN_VALUE);

		assertExecutionResult(executionResult)
			.isSuccessful();
	}
}
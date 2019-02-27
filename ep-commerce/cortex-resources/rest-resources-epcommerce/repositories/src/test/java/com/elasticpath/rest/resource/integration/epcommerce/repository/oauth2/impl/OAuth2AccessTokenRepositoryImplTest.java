/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.oauth2.impl;

import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.auth.OAuth2AccessTokenMemento;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.service.auth.OAuth2AccessTokenService;


/**
 * Tests for {@link OAuth2AccessTokenRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class OAuth2AccessTokenRepositoryImplTest {

	private static final String TEST_TOKEN_VALUE = "tokenvalue";
	@Mock
	private OAuth2AccessTokenService tokenService;

	@Mock
	private OAuth2AccessTokenMemento accessToken;

	@Mock
	private BeanFactory coreBeanFactory;

	@InjectMocks
	private OAuth2AccessTokenRepositoryImpl oAuth2AccessTokenRepository;

	@Test
	public void shouldSaveOAuth2AccessToken() {
		ExecutionResult<Void> executionResult = oAuth2AccessTokenRepository.save(accessToken);

		verify(tokenService).saveOrUpdate(accessToken);
		assertExecutionResult(executionResult).
				isSuccessful();
	}

	@Test
	public void shouldNotSaveOAuth2AccessTokenOnRuntimeException() {
		doThrow(RuntimeException.class).when(tokenService).saveOrUpdate(accessToken);

		ExecutionResult<Void> executionResult = oAuth2AccessTokenRepository.save(accessToken);

		verify(tokenService).saveOrUpdate(accessToken);
		assertExecutionResult(executionResult).
				isFailure();
	}

	@Test
	public void shouldLoadOAuth2AccessToken() {
		when(tokenService.load(TEST_TOKEN_VALUE)).thenReturn(accessToken);

		ExecutionResult<OAuth2AccessTokenMemento> executionResult = oAuth2AccessTokenRepository.load(TEST_TOKEN_VALUE);

		verify(tokenService).load(TEST_TOKEN_VALUE);
		assertExecutionResult(executionResult).
				isSuccessful().
				data(accessToken);
	}

	@Test
	public void shouldNotLoadOAuth2AccessTokenOnRuntimeException() {
		doThrow(RuntimeException.class).when(tokenService).load(TEST_TOKEN_VALUE);

		ExecutionResult<OAuth2AccessTokenMemento> executionResult = oAuth2AccessTokenRepository.load(TEST_TOKEN_VALUE);

		verify(tokenService).load(TEST_TOKEN_VALUE);
		assertExecutionResult(executionResult).
				isFailure();
	}

	@Test
	public void shouldNotLoadOAuth2AccessTokenOnNullResponseFromTokenService() {
		when(tokenService.load(TEST_TOKEN_VALUE)).thenReturn(null);

		ExecutionResult<OAuth2AccessTokenMemento> executionResult = oAuth2AccessTokenRepository.load(TEST_TOKEN_VALUE);

		verify(tokenService).load(TEST_TOKEN_VALUE);
		assertExecutionResult(executionResult).
				isFailure();
	}

	@Test
	public void shouldRemoveOAuth2AccessToken() {
		ExecutionResult<Void> executionResult = oAuth2AccessTokenRepository.remove(TEST_TOKEN_VALUE);

		verify(tokenService).remove(TEST_TOKEN_VALUE);
		assertExecutionResult(executionResult).
				isSuccessful();
	}

	@Test
	public void shouldNotRemoveOAuth2AccessTokenOnRuntimeException() {
		doThrow(RuntimeException.class).when(tokenService).remove(TEST_TOKEN_VALUE);

		ExecutionResult<Void> executionResult = oAuth2AccessTokenRepository.remove(TEST_TOKEN_VALUE);

		verify(tokenService).remove(TEST_TOKEN_VALUE);
		assertExecutionResult(executionResult).
				isFailure();
	}

	@Test
	public void shouldCreateOAuth2AccessToken() {
		OAuth2AccessTokenMemento oAuth2AccessTokenMemento = mock(OAuth2AccessTokenMemento.class);
		when(coreBeanFactory.getBean(ContextIdNames.OAUTH2_ACCESS_TOKEN_MEMENTO)).thenReturn(oAuth2AccessTokenMemento);

		ExecutionResult<OAuth2AccessTokenMemento> executionResult = oAuth2AccessTokenRepository.createOAuth2AccessToken();

		verify(coreBeanFactory).getBean(ContextIdNames.OAUTH2_ACCESS_TOKEN_MEMENTO);
		assertExecutionResult(executionResult).
				isSuccessful().
				data(oAuth2AccessTokenMemento);
	}

	@Test
	public void shouldNotCreateOAuth2AccessTokenOnRuntimeException() {
		doThrow(RuntimeException.class).when(coreBeanFactory).getBean(ContextIdNames.OAUTH2_ACCESS_TOKEN_MEMENTO);

		ExecutionResult<OAuth2AccessTokenMemento> executionResult = oAuth2AccessTokenRepository.createOAuth2AccessToken();

		verify(coreBeanFactory).getBean(ContextIdNames.OAUTH2_ACCESS_TOKEN_MEMENTO);
		assertExecutionResult(executionResult).
				isFailure().
				resourceStatus(ResourceStatus.SERVER_ERROR);
	}

}
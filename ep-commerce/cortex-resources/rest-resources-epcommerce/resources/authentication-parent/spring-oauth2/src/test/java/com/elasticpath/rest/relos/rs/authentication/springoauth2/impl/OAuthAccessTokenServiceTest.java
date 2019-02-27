/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.authentication.springoauth2.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.relos.rs.authentication.springoauth2.dto.AccessTokenDto;
import com.elasticpath.rest.relos.rs.authentication.springoauth2.security.TokenExpiryResolver;
import com.elasticpath.rest.relos.rs.authentication.springoauth2.tokenstore.TokenStoreStrategy;
import com.elasticpath.rest.relos.rs.authentication.springoauth2.transform.AccessTokenDtoTransformer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

/**
 * Test suite for {@link OAuthAccessTokenServiceImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class OAuthAccessTokenServiceTest {

	@Mock
	private TokenStoreStrategy tokenStoreStrategy;

	@Mock
	private AccessTokenDtoTransformer transformer;

	@Captor
	private ArgumentCaptor<AccessTokenDto> dtoCaptor;

	@Mock
	private TokenExpiryResolver tokenExpiryResolver;

	@Mock
	private OAuth2AccessToken accessToken;

	@InjectMocks
	private OAuthAccessTokenServiceImpl service;

	@Before
	public void setup() {
		ExecutionResult<Date> expirationDate = ExecutionResultFactory.createReadOK(Date.from(Instant.now()));
		when(tokenExpiryResolver.getTokenExpiryDate())
				.thenReturn(expirationDate);
		when(tokenStoreStrategy.storeToken(any(AccessTokenDto.class)))
				.thenReturn(ExecutionResultFactory.createReadOK(null));
		when(transformer.transformToOauth2AccessToken(dtoCaptor.capture()))
				.thenReturn(accessToken);
	}

	@Test
	public void whenCreatingOauth2TokenShouldReturnRolesProperty() {
		List<String> roles = Arrays.asList("ROLE1", "ROLE2");

		ExecutionResult<OAuth2AccessToken> result = service.createOAuth2Token("foo", "bar", roles);

		assertThat(result.isSuccessful()).isTrue();
		AccessTokenDto actualDto = dtoCaptor.getValue();
		assertThat(actualDto.getRoles()).containsExactlyElementsOf(roles);
		assertThat(actualDto.getRole()).isEqualTo("ROLE1");
		assertThat(result.getData()).isSameAs(accessToken);
	}

	@Test
	public void whenCreatingOauth2TokenWithEmptyRolesShouldReturnNullRoleProperty() {
		List<String> roles = Collections.emptyList();

		ExecutionResult<OAuth2AccessToken> result = service.createOAuth2Token("foo", "bar", roles);

		assertThat(result.isSuccessful()).isTrue();
		AccessTokenDto actualDto = dtoCaptor.getValue();
		assertThat(actualDto.getRole()).isNull();
	}
}

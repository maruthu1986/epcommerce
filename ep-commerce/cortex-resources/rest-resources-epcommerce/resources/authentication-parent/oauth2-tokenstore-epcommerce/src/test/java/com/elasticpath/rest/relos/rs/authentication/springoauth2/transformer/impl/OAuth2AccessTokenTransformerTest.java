/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.authentication.springoauth2.transformer.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.hamcrest.Matchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.auth.OAuth2AccessTokenMemento;
import com.elasticpath.domain.auth.OAuth2AuthenticationMemento;
import com.elasticpath.domain.auth.UserAuthenticationMemento;
import com.elasticpath.domain.auth.impl.OAuth2AccessTokenMementoImpl;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.identity.TestRoleConstants;
import com.elasticpath.rest.relos.rs.authentication.springoauth2.dto.AccessTokenDto;
import com.elasticpath.rest.resource.integration.epcommerce.repository.oauth2.OAuth2AccessTokenRepository;

/**
 * Test class {@link OAuth2AccessTokenTransformer}.
 */
@RunWith(MockitoJUnitRunner.class)
public class OAuth2AccessTokenTransformerTest {

	private static final Date EXPIRY_DATE = new Date();
	private static final String STORE_CODE = "store_code";
	private static final String USER_ID = "user_id";
	private static final String TOKEN_TYPE = "token_type";
	private static final String TOKEN_ID_VALUE = "token_id_value";
	private static final String ROLE2 = "CSR";
	private static final String ALL_ROLES = TestRoleConstants.REGISTERED + ',' + ROLE2;

	@Mock
	private OAuth2AccessTokenRepository oAuth2AccessTokenRepository;

	@InjectMocks
	private OAuth2AccessTokenTransformer accessTokenMementoTransformer;

	/**
	 * Test transform to OAuth2 access token memento.
	 */
	@Test
	public void testTransformToMemento() {
		when(oAuth2AccessTokenRepository.createOAuth2AccessToken()).thenReturn(
				ExecutionResultFactory.createReadOK(
						new OAuth2AccessTokenMementoImpl()
				)
		);

		AccessTokenDto accessTokenDto = ResourceTypeFactory.createResourceEntity(AccessTokenDto.class)
				.setTokenId(TOKEN_ID_VALUE)
				.setTokenType(TOKEN_TYPE)
				.setExpiryDate(EXPIRY_DATE)
				.setUserId(USER_ID)
				.setScope(STORE_CODE)
				.setRoles(Arrays.asList(TestRoleConstants.REGISTERED, ROLE2));

		OAuth2AccessTokenMemento tokenMemento = accessTokenMementoTransformer.transformToOAuth2AccessTokenMemento(accessTokenDto);

		assertEquals("Token value does not match expected value.", TOKEN_ID_VALUE, tokenMemento.getTokenId());
		assertEquals("Token type does not match expected value.", TOKEN_TYPE, tokenMemento.getTokenType());
		assertEquals("Expiry Date does not match expected date.", EXPIRY_DATE, tokenMemento.getExpiryDate());
		UserAuthenticationMemento userAuthenticationMemento = tokenMemento.getAuthenticationMemento().getUserAuthenticationMemento();
		assertEquals("User Id does not match expected value", USER_ID, userAuthenticationMemento.getCustomerGuid());
		assertEquals("Store code does not match expected value", STORE_CODE, userAuthenticationMemento.getStoreCode());
		assertEquals("Role does not match expected value", ALL_ROLES,
			userAuthenticationMemento.getRole());
	}

	/**
	 * Test transform to access token dto.
	 */
	@Test
	public void testTransformToAccessTokenDto() {
		OAuth2AccessTokenMemento tokenMemento = new OAuth2AccessTokenMementoImpl();
		tokenMemento.setTokenId(TOKEN_ID_VALUE);
		tokenMemento.setExpiryDate(EXPIRY_DATE);
		tokenMemento.setTokenType(TOKEN_TYPE);

		OAuth2AuthenticationMemento authenticationMemento = new OAuth2AuthenticationMemento();

		UserAuthenticationMemento userAuthenticationMemento = new UserAuthenticationMemento();
		userAuthenticationMemento.setCustomerGuid(USER_ID);
		userAuthenticationMemento.setStoreCode(STORE_CODE);
		userAuthenticationMemento.setRole(ALL_ROLES);
		authenticationMemento.setUserAuthenticationMemento(userAuthenticationMemento);

		tokenMemento.setAuthenticationMemento(authenticationMemento);

		AccessTokenDto accessTokenDto = accessTokenMementoTransformer.transformToAccessTokenDto(tokenMemento);

		assertEquals("Token value does not match expected value.", TOKEN_ID_VALUE, accessTokenDto.getTokenId());
		assertEquals("Token type does not match expected value.", TOKEN_TYPE, accessTokenDto.getTokenType());
		assertEquals("Expiry Date does not match expected date.", EXPIRY_DATE, accessTokenDto.getExpiryDate());
		assertEquals("User Id does not match expected value", USER_ID, accessTokenDto.getUserId());
		assertEquals("Store code does not match expected value", accessTokenDto.getScope(), STORE_CODE);
		assertThat("Roles do not match expected values", accessTokenDto.getRoles(),
			Matchers.contains(TestRoleConstants.REGISTERED, ROLE2));
		assertThat("Role does not match expected", accessTokenDto.getRole(),
			Matchers.equalTo(TestRoleConstants.REGISTERED));
	}
}

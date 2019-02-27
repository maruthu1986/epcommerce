/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.authentication.springoauth2.transform.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import org.junit.Test;

import org.hamcrest.Matchers;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.relos.rs.authentication.springoauth2.dto.AccessTokenDto;
import com.elasticpath.rest.relos.rs.authentication.springoauth2.transform.AccessTokenDtoTransformer;

/**
 * Test class for {@link AccessTokenDtoTransformer}.
 */
public final class AccessTokenDtoTransformerTest {

	private static final String ROLE = "role";
	private static final String SCOPE = "scope";
	private static final String TOKEN_VALUE_STRING = "token_value_string";

	private final AccessTokenDtoTransformer accessTokenDtoTransformer = new AccessTokenDtoTransformerImpl();

	/**
	 * Test transform to o auth2 access token.
	 */
	@Test
	public void testTransformToOAuth2AccessToken() {
		Date expiryDate = new Date();
		AccessTokenDto accessTokenDto = ResourceTypeFactory.createResourceEntity(AccessTokenDto.class)
				.setTokenId(TOKEN_VALUE_STRING)
				.setExpiryDate(expiryDate)
				.setScope(SCOPE)
				.setRoles(Collections.singleton(ROLE))
				.setRole(ROLE);

		OAuth2AccessToken token = accessTokenDtoTransformer.transformToOauth2AccessToken(accessTokenDto);

		assertEquals("Token value does not match expected value.", TOKEN_VALUE_STRING, token.getValue());
		assertEquals("Expiry date does not match expected value.", expiryDate, token.getExpiration());
		assertTrue("Token should contain expected scope.", token.getScope().contains(SCOPE));
		assertThat("Roles do not match expected value",
				(Collection<String>) token.getAdditionalInformation().get("roles"), Matchers.contains(ROLE));
		assertThat("Role does not match expected value", token.getAdditionalInformation().get("role"), Matchers.equalTo(ROLE));
	}
}

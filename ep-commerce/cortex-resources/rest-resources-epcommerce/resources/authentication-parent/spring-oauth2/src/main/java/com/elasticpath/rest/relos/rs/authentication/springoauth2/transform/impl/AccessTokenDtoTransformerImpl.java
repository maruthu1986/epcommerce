/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.authentication.springoauth2.transform.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import com.elasticpath.rest.relos.rs.authentication.springoauth2.dto.AccessTokenDto;
import com.elasticpath.rest.relos.rs.authentication.springoauth2.transform.AccessTokenDtoTransformer;

/**
 * Transformer class for AccessTokenDto.
 */
@Component
public class AccessTokenDtoTransformerImpl implements AccessTokenDtoTransformer {

	@Override
	public OAuth2AccessToken transformToOauth2AccessToken(final AccessTokenDto accessTokenDto) {
		DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(accessTokenDto.getTokenId());
		token.setExpiration(accessTokenDto.getExpiryDate());
		Set<String> scopes = Collections.singleton(accessTokenDto.getScope());
		token.setScope(scopes);

		Map<String, Object> additionalInformation = new HashMap<>();
		additionalInformation.put("roles", accessTokenDto.getRoles());
		additionalInformation.put("role", accessTokenDto.getRole());

		token.setAdditionalInformation(additionalInformation);

		return token;
	}
}

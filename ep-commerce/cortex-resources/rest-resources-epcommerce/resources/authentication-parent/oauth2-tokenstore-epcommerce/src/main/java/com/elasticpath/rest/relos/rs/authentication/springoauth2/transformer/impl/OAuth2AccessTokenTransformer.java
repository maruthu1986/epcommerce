/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
/*
* Copyright © 2013 Elastic Path Software Inc. All rights reserved.
*/
package com.elasticpath.rest.relos.rs.authentication.springoauth2.transformer.impl;

import java.util.Iterator;

import com.google.common.base.Splitter;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.auth.ClientAuthenticationMemento;
import com.elasticpath.domain.auth.OAuth2AccessTokenMemento;
import com.elasticpath.domain.auth.OAuth2AuthenticationMemento;
import com.elasticpath.domain.auth.UserAuthenticationMemento;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.relos.rs.authentication.springoauth2.dto.AccessTokenDto;
import com.elasticpath.rest.relos.rs.authentication.springoauth2.transformer.AccessTokenMementoTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.oauth2.OAuth2AccessTokenRepository;

/**
 * Transformer that uses the {@link OAuth2AccessTokenRepository}.
 */
@Component
public class OAuth2AccessTokenTransformer implements AccessTokenMementoTransformer {

	private static final String EP_CLIENT_ID = "ep_client_id";
	private static final Splitter SPLITTER = Splitter
		.on(",")
		.trimResults()
		.omitEmptyStrings();

	@Reference
	private OAuth2AccessTokenRepository oAuth2AccessTokenRepository;

	/**
	 * Transform {@link OAuth2AccessTokenMemento} to {@link AccessTokenDto}.
	 *
	 * @param memento the memento
	 * @return the access token dto
	 */
	public AccessTokenDto transformToAccessTokenDto(final OAuth2AccessTokenMemento memento) {
		OAuth2AuthenticationMemento authMemento = memento.getAuthenticationMemento();
		UserAuthenticationMemento userMemento = authMemento.getUserAuthenticationMemento();

		Iterable<String> roles = SPLITTER.split(userMemento.getRole());
		AccessTokenDto accessTokenDto = ResourceTypeFactory.createResourceEntity(AccessTokenDto.class)
				.setTokenId(memento.getTokenId())
				.setTokenType(memento.getTokenType())
				.setExpiryDate(memento.getExpiryDate())
				.setScope(userMemento.getStoreCode())
				.setUserId(userMemento.getCustomerGuid())
				.setRoles(roles);
		Iterator<String> rolesIterator = roles.iterator();
		if (rolesIterator.hasNext()) {
			accessTokenDto.setRole(rolesIterator.next());
		}
		return accessTokenDto;
	}

	/**
	 * Transform {@link AccessTokenDto} to {@link OAuth2AccessTokenMemento}.
	 *
	 * @param accessTokenDto the access token dto
	 * @return the o auth2 authentication memento
	 */
	public OAuth2AccessTokenMemento transformToOAuth2AccessTokenMemento(final AccessTokenDto accessTokenDto) {
		OAuth2AccessTokenMemento memento = Assign.ifSuccessful(oAuth2AccessTokenRepository.createOAuth2AccessToken());
		memento.setTokenId(accessTokenDto.getTokenId());
		memento.setExpiryDate(accessTokenDto.getExpiryDate());
		memento.setTokenType(accessTokenDto.getTokenType());

		OAuth2AuthenticationMemento authenticationMemento = new OAuth2AuthenticationMemento();
		ClientAuthenticationMemento clientAuthenticationMemento = new ClientAuthenticationMemento();
		clientAuthenticationMemento.setClientId(EP_CLIENT_ID);
		clientAuthenticationMemento.setClientSecret(StringUtils.EMPTY);
		authenticationMemento.setClientAuthenticationMemento(clientAuthenticationMemento);

		UserAuthenticationMemento userAuthenticationMemento = new UserAuthenticationMemento();
		userAuthenticationMemento.setCustomerGuid(accessTokenDto.getUserId());
		String role = StringUtils.join(accessTokenDto.getRoles(), ',');
		userAuthenticationMemento.setRole(role);
		//We don't store user credentials.
		userAuthenticationMemento.setCredentials(StringUtils.EMPTY);
		userAuthenticationMemento.setStoreCode(accessTokenDto.getScope());
		authenticationMemento.setUserAuthenticationMemento(userAuthenticationMemento);

		memento.setAuthenticationMemento(authenticationMemento);

		return memento;
	}
}

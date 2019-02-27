/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
/*
* Copyright © 2013 Elastic Path Software Inc. All rights reserved.
*/
package com.elasticpath.rest.relos.rs.authentication.springoauth2.transformer;

import com.elasticpath.domain.auth.OAuth2AccessTokenMemento;
import com.elasticpath.rest.relos.rs.authentication.springoauth2.dto.AccessTokenDto;

/**
 * Transformer for {@link AccessTokenDto} and {@link OAuth2AccessTokenMemento}.
 */
public interface AccessTokenMementoTransformer {

	/**
	 * Transform {@link OAuth2AccessTokenMemento} to {@link AccessTokenDto}.
	 *
	 * @param memento the memento
	 * @return the access token dto
	 */
	AccessTokenDto transformToAccessTokenDto(OAuth2AccessTokenMemento memento);

	/**
	 * Transform {@link AccessTokenDto} to {@link OAuth2AccessTokenMemento}.
	 *
	 * @param accessTokenDto the access token dto
	 * @return the o auth2 authentication memento
	 */
	OAuth2AccessTokenMemento transformToOAuth2AccessTokenMemento(AccessTokenDto accessTokenDto);
}

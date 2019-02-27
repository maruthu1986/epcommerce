/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.oauth2.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.auth.OAuth2AccessTokenMemento;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.cache.CacheRemove;
import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.oauth2.OAuth2AccessTokenRepository;
import com.elasticpath.service.auth.OAuth2AccessTokenService;

/**
 * The implementation of the facade for {@link OAuth2AccessTokenService} related operations.
 */
@Singleton
@Named("oAuth2AccessTokenRepository")
public class OAuth2AccessTokenRepositoryImpl implements OAuth2AccessTokenRepository {

	private static final Logger LOG = LoggerFactory.getLogger(OAuth2AccessTokenRepository.class);
	private static final String INVALID_TOKEN = "Invalid access token.";

	private final OAuth2AccessTokenService tokenService;
	private final BeanFactory coreBeanFactory;

	/**
	 * Default constructor.
	 *
	 * @param tokenService    the token service
	 * @param coreBeanFactory the core bean factory.
	 */
	@Inject
	OAuth2AccessTokenRepositoryImpl(
			@Named("oAuth2AccessTokenService") final OAuth2AccessTokenService tokenService,
			@Named("coreBeanFactory") final BeanFactory coreBeanFactory) {
		this.tokenService = tokenService;
		this.coreBeanFactory = coreBeanFactory;
	}

	@Override
	@CacheRemove(typesToInvalidate = OAuth2AccessTokenMemento.class)
	public ExecutionResult<Void> save(final OAuth2AccessTokenMemento oAuth2AccessTokenMemento) {
		try {
			tokenService.saveOrUpdate(oAuth2AccessTokenMemento);
		} catch (RuntimeException runtimeException) {
			LOG.warn("Unable to save or update token", runtimeException);
			return ExecutionResultFactory.createServerError("Unable to save or update token.");
		}
		return ExecutionResultFactory.createCreateOKWithData(null, false);
	}

	@Override
	@CacheResult
	public ExecutionResult<OAuth2AccessTokenMemento> load(final String tokenValue) {
		try {
			OAuth2AccessTokenMemento oAuth2AccessTokenMemento = tokenService.load(tokenValue);
			if (oAuth2AccessTokenMemento == null) {
				return ExecutionResultFactory.createNotFound(INVALID_TOKEN);
			}
			return ExecutionResultFactory.createReadOK(oAuth2AccessTokenMemento);
		} catch (RuntimeException runtimeException) {
			LOG.trace("no access token found for {}", tokenValue, runtimeException);
			return ExecutionResultFactory.createNotFound(INVALID_TOKEN);
		}
	}

	@Override
	@CacheRemove(typesToInvalidate = OAuth2AccessTokenMemento.class)
	public ExecutionResult<Void> remove(final String tokenValue) {
		try {
			tokenService.remove(tokenValue);
		} catch (RuntimeException runtimeException) {
			LOG.warn("Unable to remove token", runtimeException);
			return ExecutionResultFactory.createServerError("Unable to remove token.");
		}
		return ExecutionResultFactory.createDeleteOK();
	}

	@Override
	@CacheResult
	public ExecutionResult<OAuth2AccessTokenMemento> createOAuth2AccessToken() {
		try {
			OAuth2AccessTokenMemento memento = coreBeanFactory.getBean(ContextIdNames.OAUTH2_ACCESS_TOKEN_MEMENTO);
			return ExecutionResultFactory.createReadOK(memento);
		} catch (RuntimeException runtimeException) {
			String errorMessage = "Unable to create OAuth2AccessTokenMemento";
			LOG.warn(errorMessage, runtimeException);
			return ExecutionResultFactory.createServerError(errorMessage);
		}
	}

}

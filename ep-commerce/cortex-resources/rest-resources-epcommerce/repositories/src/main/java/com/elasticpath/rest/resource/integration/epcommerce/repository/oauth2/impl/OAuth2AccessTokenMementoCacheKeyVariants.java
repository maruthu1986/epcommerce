/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.oauth2.impl;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.auth.OAuth2AccessTokenMemento;
import com.elasticpath.rest.cache.CacheKeyVariants;

/**
 * Generates cache keys from class name and parameters.
 */
@Singleton
@Named("oauth2AccessTokenMementoCacheKeyVariants")
public class OAuth2AccessTokenMementoCacheKeyVariants implements CacheKeyVariants<OAuth2AccessTokenMemento> {

	@Override
	public Collection<Object[]> get(final OAuth2AccessTokenMemento objectToCache) {
		return Collections.singleton(new Object[] { objectToCache.getTokenId() });
	}

	@Override
	public Class<OAuth2AccessTokenMemento> getType() {
		return OAuth2AccessTokenMemento.class;
	}
}

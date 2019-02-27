/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.email.producer.spi.composer.util;

import java.util.Map;

import com.elasticpath.domain.store.Store;
import com.elasticpath.email.domain.EmailProperties;

/**
 * Factory class that creates the velocity context for generating email body.
 */
public interface EmailContextFactory {
	/**
	 * Creates a new Map containing all of the data required to render an email body.
	 *
	 * @param store the store, or null if no particular store is specified
	 * @param emailProperties email specific properties
	 * @return A map
	 */
	Map<String, Object> createVelocityContext(Store store, EmailProperties emailProperties);
}

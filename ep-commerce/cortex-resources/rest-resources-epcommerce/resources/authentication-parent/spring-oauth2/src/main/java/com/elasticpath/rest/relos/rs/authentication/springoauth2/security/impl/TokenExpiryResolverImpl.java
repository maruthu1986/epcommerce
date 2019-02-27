/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.authentication.springoauth2.security.impl;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.relos.rs.authentication.springoauth2.security.TokenExpiryResolver;

/**
 * Token expiry resolver implementation with configurable expiry time.
 */
@Component(configurationPid = "oauthTokenExpiry")
@Designate(ocd = TokenExpiryResolverImpl.Config.class)
public class TokenExpiryResolverImpl implements TokenExpiryResolver {

	private static final Logger LOG = LoggerFactory.getLogger(TokenExpiryResolverImpl.class);

	private long tokenTTLSeconds;


	/**
	 * Configuration definition.
	 */
	@ObjectClassDefinition(
			name = "OAuth2 Token Expiry",
			description = "EP :: RelOS : RS Authentication ∫ Spring OAuth2")
	@interface Config {
		/**
		 * Default number of seconds a token will be valid for.
		 */
		int DEFAULT_TOKEN_EXPIRY = 604_800; //1 week

		/**
		 * Seconds until expiry.
		 * @return seconds value.
		 */
		@AttributeDefinition(
			name = "Seconds",
			description = "Defines the authentication token's expiry time, in seconds",
			min = "1")
		int seconds() default DEFAULT_TOKEN_EXPIRY;
	}


	/**
	 * Gets the token expiry date.
	 *
	 * @return the Date the token will expire.
	 */
	public ExecutionResult<Date> getTokenExpiryDate() {
		Date expiryDate = new Date(System.currentTimeMillis() + tokenTTLSeconds);

		return ExecutionResultFactory.createReadOK(expiryDate);
	}

	/**
	 * Called by Declarative Services on activation and configuration modification.
	 *
	 * @param config the new Configuration
	 * @throws ConfigurationException thrown if the config does not meet expectations.
	 */
	@Activate
	@Modified
	protected void configure(final Config config) throws ConfigurationException {
		int configExpiry = config.seconds();
		if (configExpiry < 0) {
			throw new ConfigurationException("seconds", "expiry must be a positive number of seconds.");
		}
		LOG.debug("Updated Token Expiry, new value = {}", configExpiry);
		tokenTTLSeconds = TimeUnit.SECONDS.toMillis(configExpiry);
	}
}

/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.authentication.springoauth2.security.impl;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.osgi.service.cm.ConfigurationException;

import com.elasticpath.rest.command.ExecutionResult;

/**
 * Test class for {@link TokenExpiryResolverImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class TokenExpiryResolverTest {

	@Mock
	private TokenExpiryResolverImpl.Config mockConfig;

	private final TokenExpiryResolverImpl tokenExpiryResolver = new TokenExpiryResolverImpl();

	/**
	 * Test get token expiry date.
	 */
	@Test
	public void testGetTokenExpiryDate() throws ConfigurationException {
		ExecutionResult<Date> tokenExpiryDateResult = tokenExpiryResolver.getTokenExpiryDate();

		assertTrue("Operaton should be successful.", tokenExpiryDateResult.isSuccessful());
	}

	/**
	 * Test update token expiry date.
	 */
	@Test
	public void testUpdateTokenExpiryDate() throws ConfigurationException {
		final int newExpiry = 1234;
		setupConfig(newExpiry);
		tokenExpiryResolver.configure(mockConfig);

		ExecutionResult<Date> tokenExpiryDateResult = tokenExpiryResolver.getTokenExpiryDate();

		assertTrue("Operaton should be successful.", tokenExpiryDateResult.isSuccessful());
	}

	/**
	 * Test update token expiry date. Even though the config admin annotations specify a minimum value, it's only for the UI; one could edit
	 * the tokenExpiryResolver.config file with negative numbers.
	 */
	@Test(expected = ConfigurationException.class)
	public void testUpdateTokenExpiryDateWithNegativeNumber() throws ConfigurationException {
		final int newExpiry = -14;
		setupConfig(newExpiry);

		tokenExpiryResolver.configure(mockConfig);
	}

	private void setupConfig(final int newExpiry) {
		when(mockConfig.seconds())
			.thenReturn(newExpiry);
	}
}

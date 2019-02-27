/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.authentication.springoauth2.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import javax.ws.rs.core.HttpHeaders;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.hamcrest.Matchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;

import com.elasticpath.rest.relos.rs.authentication.AuthHeaderConstants;


/**
 * Tests for {@link AuthHeaderUtil}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class AuthHeaderUtilTest {

	private static final String AUTH_TOKEN_KEY = "auth_token";
	private static final String SEPARATOR = ",";
	private static final String TOKEN_ERROR_MESSAGE = "The token should be as expected";
	private static final String CONTENT_TYPE_HEADER = "Content-Type: application/x-www-form-urlencoded";
	private static final String AUTHORIZATION_TOKEN = "TEST_AUTHORIZATION_TOKEN";
	private static final String AUTH_VALUE_PREFIX = "oauth_signature_method";
	private static final String EXPECTED_URI_PARAM_TOKEN_VALUE = "fred";

	@Mock
	private HttpHeaders httpHeaders;

	private final MockHttpServletRequest request = new MockHttpServletRequest();


	/**
	 * Test get token from request with authorization header without oauth signature method token.
	 */
	@Test
	public void testGetTokenFromRequestWithAuthorizationHeaderWithoutOauthSignatureMethodToken() {
		givenAuthorizationHeaderOnRequestWithValue(AuthHeaderConstants.AUTH_HEADER_PREFIX + AUTHORIZATION_TOKEN);

		String tokenValue = AuthHeaderUtil.getTokenFromRequest(request);

		assertEquals(TOKEN_ERROR_MESSAGE, AUTHORIZATION_TOKEN, tokenValue);
	}

	/**
	 * Test get token from request with authorization header starting with "oauth_signature_method" which returns a null if found.<br>
	 */
	@Test
	public void testGetTokenFromRequestWithAuthorizationHeaderWithOauthSignatureMethodToken() {
		String tokenWithHeader = AuthHeaderConstants.AUTH_HEADER_PREFIX + AUTH_VALUE_PREFIX + AUTHORIZATION_TOKEN;
		givenAuthorizationHeaderOnRequestWithValue(tokenWithHeader);

		String tokenValue = AuthHeaderUtil.getTokenFromRequest(request);

		assertNull(TOKEN_ERROR_MESSAGE, tokenValue);
	}

	/**
	 * Test get token from request with authorization header with oauth signature method token with an associated token.
	 * With the header value created in the alternately handled format.
	 */
	@Test
	public void testGetTokenFromRequestWithAuthorizationHeaderWithOauthSignatureMethodTokenWithAlternateFormat() {
		String expectedValue = AUTHORIZATION_TOKEN + SEPARATOR + AUTH_TOKEN_KEY;
		String tokenWithHeader = AuthHeaderConstants.AUTH_HEADER_PREFIX + expectedValue;
		givenAuthorizationHeaderOnRequestWithValue(tokenWithHeader);

		String tokenValue = AuthHeaderUtil.getTokenFromRequest(request);

		assertEquals(TOKEN_ERROR_MESSAGE, AUTHORIZATION_TOKEN, tokenValue);
	}

	/**
	 * Test get token from request with no headers.
	 */
	@Test
	public void testGetTokenFromRequestWithNoHeaders() {
		String tokenValue = AuthHeaderUtil.getTokenFromRequest(request);

		assertNull(TOKEN_ERROR_MESSAGE, tokenValue);
	}

	/**
	 * Test get token from request with no authorization headers.
	 */
	@Test
	public void testGetTokenFromRequestWithNoAuthorizationHeaders() {
		request.addHeader(AuthHeaderConstants.AUTHORIZATION, CONTENT_TYPE_HEADER);

		String tokenValue = AuthHeaderUtil.getTokenFromRequest(request);

		assertNull(TOKEN_ERROR_MESSAGE, tokenValue);
	}

	/** Test. */
	@Test
	public void testThatHeaderOverridesUrlParam() {
		givenAuthorizationHeaderOnRequestWithValue(AuthHeaderConstants.AUTH_HEADER_PREFIX + AUTHORIZATION_TOKEN);
		request.addParameter(AuthHeaderConstants.AUTH_TOKEN_URI_PARAM, EXPECTED_URI_PARAM_TOKEN_VALUE);
		String tokenValue = AuthHeaderUtil.getTokenFromRequest(request);
		assertEquals(TOKEN_ERROR_MESSAGE, AUTHORIZATION_TOKEN, tokenValue);
	}

	/** Test. */
	@Test
	public void testGetTokenFromUriParam() {
		request.addParameter(AuthHeaderConstants.AUTH_TOKEN_URI_PARAM, EXPECTED_URI_PARAM_TOKEN_VALUE);
		String tokenValue = AuthHeaderUtil.getTokenFromRequest(request);
		assertEquals(TOKEN_ERROR_MESSAGE, EXPECTED_URI_PARAM_TOKEN_VALUE, tokenValue);
	}

	/** Test. */
	@Test
	public void testGetEmptyTokenFromUriParam() {
		request.addParameter(AuthHeaderConstants.AUTH_TOKEN_URI_PARAM, "");
		String tokenValue = AuthHeaderUtil.getTokenFromRequest(request);
		assertThat(TOKEN_ERROR_MESSAGE, tokenValue, Matchers.isEmptyString());
	}

	/**
	 * Test parse token with null http headers.
	 */
	@Test
	public void testParseTokenWithNullHttpHeaders() {
		givenAuthorizationHeaderWithValue(new String[]{null});

		String tokenValue = AuthHeaderUtil.parseToken(httpHeaders);

		assertNull(TOKEN_ERROR_MESSAGE, tokenValue);
	}

	/**
	 * Test parse token with no http headers.
	 */
	@Test
	public void testParseTokenWithNoHttpHeaders() {
		givenAuthorizationHeaderWithValue();

		String tokenValue = AuthHeaderUtil.parseToken(httpHeaders);

		assertNull(TOKEN_ERROR_MESSAGE, tokenValue);
	}

	/**
	 * Test get token from request with no authorization headers.
	 */
	@Test
	public void testParseTokenFromRequestWithNoAuthorizationHeaders() {
		givenAuthorizationHeaderWithValue(CONTENT_TYPE_HEADER);

		String tokenValue = AuthHeaderUtil.parseToken(httpHeaders);

		assertNull(TOKEN_ERROR_MESSAGE, tokenValue);
	}

	/**
	 * Test parse token with authorization header without oauth signature method token.
	 */
	@Test
	public void testParseTokenWithAuthorizationHeaderWithoutOauthSignatureMethodToken() {
		String tokenWithHeader = AuthHeaderConstants.AUTH_HEADER_PREFIX + AUTHORIZATION_TOKEN;
		givenAuthorizationHeaderWithValue(tokenWithHeader);

		String tokenValue = AuthHeaderUtil.parseToken(httpHeaders);

		assertEquals(TOKEN_ERROR_MESSAGE, AUTHORIZATION_TOKEN, tokenValue);
	}

	/**
	 * Test parse token with authorization header starting with "oauth_signature_method" which returns a null if found.<br>
	 */
	@Test
	public void testParseTokenWithAuthorizationHeaderWithOauthSignatureMethodToken() {
		String tokenWithHeader = AuthHeaderConstants.AUTH_HEADER_PREFIX + AUTH_VALUE_PREFIX + AUTHORIZATION_TOKEN;
		givenAuthorizationHeaderWithValue(tokenWithHeader);

		String tokenValue = AuthHeaderUtil.parseToken(httpHeaders);

		assertNull(TOKEN_ERROR_MESSAGE, tokenValue);
	}

	/**
	 * Test parse token with authorization header with oauth signature method token with an associated token.
	 * With the header value created in the alternately handled format.
	 */
	@Test
	public void testParseTokenWithAuthorizationHeaderWithOauthSignatureMethodTokenWithAlternateFormat() {
		String tokenWithHeader = AuthHeaderConstants.AUTH_HEADER_PREFIX + AUTHORIZATION_TOKEN + SEPARATOR + AUTH_TOKEN_KEY;
		givenAuthorizationHeaderWithValue(tokenWithHeader);

		String tokenValue = AuthHeaderUtil.parseToken(httpHeaders);

		assertEquals(TOKEN_ERROR_MESSAGE, AUTHORIZATION_TOKEN, tokenValue);
	}

	private void givenAuthorizationHeaderWithValue(final String... expectedHeaders) {
		when(httpHeaders.getRequestHeader(AuthHeaderConstants.AUTHORIZATION))
				.thenReturn(Arrays.asList(expectedHeaders));
	}

	private void givenAuthorizationHeaderOnRequestWithValue(final String expectedHeader) {
		request.addHeader(AuthHeaderConstants.AUTHORIZATION, expectedHeader);
	}
}

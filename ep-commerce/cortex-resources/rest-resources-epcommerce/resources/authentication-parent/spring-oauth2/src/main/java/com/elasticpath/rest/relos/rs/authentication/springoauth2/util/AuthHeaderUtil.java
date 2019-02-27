/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.authentication.springoauth2.util;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;

import com.google.common.base.Optional;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.rest.relos.rs.authentication.AuthHeaderConstants;
import com.elasticpath.rest.util.http.HeaderUtil;

/**
 * Util for Auth Header parsing.
 */
public final class AuthHeaderUtil {

	private AuthHeaderUtil() {
		//no-op
	}

	/**
	 * Parse the Auth header parameters. The parameters will be decoded.
	 *
	 * @param request The request.
	 * @return The parsed parameters, or null if no Auth authorization header was supplied.
	 */
	public static String getTokenFromRequest(final HttpServletRequest request) {
		Iterable<String> headers = HeaderUtil.getHeadersFromRequest(request, AuthHeaderConstants.AUTHORIZATION);
		Optional<String> authTokenValue = findParsedHeader(headers);

		if (!authTokenValue.isPresent()) {
			// Fall back to URI Parameter
			authTokenValue = Optional.fromNullable(
				request.getParameter(AuthHeaderConstants.AUTH_TOKEN_URI_PARAM));
		}

		return authTokenValue.orNull();
	}

	/**
	 * Parses auth token from headers.
	 *
	 * @param headers HttpHeaders.
	 * @return token.
	 */
	public static String parseToken(final HttpHeaders headers) {
		Optional<String> result = Optional.absent();
		List<String> requestHeaderValues = headers.getRequestHeader(AuthHeaderConstants.AUTHORIZATION);
		if (requestHeaderValues != null) {
			result = findParsedHeader(requestHeaderValues);
		}

		return result.orNull();
	}

	private static Optional<String> findParsedHeader(final Iterable<String> headers) {
		Iterable<String> parsedHeaders = Iterables.transform(headers, header -> {
			if (StringUtils.startsWithIgnoreCase(header, AuthHeaderConstants.AUTH_HEADER_PREFIX)) {
				String authHeaderValue = header.substring(AuthHeaderConstants.AUTH_HEADER_PREFIX.length());
				if (!authHeaderValue.contains("oauth_signature_method")) {
					return StringUtils.substringBefore(authHeaderValue, ",");
				}
			}
			return null;
		});
		return Iterables.tryFind(parsedHeaders, Predicates.notNull());
	}
}

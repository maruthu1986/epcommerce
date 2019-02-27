/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * Filter that is triggered for all the http requests.
 * The response HTML will be appended with X-Frame-Options.
 *
 * Option values that can be specified in plugin.xml include:
 * DENY, SAMEORIGIN, ALLOW-FROM https://example.com/
 */
public class XFrameOptionsHeaderFilter implements Filter {

	private static final String X_FRAME_OPTIONS = "X-Frame-Options"; //$NON-NLS-1$

	private static final String KEY = "x-frame-options-value";  //$NON-NLS-1$

	private String configuredOption;

	@Override
	public void init(final FilterConfig filterConfig) throws ServletException {
		configuredOption = filterConfig.getInitParameter(KEY);
	}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response,
		final FilterChain chain) throws IOException, ServletException {

		HttpServletResponse httpResponse = (HttpServletResponse) response;

		httpResponse.addHeader(X_FRAME_OPTIONS, configuredOption);

		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		//do nothing
	}
}

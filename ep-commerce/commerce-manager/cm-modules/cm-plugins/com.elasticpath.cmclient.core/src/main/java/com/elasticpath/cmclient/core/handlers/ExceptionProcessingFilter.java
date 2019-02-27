/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */

package com.elasticpath.cmclient.core.handlers;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.Logger;

/**
 * Filter that will catch all RuntimeExceptions for the reason of logging them with log4j.
 */
public class ExceptionProcessingFilter implements Filter {

	private static final Logger LOG = Logger.getLogger(ExceptionProcessingFilter.class);

	@Override
	public void init(final FilterConfig filterConfig) {
		// Do nothing
	}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response,
						 final FilterChain chain) throws IOException, ServletException {
		try {
			chain.doFilter(request, response);
		} catch (RuntimeException e) {
			LOG.error("Error occurred while processing request to Commerce Manager.", e);
			throw e;
		}
	}

	@Override
	public void destroy() {
		// Do nothing
	}
}

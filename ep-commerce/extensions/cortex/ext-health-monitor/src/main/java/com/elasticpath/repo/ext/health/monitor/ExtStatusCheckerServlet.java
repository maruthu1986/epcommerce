/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.repo.ext.health.monitor;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elasticpath.health.monitoring.servlet.StatusCheckerServlet;

/**
 * Extension servlet for overriding the default behaviour of StatusCheckerServlet.
 */
public class ExtStatusCheckerServlet extends StatusCheckerServlet {

	/**
	 * {@inheritDoc}
	 *
	 * @see com.elasticpath.health.monitoring.servlet.StatusCheckerServlet#doGet(HttpServletRequest, HttpServletResponse)
	 */
	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		ApplicationUrlHelper.extractAndSetHealthCheckUrl(request.getRequestURL().toString());
		super.doGet(request, response);
	}

}

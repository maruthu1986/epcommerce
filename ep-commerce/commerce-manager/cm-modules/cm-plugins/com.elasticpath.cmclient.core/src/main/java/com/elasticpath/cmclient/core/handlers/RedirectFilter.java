package com.elasticpath.cmclient.core.handlers;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.json.JSONObject;

/**
 * Filter that will check for new sessions and will construct
 * a json object to redirect the requests to the login page.
 */
public class RedirectFilter implements Filter {

	private static final Logger LOG = Logger.getLogger(RedirectFilter.class);

	@Override
	public void init(final FilterConfig filterConfig) throws ServletException {
		// Do nothing
	}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response,
						 final FilterChain chain) throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(httpRequest);

		if (isSessionTimeout(wrapper)) {
			try {
				constructRedirect(response, wrapper.getContextPath());
			} catch (IOException ioe) {
				LOG.error("Error constructing JSON response!", ioe); //$NON-NLS-1$
			}
		} else {
			chain.doFilter(request, response);
		}
	}

	private boolean isSessionTimeout(final HttpServletRequestWrapper wrapper) {
		return wrapper.getRequestedSessionId() != null && wrapper.getSession().isNew() && wrapper.getQueryString() != null;
	}

	@Override
	public void destroy() {
		//do nothing
	}

	/*
	 * Construct a JSON formatted response.
	 * See https://bugs.eclipse.org/bugs/show_bug.cgi?id=388249 for details on why.
	 */
	private void constructRedirect(final ServletResponse response, final String url) throws IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");

		JSONObject redirect = new JSONObject();
		redirect.put("redirect", url);

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("head", redirect);

		PrintWriter responseWriter = response.getWriter();
		responseWriter.print(jsonObject.toString());
	}

}

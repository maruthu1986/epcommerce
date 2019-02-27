/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.authentication.springoauth2.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import com.google.common.collect.Multimap;

import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.springframework.security.core.context.SecurityContextHolder;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.relos.rs.authentication.request.AddHeadersRequestWrapper;
import com.elasticpath.rest.relos.rs.authentication.request.ModifiedHeaderRequestWrapper;
import com.elasticpath.rest.relos.rs.authentication.springoauth2.dto.AccessTokenDto;
import com.elasticpath.rest.relos.rs.authentication.springoauth2.util.AuthHeaderUtil;
import com.elasticpath.rest.relos.rs.authentication.springoauth2.util.TokenValidator;
import com.elasticpath.rest.relos.rs.subject.SubjectHeaderConstants;

/**
 * Authenticate the user based on their header token. This will put the principal information into request headers to be picked up by another filter.
 */
@Component(property = {
		Constants.SERVICE_RANKING + ":Integer=10",
		"pattern=.*" }
)
public class OAuth2TokenAuthenticationFilter implements Filter {

	@Reference
	private TokenValidator tokenValidator;


	@Override
	public void init(final FilterConfig filterConfig) {
		//nothing to do
	}

	@Override
	public void destroy() {
		//nothing to do
	}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;

		//get the token header, get corresponding login headers.
		String tokenValue = AuthHeaderUtil.getTokenFromRequest(httpRequest);
		if (StringUtils.isNotEmpty(tokenValue)) {
			ExecutionResult<AccessTokenDto> validateTokenResult = tokenValidator.validateToken(tokenValue);

			if (validateTokenResult.isSuccessful()) {
				AccessTokenDto accessTokenDto = validateTokenResult.getData();
				Multimap<String, String> headers = ModifiedHeaderRequestWrapper.createHeaderMultimap();
				headers.put(SubjectHeaderConstants.USER_ID, accessTokenDto.getUserId());
				headers.putAll(SubjectHeaderConstants.USER_ROLES, accessTokenDto.getRoles());
				headers.put(SubjectHeaderConstants.USER_SCOPES, accessTokenDto.getScope());
				httpRequest = new AddHeadersRequestWrapper(httpRequest, headers);
			} else if (ResourceStatus.SERVER_ERROR == validateTokenResult.getResourceStatus()) {
				sendServerError((HttpServletResponse) response);
				return;
			}
		}

		chain.doFilter(httpRequest, response);
		SecurityContextHolder.getContext().setAuthentication(null);
	}

	private void sendServerError(final HttpServletResponse response) throws IOException {
		response.setContentType(MediaType.TEXT_PLAIN);
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		try (PrintWriter writer = response.getWriter()) {
			writer.print("Authentication service unavailable.");
		}
		//do not continue filter chain
		SecurityContextHolder.getContext().setAuthentication(null);
	}
}

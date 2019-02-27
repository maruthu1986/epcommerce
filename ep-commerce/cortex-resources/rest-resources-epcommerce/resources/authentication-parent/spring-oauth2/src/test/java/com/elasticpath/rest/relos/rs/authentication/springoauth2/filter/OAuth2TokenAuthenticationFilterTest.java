/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.authentication.springoauth2.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.relos.rs.authentication.AuthHeaderConstants;
import com.elasticpath.rest.relos.rs.authentication.springoauth2.dto.AccessTokenDto;
import com.elasticpath.rest.relos.rs.authentication.springoauth2.util.TokenValidator;
import com.elasticpath.rest.relos.rs.subject.SubjectHeaderConstants;

/**
 * Test class for {@link OAuth2TokenAuthenticationFilter}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class OAuth2TokenAuthenticationFilterTest {

	private static final int ONE_HOUR_MILLI_SECONDS = 3_600_000;
	private static final String USER_ROLE = "USER_ROLE";
	private static final String A_TOKEN = "ATOKEN";
	private static final String SCOPE = "SCOPE";
	private static final String INVALID_TOKEN = "INVALID_TOKEN";
	private static final String PROFILE_GUID = "PROFILE_GUID";

	@Mock
	private TokenValidator tokenValidator;

	@InjectMocks
	private OAuth2TokenAuthenticationFilter filter;


	private final MockHttpServletRequest request = new MockHttpServletRequest();
	private final MockHttpServletResponse response = new MockHttpServletResponse();
	private final MockFilterChain filterChain = new MockFilterChain();


	/**
	 * Test do filter with valid authentication token string.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ServletException the servlet exception
	 */
	@Test
	public void testDoFilterWithValidAuthenticationTokenString() throws IOException, ServletException {
		String tokenWithHeader = AuthHeaderConstants.AUTH_HEADER_PREFIX + A_TOKEN;
		request.addHeader(AuthHeaderConstants.AUTHORIZATION, tokenWithHeader);

		AccessTokenDto accessTokenDto = ResourceTypeFactory.createResourceEntity(AccessTokenDto.class)
				.setUserId(PROFILE_GUID)
				.setScope(SCOPE)
				.setRoles(Collections.singleton(USER_ROLE))
				.setExpiryDate(new Date(System.currentTimeMillis() + ONE_HOUR_MILLI_SECONDS));

		when(tokenValidator.validateToken(A_TOKEN)).thenReturn(ExecutionResultFactory.createReadOK(accessTokenDto));

		filter.doFilter(request, response, filterChain);

		HttpServletRequest chainRequest = (HttpServletRequest) filterChain.getRequest();

		assertEquals("User guid in header does not match expected value.", PROFILE_GUID, chainRequest.getHeader(SubjectHeaderConstants.USER_ID));
		assertEquals("Role in header does not match expected value.", USER_ROLE, chainRequest.getHeader(SubjectHeaderConstants.USER_ROLES));
		assertEquals("Scope in header does not match expected value.", SCOPE, chainRequest.getHeader(SubjectHeaderConstants.USER_SCOPES));
	}

	/**
	 * Test filter when token expired.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ServletException the servlet exception
	 */
	@Test
	public void testFilterWhenTokenExpired() throws IOException, ServletException {
		String tokenWithHeader = AuthHeaderConstants.AUTH_HEADER_PREFIX + A_TOKEN;
		request.addHeader(AuthHeaderConstants.AUTHORIZATION, tokenWithHeader);

		when(tokenValidator.validateToken(A_TOKEN)).thenReturn(ExecutionResultFactory.<AccessTokenDto>createBadRequestBody("Invalid token."));

		filter.doFilter(request, response, filterChain);

		HttpServletRequest chainRequest = (HttpServletRequest) filterChain.getRequest();

		assertNull("There should be no user guid header added.", chainRequest.getHeader(SubjectHeaderConstants.USER_ID));
		assertNull("There should be no user roles header added.", chainRequest.getHeader(SubjectHeaderConstants.USER_ROLES));
		assertNull("There should be no user scopes header added.", chainRequest.getHeader(SubjectHeaderConstants.USER_SCOPES));
	}

	/**
	 * Test do filter with no token string in header. <br>
	 * It should just flow through the filter without populating any authentication headers.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ServletException the servlet exception
	 */
	@Test
	public void testDoFilterWithNoTokenStringInHeader() throws IOException, ServletException {

		filter.doFilter(request, response, filterChain);

		HttpServletRequest chainRequest = (HttpServletRequest) filterChain.getRequest();

		assertNull("There should be no user guid header added.", chainRequest.getHeader(SubjectHeaderConstants.USER_ID));
		assertNull("There should be no user roles header added.", chainRequest.getHeader(SubjectHeaderConstants.USER_ROLES));
		assertNull("There should be no user scopes header added.", chainRequest.getHeader(SubjectHeaderConstants.USER_SCOPES));
	}

	/**
	 * Test do filter when token service returns null authentication. </br>
	 * It should just flow through the filter without populating any authentication headers.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ServletException the servlet exception
	 */
	@Test
	public void testDoFilterWhenTokenServiceReturnsNullAuthentication() throws IOException, ServletException {
		String tokenWithHeader = AuthHeaderConstants.AUTH_HEADER_PREFIX + INVALID_TOKEN;
		request.addHeader(AuthHeaderConstants.AUTHORIZATION, tokenWithHeader);


		when(tokenValidator.validateToken(INVALID_TOKEN)).thenReturn(ExecutionResultFactory.<AccessTokenDto>createNotFound());

		filter.doFilter(request, response, filterChain);

		HttpServletRequest chainRequest = (HttpServletRequest) filterChain.getRequest();

		assertNull("There should be no user guid header added.", chainRequest.getHeader(SubjectHeaderConstants.USER_ID));
		assertNull("There should be no user roles header added.", chainRequest.getHeader(SubjectHeaderConstants.USER_ROLES));
		assertNull("There should be no user scopes header added.", chainRequest.getHeader(SubjectHeaderConstants.USER_SCOPES));
	}
}

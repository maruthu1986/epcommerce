/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.authentication.springoauth2.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.relos.rs.authentication.client.AuthenticationClient;
import com.elasticpath.rest.relos.rs.authentication.dto.AuthenticationRequestDto;
import com.elasticpath.rest.relos.rs.authentication.dto.AuthenticationResponseDto;
import com.elasticpath.rest.relos.rs.authentication.springoauth2.OAuthAccessTokenService;
import com.elasticpath.rest.relos.rs.authentication.springoauth2.util.AuthHeaderUtil;
import com.elasticpath.rest.relos.rs.common.ResourceStatusToStatusType;
import com.elasticpath.rest.relos.rs.events.EventSender;
import com.elasticpath.rest.relos.rs.events.RoleTransitionEvent;
import com.elasticpath.rest.relos.rs.jaxrs.JaxRsResource;
import com.elasticpath.rest.relos.rs.subject.SubjectHeaderConstants;
import com.elasticpath.rest.util.collection.CollectionUtil;


/**
 * OAuth2Resource is the REST resource in charge of authentication.
 */
@Component
@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
@Path("/oauth2/tokens")
public final class OAuth2ResourceImpl implements JaxRsResource {

	private static final String PASSWORD_GRANT_TYPE = "password";
	private static final String ALLOW = "Allow";

	@Reference
	private OAuthAccessTokenService oAuthAccessTokenService;

	@Reference
	private AuthenticationClient authenticationClient;

	@Reference
	private EventSender eventSender;


	@Override
	public Collection<Object> getSingletons() {
		return Collections.singleton(this);
	}

	/**
	 * Creates an Subject Token for the user with the given credentials.
	 *
	 * @param username The user's username
	 * @param password The user's password
	 * @param role The user's role
	 * @param scope The user's scope
	 * @param grantType grantType
	 * @param uriInfo the uri info
	 * @param httpRequest the HTTP request
	 * @return a newly created authentication token
	 */
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response createToken(
			@FormParam("username")
			final String username,
			@FormParam("password")
			final String password,
			@FormParam("role")
			final String role,
			@FormParam("scope")
			final String scope,
			@FormParam("grant_type")
			final String grantType,
			@Context
			final UriInfo uriInfo,
			@Context
			final HttpServletRequest httpRequest) {

		ExecutionResultChain chain = new ExecutionResultChain() {
			@Override
			protected ExecutionResult<?> build() {
				Ensure.successful(validateGrantType(grantType));

				String userId = getHeaderValue(SubjectHeaderConstants.USER_ID, httpRequest);
				String roles = getHeaderValue(SubjectHeaderConstants.USER_ROLES, httpRequest);
				String scopes = getHeaderValue(SubjectHeaderConstants.USER_SCOPES, httpRequest);

				Map<String, String> userAuthHeaders = ImmutableMap.of(
						SubjectHeaderConstants.USER_ID, userId,
						SubjectHeaderConstants.USER_ROLES, roles,
						SubjectHeaderConstants.USER_SCOPES, scopes);

				AuthenticationRequestDto requestDto = createAuthenticationRequest(username, password, scope, role);
				AuthenticationResponseDto responseDto = Assign.ifSuccessful(authenticationClient.authenticate(requestDto, userAuthHeaders));

				OAuth2AccessToken token = executeCommand(() ->
					oAuthAccessTokenService.createOAuth2Token(responseDto.getId(), responseDto.getScope(), responseDto.getRoles()));

				String authenticatedRole = CollectionUtil.first(responseDto.getRoles());

				if (isRoleTransition(roles, authenticatedRole)) {
					RoleTransitionEvent eventEntity = createRoleTransitionEvent(userId, responseDto.getId(), roles, authenticatedRole);
					eventSender.sendEvent(eventEntity, scope);
				}

				CacheControl cacheControl = createCacheControl();
				Response result = Response.ok(token)
					.cacheControl(cacheControl)
					.type(MediaType.APPLICATION_JSON_TYPE)
					.build();

				return ExecutionResultFactory.createCreateOKWithData(result, false);
			}
		};

		ExecutionResult<Response> chainResult = chain.execute();

		if (chainResult.isSuccessful()) {
			return chainResult.getData();
		} else {
			return handleError(chainResult);
		}
	}

	private RoleTransitionEvent createRoleTransitionEvent(final String oldUserGuid,
															final String newUserGuid,
															final String oldRole,
															final String newRole) {
		RoleTransitionEvent eventEntity =
				ResourceTypeFactory.createResourceEntity(RoleTransitionEvent.class);

		eventEntity.setOldRole(oldRole)
				.setNewRole(newRole)
				.setOldUserGuid(oldUserGuid)
				.setNewUserGuid(newUserGuid);
		return eventEntity;
	}

	private boolean isRoleTransition(final String authHeaderUserRole, final String authResponseUserRole) {
		return !StringUtils.equals(authHeaderUserRole, authResponseUserRole);
	}

	private String getHeaderValue(final String headerKey, final HttpServletRequest servletRequest) {
		if (servletRequest.getHeader(headerKey) == null) {
			return StringUtils.EMPTY;
		}

		return servletRequest.getHeader(headerKey);
	}

	private CacheControl createCacheControl() {
		CacheControl cacheControl = new CacheControl();
		cacheControl.setNoStore(true);
		return cacheControl;
	}

	private AuthenticationRequestDto createAuthenticationRequest(
			final String username, final String password, final String scope, final String role) {
		return ResourceTypeFactory.createResourceEntity(AuthenticationRequestDto.class)
				.setUsername(username)
				.setPassword(password)
				.setScope(scope)
				.setRole(role);
	}

	/**
	 * Deletes an Subject Token for the current logged in user.
	 *
	 * @param headers HttpHeaders
	 * @return a newly created authentication token
	 */
	@DELETE
	public Response deleteToken(
			@Context
			final HttpHeaders headers) {

		Response result;

		String serializedToken = AuthHeaderUtil.parseToken(headers);
		ExecutionResult<Void> cmdResult = oAuthAccessTokenService.removeToken(serializedToken);

		if (cmdResult.isSuccessful()) {
			Response.StatusType status = ResourceStatusToStatusType.get(cmdResult.getResourceStatus());
			ResponseBuilder builder = Response.status(status);
			result = builder.build();
		} else {
			result = handleError(cmdResult);
		}

		return result;
	}

	/**
	 * @return Allow: OPTIONS,POST,DELETE
	 */
	@OPTIONS
	public Response options() {
		return Response.noContent()
				.header(ALLOW, "OPTIONS,POST,DELETE")
				.build();
	}

	//called inside an ExecutionResultChain wrapper so it's OK for these to throw exceptions.
	private ExecutionResult<Void> validateGrantType(final String grantType) {
		Ensure.isTrue(StringUtils.isNotEmpty(grantType),
				ExecutionResultFactory.createBadRequestBody("Required field grant_type is missing."));
		Ensure.isTrue(grantType.equals(PASSWORD_GRANT_TYPE),
				ExecutionResultFactory.createBadRequestBody("Grant type must be password."));
		return ExecutionResultFactory.createReadOK(null);
	}

	private Response handleError(final ExecutionResult<?> execResult) {
		Response.StatusType status = ResourceStatusToStatusType.get(execResult.getResourceStatus());

		return Response.status(status)
				.type(MediaType.TEXT_PLAIN_TYPE)
				.entity(execResult.getErrorMessage())
				.build();
	}
}

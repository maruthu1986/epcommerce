/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.service.impl;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.rcp.RemoteAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.security.CmAuthenticationManager;
import com.elasticpath.cmclient.core.security.UISessionSecurityContextHolderStrategy;
import com.elasticpath.cmclient.core.service.AuthenticationService;

/**
 * Handles remote authentication.
 *
 */
public final class AuthenticationServiceImpl implements AuthenticationService {
	private static final Logger LOG = Logger.getLogger(AuthenticationServiceImpl.class);

	private transient Authentication authToken;

	private AuthenticationServiceImpl() {
		//private constructor
	}

	/**
	 * Return instance of the singleton.
	 * @return instance of the singleton
	 */
	public static AuthenticationServiceImpl getInstance() {
		return CmSingletonUtil.getSessionInstance(AuthenticationServiceImpl.class);
	}

	/**
	 * Determine if the user has been authenticated.
	 * @return true if authenticated, false if not
	 */
	public boolean isAuthenticated() {
		if (authToken == null) {
			return false;
		}
		return this.authToken.isAuthenticated();
	}

	/**
	 * Attempt to authenticate a user against a remote server.
	 * @param username the userId
	 * @param password the password
	 */
	public void login(final String username, final String password) {
		authToken = new UsernamePasswordAuthenticationToken(username, password);

		CmAuthenticationManager cmAuthenticationManager = ServiceLocator.getService("cmAuthenticationManager");

		try {
			Collection<? extends GrantedAuthority> roles = cmAuthenticationManager.attemptAuthentication(username, password);

			authToken = new UsernamePasswordAuthenticationToken(username, password, roles);
			//Tell the security context holder to use a global security context (rather than threadlocal, for e.g.)
			//Set the global security context
			SecurityContextHolder.setStrategyName(UISessionSecurityContextHolderStrategy.class.getName()); //$NON-NLS-1$
			SecurityContextHolder.getContext().setAuthentication(authToken);
		} catch (RemoteAuthenticationException e) {
			LOG.warn("Authentication Failed", e); //$NON-NLS-1$
			throw e;
		} catch (RemoteAccessException e) {
			LOG.debug("RemoteAccessException", e); //$NON-NLS-1$
			throw e;
		}

		LOG.debug("Login succeeded"); //$NON-NLS-1$
	}
}

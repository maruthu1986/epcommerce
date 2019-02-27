/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.security;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.rcp.RemoteAuthenticationException;
import org.springframework.security.authentication.rcp.RemoteAuthenticationManagerImpl;
import org.springframework.security.core.GrantedAuthority;

/**
 * Server-side processor of a remote authentication request.
 * <P>
 * This bean requires no security interceptor to protect it. Instead, the bean uses the configured <code>CmAuthenticationManager</code> to resolve an
 * authentication request.
 * </p>
 */
public class CmAuthenticationManager extends RemoteAuthenticationManagerImpl {

	@Override
	public Collection<? extends GrantedAuthority> attemptAuthentication(final String username, final String password)
		throws RemoteAuthenticationException {

		final UsernamePasswordAuthenticationToken request = new UsernamePasswordAuthenticationToken(username, password);

		return getAuthenticationManager().authenticate(request).getAuthorities();
	}
}

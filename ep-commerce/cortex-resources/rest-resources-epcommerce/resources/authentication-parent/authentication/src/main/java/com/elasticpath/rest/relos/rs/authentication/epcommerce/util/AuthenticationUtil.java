/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.authentication.epcommerce.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.elasticpath.rest.identity.RolePrincipal;
import com.elasticpath.rest.util.collection.CollectionUtil;


/**
 * Utilities for use in authentication.
 */
public final class AuthenticationUtil {

	/*
	 * Delimiter char used to join the scope and username. The scope will be first,
	 * username is second.
	 */
	private static final char SCOPE_DELIM = '\u0007';

	private AuthenticationUtil() {
		//static class
	}

	/**
	 * Creates a collection of GrantedAuthorities from a collection of Principals.
	 *
	 * @param principals the collection of Principals
	 * @return a collection of GrantedAuthorities
	 */
	public static Collection<GrantedAuthority> createAuthorities(final Collection<RolePrincipal> principals) {
		final Collection<GrantedAuthority> authorities;
		if (CollectionUtil.isNotEmpty(principals)) {
			authorities = new ArrayList<>(principals.size());
			for (RolePrincipal principal : principals) {
				authorities.add(new SimpleGrantedAuthority(principal.getValue()));
			}
		} else {
			authorities = Collections.emptyList();
		}
		return authorities;
	}

	/**
	 * Split a combined scope and username string into a string array.
	 * array[0] is scope, array[1] is username.
	 *
	 * @param combined the combined scope and username string
	 * @return the parts in an array.
	 */
	public static String[] splitPrincipals(final String combined) {
		String[] parts = StringUtils.split(combined, SCOPE_DELIM);
		assert parts.length == 2 : "combined String of scope and username incorrectly delimited: " + combined;
		return parts;
	}

	/**
	 * Combines a scope and username into a single string.
	 *
	 * @param scope the scope
	 * @param username the username
	 * @return the combined string
	 */
	public static String combinePrincipals(final String scope, final String username) {
		return scope + SCOPE_DELIM + username;
	}
}

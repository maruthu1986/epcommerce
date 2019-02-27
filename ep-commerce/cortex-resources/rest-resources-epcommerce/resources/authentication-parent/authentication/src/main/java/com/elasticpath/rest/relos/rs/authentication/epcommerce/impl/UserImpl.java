/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.authentication.epcommerce.impl;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.elasticpath.rest.identity.RolePrincipal;
import com.elasticpath.rest.relos.rs.authentication.User;
import com.elasticpath.rest.relos.rs.authentication.epcommerce.util.AuthenticationUtil;

/**
 * Implements spring security's {@link UserDetails} and provides additional info.
 * <p/>
 * We must implement spring security's UserDetails so the object can be used by spring security.
 */
public class UserImpl implements User, UserDetails {

	private static final long serialVersionUID = 2L;

	private String userId;
	private Collection<RolePrincipal> principals;
	private String username;
	//passwords should be stored in memory as char[] is much as possible
	private char[] password;
	private boolean accountExpired;
	private boolean accountLocked;
	private boolean credentialsExpired;
	private boolean accountEnabled;
	private String requestedScope;
	private String salt;


	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		return AuthenticationUtil.createAuthorities(principals);
	}

	@Override
	public Collection<RolePrincipal> getPrincipals() {
		return principals;
	}

	@Override
	public String getPassword() {
		return new String(password);
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountExpired() {
		return accountExpired;
	}

	@Override
	public boolean isAccountNonExpired() {
		return !accountExpired;
	}

	@Override
	public boolean isAccountLocked() {
		return accountLocked;
	}

	@Override
	public boolean isAccountNonLocked() {
		return !accountLocked;
	}

	@Override
	public boolean isCredentialsExpired() {
		return credentialsExpired;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return !credentialsExpired;
	}

	@Override
	public boolean isAccountEnabled() {
		return accountEnabled;
	}

	@Override
	public boolean isEnabled() {
		return accountEnabled;
	}

	@Override
	public String getRequestedScope() {
		return requestedScope;
	}

	@Override
	public User setPrincipals(final Collection<RolePrincipal> principals) {
		this.principals = principals;
		return this;
	}

	@Override
	public User setUsername(final String username) {
		this.username = username;
		return this;
	}

	@Override
	public User setPassword(final String password) {
		if (password != null) {
			this.password = password.toCharArray().clone();
		}
		return this;
	}

	@Override
	public User setAccountExpired(final boolean accountExpired) {
		this.accountExpired = accountExpired;
		return this;
	}

	@Override
	public User setAccountLocked(final boolean accountLocked) {
		this.accountLocked = accountLocked;
		return this;
	}

	@Override
	public User setCredentialsExpired(final boolean credentialsExpired) {
		this.credentialsExpired = credentialsExpired;
		return this;
	}

	@Override
	public User setAccountEnabled(final boolean enabled) {
		accountEnabled = enabled;
		return this;
	}

	@Override
	public User setRequestedScope(final String requestedScope) {
		this.requestedScope = requestedScope;
		return this;
	}

	@Override
	public User setUserId(final String userId) {
		this.userId = userId;
		return this;
	}

	@Override
	public String getUserId() {
		return userId;
	}

	@Override
	public String getSalt() {
		return salt;
	}

	@Override
	public User setSalt(final String salt) {
		this.salt = salt;
		return this;
	}
}

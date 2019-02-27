/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.authentication.epcommerce.impl;

import java.util.Arrays;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.elasticpath.rest.relos.rs.authentication.User;

/**
 * Provides an {@link AuthenticationManager} that calls CE.
 */
@Component
public class EpAuthenticationManager implements AuthenticationManager {

	private static final int STRENGTH_256 = 256;

	@Reference
	private UserDetailsService userDetailsService;

	@Reference
	private MessageSource globalMessageSource;

	private AuthenticationManager authenticationManager;


	/**
	 * Activates the AuthenticationManager by constructing one that matches EP's mechanisms.
	 */
	@Activate
	protected void activate() {
		ShaPasswordEncoder sha256PasswordEncoder = new ShaPasswordEncoder(STRENGTH_256);
		//Our UserDetailsService produces Users
		SaltSource saltSource = user -> ((User) user).getSalt();
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setPasswordEncoder(sha256PasswordEncoder);
		daoAuthenticationProvider.setUserDetailsService(userDetailsService);
		daoAuthenticationProvider.setSaltSource(saltSource);
		daoAuthenticationProvider.setMessageSource(globalMessageSource);

		ShaPasswordEncoder sha1PasswordEncoder = new ShaPasswordEncoder();
		DaoAuthenticationProvider fallbackDaoAuthenticationProvider = new DaoAuthenticationProvider();
		fallbackDaoAuthenticationProvider.setPasswordEncoder(sha1PasswordEncoder);
		fallbackDaoAuthenticationProvider.setUserDetailsService(userDetailsService);
		fallbackDaoAuthenticationProvider.setMessageSource(globalMessageSource);

		authenticationManager = new ProviderManager(Arrays.asList(daoAuthenticationProvider, fallbackDaoAuthenticationProvider));
	}

	@Override
	public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
		return authenticationManager.authenticate(authentication);
	}
}

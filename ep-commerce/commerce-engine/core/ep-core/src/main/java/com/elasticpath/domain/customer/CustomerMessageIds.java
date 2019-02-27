/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.domain.customer;

/**
 * Collection of StructuredErrorMessage message ids for the customer profile domain.
 * These message ids should be used for localization of error messages on client side.
 */
public final class CustomerMessageIds {
	/**
	 * User id already exists error where email address is already defined.
	 */
	public static final String EMAIL_ALREADY_EXISTS = "profile.email.already.exists";
	/**
	 * User id already exists error where user id is already defined.
	 */
	public static final String USERID_ALREADY_EXISTS = "profile.userid.already.exists";

	private CustomerMessageIds() {
		// private constructor to ensure class can't be instantiated
	}
}

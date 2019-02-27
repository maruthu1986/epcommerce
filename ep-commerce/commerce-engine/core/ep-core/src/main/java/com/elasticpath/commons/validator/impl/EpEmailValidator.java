/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.commons.validator.impl;

import org.apache.commons.validator.EmailValidator;

/**
 * <code>EpEmailValidator</code> currently uses {@link EmailValidator}.
 *
 * @deprecated whereever possible use directly the {@link EmailValidator} from the commons package
 */
@Deprecated
public class EpEmailValidator {

	/**
	 * Singleton instance of this class.
	 */
	private static final EpEmailValidator EP_EMAIL_VALIDATOR = new EpEmailValidator();

	/**
	 * Protected constructor for subclasses to use.
	 */
	protected EpEmailValidator() {
		super();
	}

	/**
	 * Returns the Singleton instance of this validator.
	 * @return singleton instance of this validator.
	 */
	public static EpEmailValidator getInstance() {
		return EP_EMAIL_VALIDATOR;
	}

	/**
	 * <p>Checks if a field has a valid e-mail address.</p>
	 *
	 * @param email The value that validation is being performed on.  A <code>null</code>
	 * value is considered invalid.
	 * @return true if the email address is valid.
	 */
	public boolean isValid(final String email) {
		return EmailValidator.getInstance().isValid(email);
	}

}

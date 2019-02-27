/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.email.handler.cmuser.helper;

import java.util.Locale;

import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.email.domain.EmailProperties;

/**
 * Helper for constructing email properties.
 */
public interface CmUserEmailPropertyHelper {

	/**
	 * Returns email properties for create password.
	 *
	 * @param cmUser the {@link CmUser}
	 * @param newPassword the new password
	 * @param locale the locale of the email messages
	 * @return {@link EmailProperties}
	 */
	EmailProperties getCreateEmailProperties(CmUser cmUser, String newPassword, Locale locale);

	/**
	 * Returns email properties for change password.
	 *
	 * @param cmUser the {@link CmUser}
	 * @param newPassword the new password
	 * @param locale the locale of the email messages
	 * @return {@link EmailProperties}
	 */
	EmailProperties getChangePasswordEmailProperties(CmUser cmUser, String newPassword, Locale locale);

	/**
	 * Returns email properties for reset password.
	 *
	 * @param cmUser the {@link CmUser}
	 * @param newPassword the new password
	 * @param locale the locale of the email messages
	 * @return {@link EmailProperties}
	 */
	EmailProperties getResetEmailProperties(CmUser cmUser, String newPassword, Locale locale);

}

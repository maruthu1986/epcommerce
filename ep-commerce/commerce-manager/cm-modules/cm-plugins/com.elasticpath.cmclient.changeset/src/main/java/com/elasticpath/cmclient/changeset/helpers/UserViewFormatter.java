/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.changeset.helpers;

import com.elasticpath.domain.changeset.ChangeSetUserView;
import com.elasticpath.domain.cmuser.CmUser;

/**
 * A utility class for formatting a CmUser into a String.
 */
public final class UserViewFormatter {

	/**
	 * Constructor.
	 */
	private UserViewFormatter() {
		super();
	}

	/**
	 * Formats a user by concatenating their name with their user name.
	 *   
	 * @param user the user to use
	 * @return the formatted name
	 */
	public static String formatWithNameAndUserName(final ChangeSetUserView user) {
		final StringBuilder formattedName = new StringBuilder(formatWithName(user));			
		formattedName.append(" ("); //$NON-NLS-1$
		formattedName.append(user.getUserName());
		formattedName.append(')'); //$NON-NLS-1$
		return formattedName.toString();
	}
	
	/**
	 * Formats a user by appending the last and first names.
	 * 
	 * @param user the user
	 * @return a formatter string
	 */
	public static String formatWithName(final ChangeSetUserView user) {
		final StringBuilder formattedName = new StringBuilder();
		formattedName.append(user.getLastName());
		formattedName.append(", "); //$NON-NLS-1$
		formattedName.append(user.getFirstName());
		return formattedName.toString();
	}
	
	/**
	 * Formats a user by concatenating their name with their user name.
	 *   
	 * @param user the user to use
	 * @return the formatted name
	 */
	public static String formatWithNameAndUserName(final CmUser user) {
		final StringBuilder formattedName = new StringBuilder(formatWithName(user));			
		formattedName.append(" ("); //$NON-NLS-1$
		formattedName.append(user.getUserName());
		formattedName.append(')'); //$NON-NLS-1$
		return formattedName.toString();
	}

	/**
	 * Formats a user by appending the last and first names.
	 * 
	 * @param user the user
	 * @return a formatter string
	 */
	public static String formatWithName(final CmUser user) {
		final StringBuilder formattedName = new StringBuilder();
		formattedName.append(user.getLastName());
		formattedName.append(", "); //$NON-NLS-1$
		formattedName.append(user.getFirstName());
		return formattedName.toString();
	}

}

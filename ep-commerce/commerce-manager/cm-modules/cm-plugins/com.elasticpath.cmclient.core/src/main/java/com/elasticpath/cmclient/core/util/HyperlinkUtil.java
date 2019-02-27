/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.util;

/**
 * Util class for handling hyperlinks.
 *
 */
public final class HyperlinkUtil {

	private HyperlinkUtil() {
		//private constructor
	}

	/**
	 * Opens a default email client with "To" field preset to given email address.
	 *
	 * @param email the email address to send an email to.
	 */
	public static void openEmailHyperLink(final String email) {
		ServiceUtil.getUrlLauncherService().openURL("mailto:" + email);
	}
}

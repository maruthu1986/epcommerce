/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.util;

import org.eclipse.rap.rwt.RWT;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Manages Web Cookies. This must be used within the UI Thread.
 */
public final class CookieUtil {

	private CookieUtil() {
		//private constructor
	}

	/**
	 * Get cookie value.
	 *
	 * @param cookieName the cookie name
	 * @return the cookie value or null
	 */
	public static String getCookieValue(final String cookieName) {
		HttpServletRequest request = RWT.getRequest();
		Cookie[] cookies = request.getCookies();
		Cookie result = null;
		if (cookies != null) {
			for (int i = 0; result == null && i < cookies.length; i++) {
				if (cookieName.equals(cookies[i].getName())) {
					result = cookies[i];
				}
			}
		}

		return result == null ? null : result.getValue();
	}

	/**
	 * Set the cookie value.
	 *
	 * @param cookieName  the cookie name
	 * @param cookieValue the cookieValue
	 */
	public static void setCookie(final String cookieName, final String cookieValue) {
		Cookie cookie = new Cookie(cookieName, cookieValue);
		cookie.setMaxAge(-1);
		if (RWT.getRequest().isSecure()) {
			cookie.setSecure(true);
		} else {
			cookie.setHttpOnly(true);
		}
		cookie.setPath(RWT.getRequest().getContextPath());
		RWT.getResponse().addCookie(cookie);
	}

}

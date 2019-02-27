/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.SingletonUtil;

/**
 * CM wrapper for {@link SingletonUtil}.
 */
public final class CmSingletonUtil {

	private CmSingletonUtil() {
		//private constructor
	}
	/**
	 * Gets a session singleton.
	 * @param type the class type
	 * @param <T> The generic type
	 * @return A Session instance of the class.
	 */
	public static <T> T getSessionInstance(final Class<T> type) {
		return SingletonUtil.getSessionInstance(type);
	}

	/**
	 * Gets an instance which is shared between the application sessions.
	 * @param type The class type.
	 * @param <T> The generic type.
	 * @return An application instance, that is shared between sessions.
	 */
	public static <T> T getApplicationInstance(final Class<T> type) {
		return SingletonUtil.getUniqueInstance(type, RWT.getApplicationContext());
	}
}

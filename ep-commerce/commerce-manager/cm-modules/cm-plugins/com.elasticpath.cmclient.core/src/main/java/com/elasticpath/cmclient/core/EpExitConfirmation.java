/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core;

import org.eclipse.rap.rwt.client.service.ExitConfirmation;
import org.eclipse.rap.rwt.client.service.JavaScriptExecutor;

import com.elasticpath.cmclient.core.util.ServiceUtil;

/**
 * This class provides a service which displays a warning about leaving the site.
 * This service includes the following: attempt to use the back or forward buttons, refresh, close browser, or enter new url.
 */
public final class EpExitConfirmation {

	private static final String JS_RELOAD = "window.location.reload(true);"; //$NON-NLS-1$

	private static ThreadLocal<ExitConfirmation> service =
		ThreadLocal.withInitial(() -> ServiceUtil.getRWTService(ExitConfirmation.class));

	private EpExitConfirmation() {
		//empty
	}

	/**
	 * Enable popup for the exiting, refreshing or closing the window.
	 */
	public static void enableService() {
		service.get().setMessage(CoreMessages.get().ApplicationExit_Warning_Msg);
	}

	/**
	 * Disable exit confirmation popup.
	 */
	public static void disableService() {
		service.get().setMessage(null);
	}

	/**
	 * Perform force reloading without popup message.
	 */
	public static void forceReload() {
		//Allow reloading the page
		disableService();

		final JavaScriptExecutor executor = ServiceUtil.getRWTService(JavaScriptExecutor.class);
		if (executor != null) {
			executor.execute(JS_RELOAD);
		}
	}
}

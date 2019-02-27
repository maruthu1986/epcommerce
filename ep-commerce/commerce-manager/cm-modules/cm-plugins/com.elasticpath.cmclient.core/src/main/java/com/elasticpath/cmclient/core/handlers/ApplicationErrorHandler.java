/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.handlers;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.CorePlugin;

/**
 * Global handler for system errors.
 */
public final class ApplicationErrorHandler {

	private ApplicationErrorHandler() {
		//private constructor
	}

	/**
	 *Creates ErrorDialog with stack trace in details.
	 *
	 * @param throwable throwable exception
	 */
	public static void createErrorDialogForException(final Throwable throwable) {
		Status status = new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, CoreMessages.get().SystemErrorOccured, throwable);
		ErrorDialog.openError(new Shell(Display.getCurrent()), CoreMessages.get().SystemErrorTitle, null, status);
	}
}

/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.ui.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;

/**
 * Utility class for RAP widgets.
 * The following methods are not present in RAP widget utilities.
 * Most common methods that deal with widgets should be placed in this class.
 */
public final class EpWidgetUtil {

	private EpWidgetUtil() {
		//utility class
	}


	/**
	 * Disposes control if it is not yet disposed and is not null.
	 *
	 * @param control control to be disposed
	 */
	public static void safeDispose(final Control control) {
		if (control != null && !control.isDisposed()) {
			control.dispose();
		}
	}

	/**
	 * Compute the width of the control.
	 *
	 * @param control the control
	 * @return the width
	 */
	public static int computeWidth(final Control control) {
		return control.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x;
	}

}

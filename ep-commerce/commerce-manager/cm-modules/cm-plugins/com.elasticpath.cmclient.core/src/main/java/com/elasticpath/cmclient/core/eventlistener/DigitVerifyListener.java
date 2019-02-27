/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.eventlistener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;

/**
 * The class to verify the input is digit.
 */
public class DigitVerifyListener implements VerifyListener {
	@Override
	@SuppressWarnings({ "PMD.MissingBreakInSwitch" })
	public void verifyText(final VerifyEvent event) {
		switch (event.keyCode) {
			case SWT.BS:           // Backspace
			case SWT.DEL:          // Delete
			case SWT.HOME:         // Home
			case SWT.END:          // End
			case SWT.ARROW_LEFT:   // Left arrow
			case SWT.ARROW_RIGHT:  // Right arrow
			case SWT.NONE:		   // Return ?
				return;
			default: 			   //nothing to do, for PMD only
		}
		if (!Character.isDigit(event.character)) {
			event.doit = false;  // disallow the action
		}
	}
}

/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core;

import org.junit.Assert;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * This class is used as an alternative to the PlatformUI.createDisplay().
 * <p>
 * Display that will be returned solves rare `Widget is disposed error` caused by saveFocus.
 * For example: CSV import for the PriceList would fail because of this issue.
 * Note: the Job class creates shell internally. So we have no direct control over it. This bug is specific to the RAP
 * <p>
 * For more information see: Bugzilla Bug 517750. (https://bugs.eclipse.org/bugs/show_bug.cgi?id=517750)
 */
final class DisplayCreator {

	/**
	 * Constructor.
	 */
	private DisplayCreator() {
		//empty
	}

	/**
	 * Creates a new Display.
	 * Note! It must be called only once at the start.
	 *
	 * @return display
	 */
	static Display createDisplay() {

		//No display is attached to the current session.
		Assert.assertTrue(Display.getCurrent() == null);

		//Calling constructor registers this display withing this Session
		//as a result in future getCurrent should return instance of this display
		Display display = new EpDisplay();

		//Complete normal creation (previously created display will be reused as it relies on Display.getCurrent())
		Display platformDisplay = PlatformUI.createDisplay();

		Assert.assertTrue(display.equals(platformDisplay));

		return display;
	}

	/**
	 * Display that has a work around RAP specific bug.
	 * The issue why this class was created: internal Job Process Dialog would give a widget dispose error
	 * just when it will perform an attempt to getFocusControl which might have been disposed.
	 * <p>
	 * Null value is handled anyway so it is save to return null as a default value when focusControl is disposed
	 */
	private static class EpDisplay extends Display {
		@Override
		public Control getFocusControl() {
			Control focusControl = super.getFocusControl();

			if (focusControl != null && focusControl.isDisposed()) {
				return null;
			}
			return focusControl;
		}
	}
}

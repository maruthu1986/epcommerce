/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.presentation;

import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.ui.internal.WorkbenchWindow;

/**
 * A window within the workbench.
 * This class gives control over the coolbar.
 */
public class EpWorkbenchWindow extends WorkbenchWindow {


	/**
	 * Constructor.
	 *
	 * @param number the number for the window
	 */
	public EpWorkbenchWindow(final int number) {
		super(number);
	}

	@Override
	protected ICoolBarManager createCoolBarManager2(final int style) {
		return new SimplifiedCoolbarManager();
	}
}
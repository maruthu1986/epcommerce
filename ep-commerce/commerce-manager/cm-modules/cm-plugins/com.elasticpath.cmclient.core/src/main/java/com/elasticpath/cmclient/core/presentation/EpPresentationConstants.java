/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.presentation;

/**
 * Utility class that contains constants used in presentation package.
 */
public final class EpPresentationConstants {

	/**
	 * Custom toolbar path.
	 * It is used to specify items that will be positioned on the right side of the header area.
	 * <p>
	 * NOTE: DO NOT use MenuUtil.TRIM_COMMAND2 aka toolbar:org.eclipse.ui.trim.command2
	 * <p>
	 * Usage of this path resolves the bug in RAP: Bug 471326. https://bugs.eclipse.org/bugs/show_bug.cgi?id=471326
	 * <p>
	 */
	public static final String TOOLBAR_RIGHT = "toolbar:com.elasticpath.cmclient.core.toolbars.right"; //$NON-NLS-1$


	private EpPresentationConstants() {
		//empty constructor
	}
}

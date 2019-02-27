/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Provides resources such as colors and fonts.
 */
public final class CmClientResources {

	/**
	 * RED.
	 */
	public static final String COLOR_RED = "RED"; //$NON-NLS-1$

	/**
	 * RED.
	 */
	public static final String COLOR_FIELD_ERROR = "FIELD_ERROR_COLOR"; //$NON-NLS-1$

	/**
	 * GREEN.
	 */
	public static final String COLOR_GREEN = "GREEN"; //$NON-NLS-1$

	/**
	 * BLUE.
	 */
	public static final String COLOR_BLUE = "BLUE"; //$NON-NLS-1$

	/**
	 * WHITE.
	 */
	public static final String COLOR_WHITE = "WHITE"; //$NON-NLS-1$

	/**
	 * GREY.
	 */
	public static final String COLOR_GREY = "TABLE_GREY"; //$NON-NLS-1$

	/* Colors */
	private static final RGB RGB_RED = new RGB(255, 0, 0);
	private static final RGB RGB_FIELD_ERROR = new RGB(255, 179, 179);
	private static final RGB RGB_GREEN = new RGB(0, 128, 0);
	private static final RGB RGB_BLUE = new RGB(0, 0, 255);
	private static final RGB RGB_WHITE = new RGB(255, 255, 255);
	private static final RGB RGB_GREY = new RGB(240, 240, 240);


	// Initialize the registry with the colors and fonts needed by the plugins
	static {
		JFaceResources.getColorRegistry().put(CmClientResources.COLOR_RED, RGB_RED);
		JFaceResources.getColorRegistry().put(CmClientResources.COLOR_FIELD_ERROR, RGB_FIELD_ERROR);
		JFaceResources.getColorRegistry().put(CmClientResources.COLOR_GREEN, RGB_GREEN);
		JFaceResources.getColorRegistry().put(CmClientResources.COLOR_BLUE, RGB_BLUE);
		JFaceResources.getColorRegistry().put(CmClientResources.COLOR_WHITE, RGB_WHITE);
		JFaceResources.getColorRegistry().put(CmClientResources.COLOR_GREY, RGB_GREY);
	}

	/**
	 * Constructor.
	 */
	private CmClientResources() {
		// empty
	}

	/**
	 * Gets the color.
	 *
	 * @param colorId this is a constant from <code>CmClientResources</code>
	 * @return <code>Color</code>
	 */
	public static Color getColor(final String colorId) {
		return JFaceResources.getColorRegistry().get(colorId);
	}

	/**
	 * Get the EP Background Color.
	 *
	 * @return the EP background color
	 */
	public static Color getBackgroundColor() {
		return Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
	}

	/**
	 * Get the EP Foreground Color.
	 *
	 * @return the EP foreground color
	 */
	public static Color getForegroundColor() {
		return Display.getCurrent().getSystemColor(SWT.COLOR_LIST_FOREGROUND);
	}

	/**
	 * Get the EP Inactive Foreground Color.
	 *
	 * @return the EP color
	 */
	public static Color getInactiveForegroundColor() {
		return Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY);
	}
}

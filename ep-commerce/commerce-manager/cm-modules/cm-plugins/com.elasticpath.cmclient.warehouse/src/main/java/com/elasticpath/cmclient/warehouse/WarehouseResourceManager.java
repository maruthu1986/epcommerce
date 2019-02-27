/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.warehouse;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Resource manager for fulfilment.
 */
public final class WarehouseResourceManager {
	private static final int MAXRGB = 255;
	private static final int SEMIRGB = 128;
	private static final int MINRGB = 0;

	/** Red color id. */
	public static final int RED = 0;
	/** Pink color id. */
	public static final int PINK = 1;
	/** White color id. */
	public static final int WHITE = -2;
	
	private static Map<Integer, Color> colorMap = new HashMap<>();

	static {
		colorMap.put(RED, new Color(Display.getCurrent(), new RGB(MAXRGB, MINRGB, MINRGB)));
		colorMap.put(PINK, new Color(Display.getCurrent(), new RGB(MAXRGB, SEMIRGB, SEMIRGB)));
		colorMap.put(WHITE, new Color(Display.getCurrent(), new RGB(MAXRGB, MAXRGB, MAXRGB)));
		
	}

	private WarehouseResourceManager() {

	}

	/**
	 * Returns Color by identifier.
	 * 
	 * @param key
	 *            identifier.
	 * @return color
	 */
	public static Color getColor(final int key) {
		return colorMap.get(key);
	}

}

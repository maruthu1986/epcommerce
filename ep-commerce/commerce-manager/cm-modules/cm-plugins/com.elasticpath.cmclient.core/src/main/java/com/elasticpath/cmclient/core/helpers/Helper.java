/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.helpers;

import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.TableWrapData;

/**
 * Helper class for miscellaneous useful UI operations.
 */
public final class Helper {
	private Helper() {
		//Necessary to silent PMD.
	}
	
	/**
	 * Fixes the text width and height so to restrict its endless growth.
	 * 
	 * @param text the text to fix
	 * @param width the max width
	 * @param height the wanted height
	 */
	public static void fixText(final Text text, final int width, final int height) {
		TableWrapData noteLayoutData = new TableWrapData(TableWrapData.LEFT, TableWrapData.FILL_GRAB);
		noteLayoutData.heightHint = height;
		noteLayoutData.grabHorizontal = true;

		GC graphicsContext = new GC(text);
		FontMetrics fontMetrics = graphicsContext.getFontMetrics();

		noteLayoutData.maxWidth = text.computeSize(width * fontMetrics.getAverageCharWidth(), fontMetrics.getHeight()).x;

		graphicsContext.dispose();

		text.setLayoutData(noteLayoutData);
	}
}

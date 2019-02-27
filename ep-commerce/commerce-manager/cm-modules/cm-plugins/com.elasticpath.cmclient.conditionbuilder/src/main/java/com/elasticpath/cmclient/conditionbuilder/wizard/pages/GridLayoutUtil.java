/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.conditionbuilder.wizard.pages;


import org.eclipse.swt.layout.GridLayout;

/**
 * Grid layout utility class.
 */
public final class GridLayoutUtil {
	/**
	 * Create a grid layout.
	 * @param numberOfColumns number of columns in grid layout. 
	 * @return instance of GridLayout. 
	 */
	public static GridLayout getBorderlessLayout(final int numberOfColumns) {
		GridLayout layout = new GridLayout();
		layout.numColumns = numberOfColumns;
		layout.makeColumnsEqualWidth = false;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		return layout;
	}
	
	private GridLayoutUtil() {
		
	}


}

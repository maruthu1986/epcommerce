/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.ui.framework;

/**
 * Interface representing the layout data used for setting the UI components placement inside the layout grid.
 */
public interface IEpLayoutData {

	/**
	 * Takes all the space available inside the cell where the component is. Used for both horizontal and vertical alignment.
	 */
	int FILL = 1;

	/**
	 * The component goes to the right or bottom of the cell. Used for both horizontal and vertical alignment.
	 */
	int END = 2;

	/**
	 * The component goes to the left or top of the cell depending on the align type (horizontal/vertical). Used for both horizontal and vertical
	 * alignment.
	 */
	int BEGINNING = 3;

	/**
	 * The component is centered horizontally or vertically. Used for both horizontal and vertical alignment.
	 */
	int CENTER = 4;

	/**
	 * Gets the native SWT layout data, adapted by the parent EP layout composite.<br>
	 * Basically it creates new layout data each time the method is called. If changed, the 
	 * data should be applied to the widget using <code>setLayoutData(Object data)</code>.
	 * 
	 * @return layout data object - <code>GridData</code> or <code>TableWrapData</code>
	 */
	Object getSwtLayoutData();

}

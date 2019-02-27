/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.dialog.value.support;

/**
 * Label provider for dialog values.
 */
public interface DialogValueLabelProvider {
	
	/**
	 * get the label for this value. if returns <code>null</code> no label will 
	 * be added to the ui.
	 * @return the text for label
	 */
	String getLabelText();
	
	/**
	 * get the style of label.
	 * @return true is label needs to be bold, false otherwise
	 */
	boolean isLabelBold();
}

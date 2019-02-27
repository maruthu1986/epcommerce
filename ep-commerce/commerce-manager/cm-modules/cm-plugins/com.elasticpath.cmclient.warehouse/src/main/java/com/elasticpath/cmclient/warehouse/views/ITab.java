/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.warehouse.views;

/**
 * Generic interface for tab in search view.
 */
@SuppressWarnings("PMD.ShortClassName")
public interface ITab {
	/**
	 * Called when tab is activated.
	 */
	void tabActivated();

	/**
	 * Sets the focus.
	 */
	void setFocus();
	
	/**
	 * Refreshes the tab.
	 */
	void refresh();
}

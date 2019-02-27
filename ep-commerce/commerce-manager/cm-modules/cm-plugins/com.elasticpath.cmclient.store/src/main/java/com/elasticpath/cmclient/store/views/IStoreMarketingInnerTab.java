/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.views;

/**
 * Interface to represent an inner tab.
 */
public interface IStoreMarketingInnerTab {

	/**
	 * Search the results.
	 */
	void search();

	/**
	 * Clear the results.
	 */
	void clear();

	/**
	 * Set the flag that controls the display of the search button.
	 *
	 * @return the is display button flag
	 */
	boolean isDisplaySearchButton();

	/**
	 * Get the tab index.
	 *
	 * @return the tab index
	 */
	int getTabIndex();
}

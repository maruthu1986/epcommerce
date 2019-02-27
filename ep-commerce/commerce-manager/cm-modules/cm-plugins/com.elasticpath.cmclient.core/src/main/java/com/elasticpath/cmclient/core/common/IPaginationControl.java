/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.common;

/**
 * This is the pagination control interface.
 */
public interface IPaginationControl {

	/**
	 * Create view part control.
	 */
	void createViewPartControl();

	/**
	 * Update the navigation components.
	 */
	void updateNavigationComponents();

	/**
	 * Get the current page.
	 *
	 * @return the current page
	 */
	int getCurrentPage();

	/**
	 * Get the valid page number.
	 * The page number input should be in the range of minimun and maximun page of the search result.
	 *
	 * @param pageNumber the page number input
	 * @return the valid page number
	 */
	int getValidPage(int pageNumber);

	/**
	 * navigate to the specific page.
	 *
	 * @param toPage the page number
	 */
	void navigateTo(int toPage);
}

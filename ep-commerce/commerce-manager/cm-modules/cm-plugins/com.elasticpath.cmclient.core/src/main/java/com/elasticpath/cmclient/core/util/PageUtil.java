/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.util;

/**
 * 
 * The util class for pagination.
 *
 */
public final class PageUtil {
	
	/**
	 * Get the page by the item index and items of each page.
	 * 
	 * @param itemIndex the index of the item (start from 1)
	 * @param numberOfEachPage the number of the items in each page
	 * @return the page number
	 */
	public static int getPage(final int itemIndex, final int numberOfEachPage) {
		int page = itemIndex / numberOfEachPage;
		if (itemIndex % numberOfEachPage == 0) {
			return page;
		}
		return page + 1;
	}
	
	private PageUtil() {
		
	}
}
